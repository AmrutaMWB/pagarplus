package com.pagarplus.app.modules.expense.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityExpenseBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.expense.data.model.ExpenseModel
import com.pagarplus.app.modules.expense.`data`.viewmodel.ExpenseVM
import com.pagarplus.app.modules.expensereport.ui.ExpenseReportActivity
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.createcreateemployee.ProofItem
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.expense.AddExpenseRequest
import com.pagarplus.app.network.models.expense.ExpenseItem
import com.pagarplus.app.network.models.expense.ImageItem
import com.pagarplus.app.network.models.expense.SubExpenseItem
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.String
import kotlin.Unit
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import org.koin.android.ext.android.inject

class ExpenseActivity : BaseActivity<ActivityExpenseBinding>(R.layout.activity_expense),
  AdapterView.OnItemSelectedListener{
  private var mImageUrl: String?=null
  private val viewModel: ExpenseVM by viewModels<ExpenseVM>()
  var mExpenseList= arrayListOf<ExpenseItem>()
  var mMainSubExpenseList= arrayListOf<SubExpenseItem>()
  var mSubExpenseList= arrayListOf<SubExpenseItem>()
  var mCurrentExpenseType=ExpenseItem()
  var mCurrentSubExpense=SubExpenseItem()
  var mBoardingFromDate=Date()
  var mBoardingToDate=Date()
  var mTravelPicUri:Uri?=null
  var mLocalPicUri:Uri?=null
  var mBoardingPicUri:Uri?=null
  var mDnsPicUri:Uri?=null
  private lateinit var mImageBytes:ByteArray
  private var imageFile:File?=null
  private var mExpenseRequest: AddExpenseRequest= AddExpenseRequest()
  private val prefs: PreferenceHelper by inject()
  private var mLoginDetails=prefs.getLoginDetails<LoginResponse>()!!
  private var mProfileDetails=prefs.getProfileDetails<CreateCreateEmployeeRequest>()
  var arrindex: Int? = 0

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.expenseVM = viewModel
    lifecycleScope.launch {
      viewModel.progressLiveData.postValue(true)
      mExpenseList = ApiUtil(this@ExpenseActivity).getExpenseTypes()
      mMainSubExpenseList = ApiUtil(this@ExpenseActivity).getSubExpenseTypes()
      viewModel.progressLiveData.postValue(false)
      initSpinner()
    }
  }

  fun initSpinner(){
    val expenseAdapter = ArrayAdapter(this@ExpenseActivity, R.layout.attendance_spinner_item, mExpenseList.map { it.expenseName })
    binding.spinnerExpenseType.adapter = expenseAdapter
    binding.spinnerExpenseType.onItemSelectedListener = this@ExpenseActivity
  }

  private fun setSubExpenseTypeSpinners() {
    mSubExpenseList.clear()
    mMainSubExpenseList.forEach {
      if(it.expenseId==mCurrentExpenseType?.expenseId?:0)
       mSubExpenseList.add(it)
    }
    val dnsExpenseAdapter= ArrayAdapter(this, R.layout.attendance_spinner_item,mSubExpenseList.map { it.subexpenseName })
    binding.spinnerSubExpense.adapter=dnsExpenseAdapter
    binding.spinnerSubExpense.onItemSelectedListener=this
  }

  override fun setUpClicks(): Unit {
    binding.imgTravel.setOnClickListener {
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imgTravel.setImageURI(path)
          mTravelPicUri=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }
    binding.imgLocalExpense.setOnClickListener {
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imgLocalExpense.setImageURI(path)
          mLocalPicUri=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }
    binding.imgDns.setOnClickListener {
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imgDns.setImageURI(path)
          mDnsPicUri=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }
    binding.imgBoarding.setOnClickListener {
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imgBoarding.setImageURI(path)
          mBoardingPicUri=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }

    binding.txtLocalExpenseDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtLocalExpenseDate.text = selected
      }
    }

    binding.txtTravelDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtTravelDate.text = selected
      }
    }

    binding.txtDnsDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtDnsDate.text = selected
      }
    }

      binding.boardingFromDateLayout.setOnClickListener {
        val destinationInstance = DatePickerFragment.getInstance()
        destinationInstance.show(
          this.supportFragmentManager,
          DatePickerFragment.TAG
        ) { selectedDate ->
          mBoardingFromDate=selectedDate
          val selected = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
          binding.txtBoardingFromDate.text = selected
        }
    }
      binding.boardingToDateLayout.setOnClickListener {
        val destinationInstance = DatePickerFragment.getInstance()
        destinationInstance.show(
          this.supportFragmentManager,
          DatePickerFragment.TAG
        ) { selectedDate ->
          mBoardingToDate=selectedDate
          val selected = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
          binding.txtBoardingToDate.text = selected
        }
      }

      binding.btnBack.setOnClickListener {
        finish()
      }
    binding.btnTravelSubmit.setOnClickListener {
      if(validateTravelDetails())
        createExpenseObject()
        viewModel.addExpenseDetails(mExpenseRequest)
    }
    binding.btnLocalExpSubmit.setOnClickListener {
      if(validateLocalDetails())
      createExpenseObject()
      viewModel.addExpenseDetails(mExpenseRequest)
    }
    binding.btnDnsSubmit.setOnClickListener {
      if(validateDnsDetails())
        createExpenseObject()
        viewModel.addExpenseDetails(mExpenseRequest)
    }
    binding.btnBoardingSubmit.setOnClickListener {
      if(validateBoardingDetails())
        createExpenseObject()
        viewModel.addExpenseDetails(mExpenseRequest)
    }

    binding.btnExpenseReport.setOnClickListener {
      val destIntent=ExpenseReportActivity.getIntent(this,null)
      destIntent.putExtra(IntentParameters.AdminId,mProfileDetails?.adminID.toString())
      destIntent.putExtra(IntentParameters.UserId,mProfileDetails?.empID.toString())
      startActivity(destIntent)
    }
  }

  /*get image path from file*/
  fun setImagePath(picUri: Uri){
    val uriPathHelper = URIPathHelper()
    val filePath = uriPathHelper.getPath(this, picUri)
    if(filePath!=null)
      imageFile = File(filePath)
      mImageBytes = FileUploadHelper.compressedImageFile(picUri,this)
      viewModel.imageUpload(ImageFolders.Expense,picUri!!,mImageBytes)
      Log.e("ImagePath", "path is $filePath")
  }

  fun clearDetails(){
    viewModel.expenseModel.value=ExpenseModel()
    mTravelPicUri=null
    mLocalPicUri=null
    mDnsPicUri=null
    mBoardingPicUri=null
    binding.imgBoarding.setImageResource(R.drawable.ic_camera)
    binding.imgDns.setImageResource(R.drawable.ic_camera)
    binding.imgLocalExpense.setImageResource(R.drawable.ic_camera)
    binding.imgTravel.setImageResource(R.drawable.ic_camera)
  }


  fun validateTravelDetails():Boolean{
    val amt=binding.etTravelAmount.text.trim().toString()
    val src=binding.etSourcePlace.text.trim().toString()
    val dst=binding.etDestPlace.text.trim().toString()
    val date=binding.txtTravelDate.text.trim().toString()
    val comment=binding.etLocalExpenseDetails.text.trim().toString()

    if(amt.isNotEmpty()){
      if(src.isNotEmpty()){
        if(dst.isNotEmpty()){
          if(date.getDate()<=Date()) {
            if (comment.isNotEmpty()) {
              if (viewModel.expenseModel.value?.ImageList != null) {
                return true
              } else
                Snackbar.make(
                  binding.root,
                  MyApp.getInstance().resources.getString(R.string.lbl_select_img_pls),
                  Snackbar.LENGTH_LONG
                ).show()
            }else
              Snackbar.make(
                binding.root,
                MyApp.getInstance().resources.getString(R.string.lbl_ent_comm),
                Snackbar.LENGTH_LONG
              ).show()
          }
            else
            Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()

        }
        else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_dest), Snackbar.LENGTH_LONG).show()

      }
      else
        Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_src), Snackbar.LENGTH_LONG).show()

    }
    else
      Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_amt), Snackbar.LENGTH_LONG).show()

    return false
  }

  fun validateLocalDetails():Boolean{
    val amt=binding.etLocalExpenseAmount.text.trim().toString()
    val comment=binding.etLocalExpenseDetails.text.trim().toString()
    val date=binding.txtLocalExpenseDate.text.trim().toString()

    if(date.getDate()<=Date()) {
      if (amt.isNotEmpty()) {
        if (comment.isNotEmpty()) {
          if (viewModel.expenseModel.value?.ImageList != null) {
            return true
          } else
            Snackbar.make(
              binding.root,
              MyApp.getInstance().resources.getString(R.string.lbl_select_img_pls),
              Snackbar.LENGTH_LONG
            ).show()

        } else
          Snackbar.make(
            binding.root,
            MyApp.getInstance().resources.getString(R.string.lbl_ent_comm),
            Snackbar.LENGTH_LONG
          ).show()


      } else
        Snackbar.make(
          binding.root,
          MyApp.getInstance().resources.getString(R.string.lbl_ent_amt),
          Snackbar.LENGTH_LONG
        ).show()
    }
      else
      Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()
    return false
  }

  fun validateDnsDetails():Boolean{
    val date=binding.txtDnsDate.text.trim().toString()
    val amt=binding.etDnsAmount.text.trim().toString()
    val dnsDetails=binding.etDNSExpenseDetails.text.trim().toString()

    if(date.getDate()<=Date()) {
    if(amt.isNotEmpty()){
      if(dnsDetails.isNotEmpty()) {
        if (viewModel.expenseModel.value?.ImageList != null) {
          return true
        } else
          Snackbar.make(
            binding.root,
            MyApp.getInstance().resources.getString(R.string.lbl_select_img_pls),
            Snackbar.LENGTH_LONG
          ).show()
      }else
        Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_pls_ent_dns_detail), Snackbar.LENGTH_LONG).show()
    } else
      Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_amt), Snackbar.LENGTH_LONG).show()
    } else
      Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()
    return false
  }

  fun validateBoardingDetails():Boolean{
    val amt=binding.etBoardingAmount.text.trim().toString()
    val place=binding.etBoardingPlace.text.trim().toString()
    val hotelDetails=binding.etHotelDetails.text.trim().toString()

    if(validateBoardingDates()){
    if(amt.isNotEmpty()){
      if(place.isNotEmpty()){
        if(hotelDetails.isNotEmpty()){
          if(viewModel.expenseModel.value?.ImageList!=null){
            return true
          } else
            Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img_pls), Snackbar.LENGTH_LONG).show()

        } else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_pls_ent_hotel_detail), Snackbar.LENGTH_LONG).show()

      } else
        Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_hotel), Snackbar.LENGTH_LONG).show()
    } else
      Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_amt), Snackbar.LENGTH_LONG).show()
    } else
      Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()
    return false
  }

  private fun validateBoardingDates(): Boolean {
if(mBoardingFromDate>mBoardingToDate)
  return false
    return true
  }

    override fun addObservers() {
      var progressDialog : AlertDialog? = null
      viewModel.progressLiveData.observe(this@ExpenseActivity) {
        if(it) {
          progressDialog?.dismiss()
          progressDialog = null
          progressDialog = this@ExpenseActivity.showProgressDialog()
        } else  {
          progressDialog?.dismiss()
        }
      }
      viewModel.imageUploadLiveData.observe(this@ExpenseActivity) {
        if(it is SuccessResponse) {
          val response = it.getContentIfNotHandled()
          if(it.data.status!!) {
            mImageUrl = it.data.imagePath
            if (mImageUrl != null) {
              showConfirmDialog(mImageUrl)
            }
          }
          else Snackbar.make(
                  binding.root,
          it.data.message?:"Image is not uploaded",
          Snackbar.LENGTH_LONG
          ).show()

        } else if(it is ErrorResponse)  {
          onError(it.data ?:Exception())
        }
      }
      viewModel.createAddExpenseLiveData.observe(this@ExpenseActivity) {
        if(it is SuccessResponse) {
          val response = it.getContentIfNotHandled()
          onSuccessCreateAddExpense(it)
        } else if(it is ErrorResponse)  {
          onError(it.data ?:Exception())
        }
      }
    }

    private fun onSuccessCreateAddExpense(response: SuccessResponse<RetroResponse>) {
      if(response.data.status!!){
        Snackbar.make(binding.root, "${response.`data`.message}", Snackbar.LENGTH_LONG).show()
        clearDetails()
        mImageUrl=null
      }else{
        Snackbar.make(binding.root, "${response.`data`.message}", Snackbar.LENGTH_LONG).show()
      }
    }

    private fun onError(exception: Exception) {
      when(exception) {
        is NoInternetConnection -> {
          Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
        }
        is HttpException -> {
          val errorBody = exception.response()?.errorBody()?.string()
          val errorObject = if (errorBody != null  && errorBody.isJSONObject())
              JSONObject(errorBody) else JSONObject()
          val errMessage = if(!errorObject.optString("Message").isNullOrEmpty()) {
            errorObject.optString("Message").toString()
          } else {
             exception.response()?.message()?:""
          }
          Snackbar.make(binding.root, errMessage, Snackbar.LENGTH_LONG).show()
        }
      }
    }

    companion object {
      const val TAG: String = "EXPENSE_ACTIVITY"
      public fun getIntent(context: Context, bundle: Bundle?): Intent {
        val destIntent = Intent(context, ExpenseActivity::class.java)
        destIntent.putExtra("bundle", bundle)
        return destIntent
      }
    }

  fun setExpenseViews(travel:Boolean,local:Boolean,dns:Boolean,boarding:Boolean){
    binding.travelLayout.visibility=if(travel)View.VISIBLE else View.GONE
    binding.dnsLayout.visibility=if(dns)View.VISIBLE else View.GONE
    binding.boardingLayout.visibility=if(boarding)View.VISIBLE else View.GONE
    binding.localExpenseLayout.visibility=if(local)View.VISIBLE else View.GONE
  }

  fun showConfirmDialog(imagePath: String?){
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setTitle("Confirmation")
    // set message of alert dialog
    dialogBuilder.setMessage(R.string.add_image_confirmation)
      // if the dialog is cancelable
      .setCancelable(false)
      // positive button text and action
      .setPositiveButton("Yes", DialogInterface.OnClickListener {
          dialog, id ->
        binding.imgBoarding.setImageResource(R.drawable.ic_camera)
        binding.imgDns.setImageResource(R.drawable.ic_camera)
        binding.imgLocalExpense.setImageResource(R.drawable.ic_camera)
        binding.imgTravel.setImageResource(R.drawable.ic_camera)
        setImageArray(imagePath)
        dialog.dismiss()
      })
      .setNeutralButton("No", DialogInterface.OnClickListener {
          dialog, id ->
        setImageArray(mImageUrl)
        dialog.cancel()
      })
    // create dialog box
    val alert = dialogBuilder.create()
    // show alert dialog
    alert.show()
  }
  /*after successfull uploading ID proof save to array*/
  fun setImageArray(imagePath: String?){
    viewModel.expenseModel.value?.ImageList?.add(arrindex!!, ImageItem(imagePath))
    arrindex!!+1
  }

  fun createExpenseObject(){
    val modelValue=viewModel.expenseModel.value?: ExpenseModel()
    val expenseObject=AddExpenseRequest(
      empID =mLoginDetails.userID?.toString()?:"0",
      expenseDateTime = modelValue.txtExpenseDate,
      amount = modelValue.txtAmount,
      expHeadID = mCurrentExpenseType.expenseId?.toString()?:"0",
      expTypeID = mCurrentSubExpense.subexpenseId?.toString(),
      sourceLocation = modelValue.txtSourceLocation,
      destinationLocation = modelValue.txtDestLocation,
      billImages = modelValue.ImageList,
      hotelDetails = modelValue.txtHotelDetails,
      hotelName = modelValue.txtHotelName,
      locationLongitude ="",
      locationLatitude = "",
      commentByEmployee = modelValue.txtComents,
      boardingFromDate = modelValue.txtBoardingFromDate,
      boardingToDate = modelValue.txtBoardingToDate
    )
    if(!mCurrentExpenseType.isSubtypeExist!!)
      expenseObject.expTypeID=null
      mExpenseRequest=expenseObject
  }

  override fun onItemSelected(parent: AdapterView<*>?, child: View?, position: Int, p3: Long) {
    if(parent?.id==R.id.spinnerExpenseType){
      mCurrentExpenseType=mExpenseList[position]
      if(mCurrentExpenseType.isSubtypeExist!!){
        binding.spinnerSubExpense.visibility=View.VISIBLE
        clearDetails()
        setSubExpenseTypeSpinners()
      }else
        binding.spinnerSubExpense.visibility=View.GONE

      if(mCurrentExpenseType.expenseName?.contains("Travel",true) == true)
        setExpenseViews(true,false,false,false)
      else if(mCurrentExpenseType.expenseName?.contains("Local",true) == true)
        setExpenseViews(false,true,false,false)
      else if(mCurrentExpenseType.expenseName?.contains("DNS",true) == true)
        setExpenseViews(false,false,true,false)
      else if(mCurrentExpenseType.expenseName?.contains("Lodging",true) == true)
        setExpenseViews(false,false,false,true)
      else
        setExpenseViews(false,true,false,false)
    }
    else if(parent?.id==R.id.spinnerSubExpense){
      mCurrentSubExpense=mSubExpenseList[position]
    }
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {
    TODO("Not yet implemented")
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
