package com.pagarplus.app.modules.attedence.ui

import android.Manifest
import android.R.attr.thumbnail
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.databinding.ActivityAttedenceBinding

import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.attedence.data.viewmodel.AttedenceVM
import com.pagarplus.app.modules.attendance_details.ui.AttendanceDetailsActivity
import com.pagarplus.app.modules.expensereport.ui.ExpenseReportActivity
import com.pagarplus.app.network.models.attendance.AttendanceStatus
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.models.attendance.SaveAttendanceDataRequest
import com.pagarplus.app.network.models.attendance.SaveAttendanceDataResponse
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

public class AttendanceActivity :
    BaseActivity<ActivityAttedenceBinding>(R.layout.activity_attedence), AdapterView.OnItemSelectedListener, KoinComponent
{
    private val viewModel: AttedenceVM by viewModels<AttedenceVM>()
    var currentweekday = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    var currentdate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    val pref: PreferenceHelper by inject()
    private var currentsession: FeaturesTypes? = null
    private var currentvisitLocation: FeaturesTypes? = null
    private var mImageUri:Uri?=null
    private lateinit var mImageBytes:ByteArray
    private var imageFile:File?=null
    private var mLongitude=""
    private var mLatitude=""
    private var mCurrentLocation=""
    private var checkinLocation=""
    private var checkoutLocation=""
    private var mImageUrl:String?=""
    private var mISLogin=false
    var userdetails=pref.getLoginDetails<LoginResponse>()!!
    private var visitlist= arrayListOf<FeaturesTypes>() //pref.getVisitTypes<ArrayList<FeaturesTypes>>()?: arrayListOf()
    private var sessionlist=arrayListOf<FeaturesTypes>() //pref.getSessionTypes<ArrayList<FeaturesTypes>>()?: arrayListOf()
//    private var mLogData:AttendanceData?=null
    private var mLogStatus:AttendanceStatus?=null

    public override fun onInitialized(): Unit {
        lifecycleScope.launchWhenCreated{
           // viewModel.progressLiveData.postValue(true)
             visitlist = ApiUtil(applicationContext).getFeatureTypes(URLParameters.Visit)
          //   visitlist = ApiUtil(applicationContext).getVisitTypes(userdetails.userID?:0)
            sessionlist = ApiUtil(applicationContext).getFeatureTypes(URLParameters.Period)
           // viewModel.progressLiveData.postValue(false)
            initSpinner()
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
    }

    private fun initSubmitButton() {
        if(mLogStatus?.CheckInStatus==true)
        {
            setCheckOutUi()
            binding.spinnerVisitLocation.setSelection(visitlist.indexOf(visitlist.find { it.ID==mLogStatus?.VisitTypeId?:0}))
            binding.spinnerPeriod.setSelection(sessionlist.indexOf(sessionlist.find { it.ID==mLogStatus?.SessionTypeId?:0}))
        }else{
            setCheckInUi()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun setCheckInUi() {
       mISLogin=true
        binding.btnMyAttendance.text=MyApp.getInstance().resources.getString(R.string.lbl_checkIn)
        binding.btnMyAttendance.background.setTint(resources.getColor(R.color.green_A700))
        binding.spinnerPeriod.isEnabled=true
        binding.spinnerVisitLocation.isEnabled=true
    }
    private fun setCheckOutUi() {
        mISLogin=false
        binding.btnMyAttendance.text=MyApp.getInstance().resources.getString(R.string.lbl_checkOut)
        binding.btnMyAttendance.background.setTint(resources.getColor(R.color.red_600))
        binding.spinnerVisitLocation.isEnabled=false
        binding.spinnerPeriod.isEnabled=false

    }

    private fun initSpinner() {
        val sessionAdapter= ArrayAdapter(this, R.layout.attendance_spinner_item,sessionlist.map { it.featureName })
        binding.spinnerPeriod.adapter=sessionAdapter
        binding.spinnerPeriod.onItemSelectedListener=this
        val locationAdapter= ArrayAdapter(this, R.layout.attendance_spinner_item,visitlist.map { it.featureName })
        binding.spinnerVisitLocation.adapter=locationAdapter
        binding.spinnerVisitLocation.onItemSelectedListener=this
        viewModel.getAttendanceStatus(userdetails.userID?:0,currentdate)

    }

    fun getCurrentLocation(){
        if(checkPermisson()){
              if(isLocationEnabled()){
                  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                      ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                      {
                          requestpermission()
                      return
                  }
                  mFusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                      var location:Location?=task.result
                       if(location!=null)
                       {
                           mLatitude=location.latitude.toString()
                           mLongitude=location.longitude.toString()
                           val geocoder = Geocoder(this, Locale.getDefault())
                           var addresses : List<Address>?=null
                           try {
                               addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                           } catch (e: IOException) {
                               e.printStackTrace()
                           }
                           if (!addresses.isNullOrEmpty()) {
                               Log.d("max", " " + addresses[0].getMaxAddressLineIndex())
                               val address: String = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                               val city: String = addresses[0].getLocality()
                               val state: String = addresses[0].getAdminArea()
                               val country: String = addresses[0].getCountryName()
                               val postalCode: String = addresses[0].getPostalCode()
                               val knownName: String = addresses[0].getFeatureName() // Only if available else return NULLaddresses[0].getAdminArea()

                                mCurrentLocation = "$address $city"

                              // showToast(mCurrentLocation)
                               Log.e("Location",mCurrentLocation)
                               if(mISLogin){
                                   checkinLocation = "$address $city"
                                   checkoutLocation = ""
                               }else{
                                   checkoutLocation = "$address $city"
                                   checkinLocation = ""
                               }
                       }else{
                               requestpermission()
                           }
                       } else{
                           showToast("Location Not Found")
                           val gmmIntentUri = Uri.parse("geo:15.3559335,75.1354651")
                           val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                           mapIntent.setPackage("com.google.android.apps.maps")
                           startActivity(mapIntent)
                       }
                  }
              }else{
                  showToast("Turn on Location")
                  val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                  startActivity(intent)
               }
        }else{
            requestpermission()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun requestpermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onResume() {
        getCurrentLocation()
        super.onResume()
    }

    public override fun setUpClicks(): Unit {
        binding.btnBack.setOnClickListener {
            finish()
        }
        val currentclocktime = binding.txtTime.text
        Log.e("currentclocktime", currentclocktime.toString())

        binding.txtWeek.text = currentweekday
        binding.txtDate.text = currentdate

        binding.imageCamera.setOnClickListener {
            ImagePickerFragmentDialog(false).show(supportFragmentManager)
            { path ->
                if(path!=null) {
                    binding.squareImage.setImageURI(path)
                    mImageUri = path
                    setImagePath(path)
                    mImageBytes = FileUploadHelper.compressedImageFile(mImageUri!!,this)
                }else {
                    Snackbar.make(
                        binding.root,
                        MyApp.getInstance().resources.getString(R.string.lbl_select_img),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
           /* val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePicture, 0)*/
        }
        binding.btnMyAttendance.setOnClickListener {
            getCurrentLocation()
            if (mImageUri != null) {
              viewModel.imageUpload(ImageFolders.Attendance,mImageUri!!,mImageBytes)
            } else {
                Snackbar.make(
                    binding.root,
                    MyApp.getInstance().resources.getString(R.string.lbl_select_img_pls),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnAttendanceDetails.setOnClickListener{
            val destIntent= AttendanceDetailsActivity.getIntent(this,null)
            destIntent.putExtra(IntentParameters.EmpID,userdetails?.userID.toString())
            startActivity(destIntent)
        }
    }

    fun sendSaveAttendanceData(){
        val saveattendanceobject = SaveAttendanceDataRequest(
            employeeID = userdetails.userID,
            visitType = currentvisitLocation?.featureName,
            sessionTypeID = currentsession?.ID?:0,
            logDate=SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
            location_Latitude = mLatitude,
            location_Longitude = mLongitude,
            imageURl = mImageUrl,
            checkInAddress =checkinLocation,
            checkOutAddress = checkoutLocation)
        viewModel.saveattendanceVM(saveattendanceobject)
    }

    /*get image path from file*/
    fun setImagePath(picUri: Uri){
        val uriPathHelper = URIPathHelper()
        val filePath = uriPathHelper.getPath(this, picUri)
        if(filePath!=null)
            imageFile = File(filePath)
        Log.e("ImagePath", "path is $filePath")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showToast("Permission Granted")
                getCurrentLocation()
            }
            else
                showToast("Permission Denied")
        }
    }

    fun showToast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    public override fun addObservers(): Unit {
        var progressDialog: AlertDialog? = null
        viewModel.progressLiveData.observe(this@AttendanceActivity) {
            if (it) {
                progressDialog?.dismiss()
                progressDialog = null
                progressDialog = this@AttendanceActivity.showProgressDialog()
            } else {
                progressDialog?.dismiss()
            }
        }
        viewModel.imageUploadLiveData.observe(this@AttendanceActivity) {
            if(it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                if(it.data.status == true) {
                    mImageUrl = it.data.imagePath
                    sendSaveAttendanceData()
                }else {
                    Snackbar.make(
                        binding.root,
                        it.data.message ?: "Image is not uploaded",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else if(it is ErrorResponse)  {
                onErrorCreate(it.data ?: java.lang.Exception())
            }
        }
        viewModel.saveattendanceLiveData.observe(this@AttendanceActivity) {
            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                onSuccessCreate(it)
            } else if (it is ErrorResponse) {
                onErrorCreate(it.data ?: Exception())
            }
        }
        viewModel.attendanceStatusLiveData.observe(this@AttendanceActivity) {

            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
               mLogStatus=it.data.LoginData
                Log.e("LogStatus","$mLogStatus")
                if(mLogStatus!=null){
                    binding.btnMyAttendance.visibility=View.VISIBLE
                    initSubmitButton()
                }

                else {
                    binding.btnMyAttendance.visibility=View.GONE
                    Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.msg_try_again_later), Snackbar.LENGTH_LONG).show()
                }
            } else if (it is ErrorResponse) {
                onErrorCreate(it.data ?: Exception())
            }
        }
    }

    private fun onSuccessCreate(response: SuccessResponse<SaveAttendanceDataResponse>): Unit {

       if(response.data.status == true) {
           if (mISLogin) {
               Snackbar.make(
                   binding.root,
                   "Hi ${userdetails.username}, you have checked-in successfully! Have a good day",
                   Snackbar.LENGTH_LONG
               ).show()

           } else {
               Snackbar.make(
                   binding.root,
                   "Thank you ${userdetails.username}, you have checked-out successfully!..Hope you had a good day",
                   Snackbar.LENGTH_LONG
               ).show()
           }
           mImageUri=null
           binding.squareImage.setImageResource(android.R.drawable.ic_menu_camera)
           viewModel.getAttendanceStatus(userdetails.userID?:0,currentdate)

       }
        else
           Snackbar.make(binding.root, "${response.data.message}", Snackbar.LENGTH_LONG).show()
    }


    private fun onErrorCreate(exception: Exception): Unit {
        when (exception) {
            is NoInternetConnection -> {
                Snackbar.make(binding.root, exception.message ?: "", Snackbar.LENGTH_LONG).show()
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()?.string()
                val errorObject = if (errorBody != null && errorBody.isJSONObject())
                    JSONObject(errorBody) else JSONObject()
                Snackbar.make(
                    binding.root, "Error",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }
    }
    fun checkPermisson():Boolean{
        return (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
    }
    public companion object {
        public const val TAG: String = "ATTEDENCE_ACTIVITY"
        private const val PERMISSION_REQUEST_ACCESS_LOCATION: Int = 100

        public fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, AttendanceActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, spinnerView: View?, position: Int, p3: Long) {
        when(parent?.id!!){
            R.id.spinner_visit_location->{
                currentvisitLocation=visitlist[position]
            }
            R.id.spinner_period->{
                currentsession=sessionlist[position]
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}