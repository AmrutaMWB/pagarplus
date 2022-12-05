package com.pagarplus.app.modules.createbranch.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.databinding.ActivityCreateBranchBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.adminemplist.ui.AdminemplistActivity
import com.pagarplus.app.modules.createbranch.data.viewmodel.CreateBranchVM
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.ItemlistDialog
import com.pagarplus.app.network.models.createcreateBranch.CreateCreateBranchResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class CreateBranchActivity :
  BaseActivity<ActivityCreateBranchBinding>(R.layout.activity_create_branch), OnMapReadyCallback,
  ItemlistDialog.OnCompleteListener {
  private val viewModel: CreateBranchVM by viewModels<CreateBranchVM>()

  var currentLocation : Location? = null
  var address:String = "";
  var city:String="";
  var state:String = "";
  var postalcode:String = "";
  var fusedLocationProviderClient: FusedLocationProviderClient? = null
  val REQUEST_CODE = 101;
  var isNavigate: Boolean = false

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    isNavigate = intent.getBooleanExtra(IntentParameters.IsNevigate,true)

    binding.createBranchVM = viewModel
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    CurrentLocation()
  }

  override fun setUpClicks(): Unit {
    binding.btnBack.setOnClickListener{
      startActivity(AdmindashboardActivity.getIntent(this,null))
      finish()
    }

    binding.btnSubmit.setOnClickListener {
      if(viewModel.createBranchModel.value?.etEdtTxtBranchnameValue?.isRequired() == true) {
        binding.outlinedBranchname.setError("")
        if(viewModel.createBranchModel.value?.etEdtTxtBranchadrValue?.isRequired() == true) {
          binding.outlinedBranchAdre.setError("")
          this@CreateBranchActivity.hideKeyboard()
          viewModel.callCreateCreateBranchApi()
        }else{
          binding.outlinedBranchAdre.setError("Please enter branch address")
        }
      }else{
        binding.outlinedBranchname.setError("Please enter branch name")
      }
    }

    binding.btnLocate.setOnClickListener {
      binding.etEdtTxtCity.setText(city)
      binding.etEdtTxtState.setText(state)
      binding.etEdtTxtBranchadr.setText(address)
    }

    binding.etEdtTxtState.setOnClickListener{
      /*execute state list api*/
      callpopup(true)
    }

    binding.etEdtTxtCity.addTextChangedListener(object : TextWatcher {

      override fun afterTextChanged(s: Editable) {
      }

      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if(count == 3) {
          callpopup(false, viewModel.createBranchModel.value?.StateID!!,s.toString())
        }
      }
    })
  }

  fun callpopup(isState:Boolean,stateId:Int=0,cityKeyword: String=""){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsStateOrCity, isState)
    if(!isState)
      bundle.putInt(IntentParameters.StateId, stateId)
    bundle.putString(IntentParameters.CityKeyword, cityKeyword)

    var itemlistDialog = ItemlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
  }

  override fun addObservers() {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this@CreateBranchActivity) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@CreateBranchActivity.showProgressDialog()
      } else  {
        progressDialog?.dismiss()
      }
    }
    viewModel.createCreateBranchLiveData.observe(this@CreateBranchActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateCreateBranch(it)
      } else if(it is ErrorResponse)  {
        onErrorCreateCreateBranch(it.data ?:Exception())
      }
    }
  }

  private
  fun onSuccessCreateCreateBranch(response: SuccessResponse<CreateCreateBranchResponse>) {
    viewModel.bindCreateCreateBranchResponse(response.data)
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setTitle("${response.`data`.message}")
    // set message of alert dialog
    dialogBuilder.setMessage("You want to create another Branch?")
      // if the dialog is cancelable
      .setCancelable(false)
      // positive button text and action
      .setPositiveButton("Yes", DialogInterface.OnClickListener {
          dialog, id ->
        dialog.dismiss()
        binding.etEdtTxtBranchname.setText("")
      })
      // negative button text and action
      .setNegativeButton("No", DialogInterface.OnClickListener {
          dialog, id -> dialog.cancel()
        if(isNavigate){
          finish()
        }else {
          finish()
          startActivity(AdmindashboardActivity.getIntent(this, null))
        }
      })
    // create dialog box
    val alert = dialogBuilder.create()
    // show alert dialog
    alert.show()
  }

  private fun onErrorCreateCreateBranch(exception: Exception) {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
      is HttpException -> {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
        else JSONObject()
        val errMessage = if(!errorObject.optString("Message").isNullOrEmpty()) {
          errorObject.optString("Message").toString()
        } else {
          exception.response()?.message()?:""
        }
        Snackbar.make(binding.root, errMessage, Snackbar.LENGTH_LONG).show()
      }
    }
  }

  private fun CurrentLocation(){
    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
      != PackageManager.PERMISSION_GRANTED)
    {
      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
      return
    }

    val task = fusedLocationProviderClient!!.lastLocation
    task.addOnSuccessListener { location ->
      if (location != null){
        currentLocation = location
        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)
        supportMapFragment!!.getMapAsync(this@CreateBranchActivity)
      }
    }
  }

  /*getting current location*/
  override fun onMapReady(googleMap: GoogleMap) {
    val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
    val markerOptions = MarkerOptions().position(latLng).title("You are Here!")
    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
    googleMap.addMarker(markerOptions
      .draggable(true))

    val geocoder = Geocoder(this, Locale.getDefault())
    var addresses : List<Address>?=null
    try {
      addresses = geocoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
    } catch (e: IOException) {
      e.printStackTrace()
    }
    if (addresses!!.size > 0) {
      Log.e("max", " " + addresses[0].getMaxAddressLineIndex())
      address = addresses.get(0).getAddressLine(0)// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
      city = addresses[0].getLocality()
      state = addresses[0].getAdminArea()
      val country: String = addresses[0].getCountryName()
      postalcode = addresses[0].getPostalCode()
      val knownName: String = addresses[0].getFeatureName() // Only if available else return NULLaddresses[0].getAdminArea()

      binding.etEdtTxtCity.setText(city)
      binding.etEdtTxtState.setText(state)
      binding.etEdtTxtBranchadr.setText(address)
      viewModel.createBranchModel.value?.LatitudeValue = currentLocation!!.latitude.toString()
      viewModel.createBranchModel.value?.LongitudeValue = currentLocation!!.longitude.toString()

      val mCurrentLocation = "$address"
      Log.e("Location",mCurrentLocation)

      googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragStart(arg0: Marker) {
          // TODO Auto-generated method stub
          Log.e(
            "System out",
            "onMarkerDragStart..." + arg0.getPosition().latitude.toString() + "..." + arg0.getPosition().longitude
          )
          googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()))
          val geocoder = Geocoder(applicationContext, Locale.getDefault())
          var addresses1 : List<Address>?=null
          try {
            addresses1 = geocoder.getFromLocation(arg0.getPosition().latitude, arg0.getPosition().longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
          } catch (e: IOException) {
            e.printStackTrace()
          }
          if (addresses1!!.size > 0) {
            Log.e("max", " " + addresses1[0].getMaxAddressLineIndex())
            address = addresses1.get(0).getAddressLine(0)// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses1[0].getLocality()
            state = addresses1[0].getAdminArea()
            val country: String = addresses1[0].getCountryName()
            postalcode = addresses1[0].getPostalCode()
            val knownName: String = addresses1[0].getFeatureName() // Only if available else return NULLaddresses[0].getAdminArea()

            val mCurrentLocation = "$address"
            Log.e("Location1",mCurrentLocation)
          }
        }

        override fun onMarkerDragEnd(arg0: Marker) {
          // TODO Auto-generated method stub
          Log.e(
            "MarkerEnd",
            "onMarkerDragStart..." + arg0.getPosition().latitude.toString() + "..." + arg0.getPosition().longitude
          )
        }

        override fun onMarkerDrag(arg0: Marker?) {
          // TODO Auto-generated method stub
          Log.i("marker", "onMarkerDrag...")
        }
      })
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when(requestCode){
      REQUEST_CODE -> {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
          CurrentLocation()
        }
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  companion object {
    const val TAG: String = "CREATE_BRANCH_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, CreateBranchActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("State","$selectedItem")
    if(selectedItem.isState==true){
      binding.etEdtTxtState.setText(selectedItem.txtName)
    }else{
      binding.etEdtTxtCity.setText(selectedItem.txtName)
    }
  }

  override fun onBackPressed() {
    val builder = AlertDialog.Builder(this)
    //set title for alert dialog
    builder.setTitle("Alert !!")
    //set message for alert dialog
    builder.setMessage("Are you sure you want to leave this page?")
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    //performing positive action
    builder.setPositiveButton("Yes") { dialogInterface, which ->
      finish()
    }
    //performing negative action
    builder.setNegativeButton("No") { dialogInterface, which ->
      dialogInterface.dismiss()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()
  }
}
