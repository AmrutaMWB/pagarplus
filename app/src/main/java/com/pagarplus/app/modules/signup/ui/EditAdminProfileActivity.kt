package com.pagarplus.app.modules.signup.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.appcomponents.views.TimePickerFragment
import com.pagarplus.app.databinding.ActivitySignUpBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.ItemlistDialog
import com.pagarplus.app.modules.signup.data.viewmodel.SignUpVM
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.network.models.createsignup.CreateSignUpResponse
import com.pagarplus.app.network.models.createsignup.StateListResponse
import com.pagarplus.app.network.models.others.ApiResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import org.koin.android.ext.android.bind
import retrofit2.HttpException
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String
import kotlin.Unit

class EditAdminProfileActivity :
    BaseActivity<ActivitySignUpBinding>(R.layout.activity_sign_up),ItemlistDialog.OnCompleteListener {
  private val viewModel: SignUpVM by viewModels<SignUpVM>()
  private lateinit var mImageBytes:ByteArray
  private var imageFile:File?=null
  var mPicUri_img: Uri?=null
  var img_pathfrom_api: String? = null

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")

    var modelval = viewModel.signUpModel.value
    if(viewModel.profiledetails?.profileImageURl.isNullOrEmpty()){
      viewModel.signUpModel.value?.etProfileImgURL = viewModel.profiledetails?.profileImageURl
      Glide.with(this).load(R.drawable.img_camera).into(binding.imageCamera)
      binding.txtClickImage.setText("Upload Logo")
    }else {
      viewModel.signUpModel.value?.etProfileImgURL = viewModel.profiledetails?.profileImageURl
      Glide.with(this).load(viewModel.profiledetails?.profileImageURl).into(binding.imageCamera)
      binding.txtClickImage.setText("Change Logo")
    }
    viewModel.signUpModel.value?.etFirmNameValue = if(viewModel.profiledetails?.organization.isNullOrEmpty()) modelval?.etFirmNameValue else viewModel.profiledetails?.organization
    viewModel.signUpModel.value?.etFirstNameValue = if(viewModel.profiledetails?.firstName.isNullOrEmpty()) "" else viewModel.profiledetails?.firstName
    viewModel.signUpModel.value?.etLastNameValue = if(viewModel.profiledetails?.lastName.isNullOrEmpty()) "" else viewModel.profiledetails?.lastName
    viewModel.signUpModel.value?.etEmailIDValue = if(viewModel.profiledetails?.email.isNullOrEmpty()) "" else viewModel.profiledetails?.email
    viewModel.signUpModel.value?.etMobileValue = if(viewModel.profiledetails?.mobileNumber.isNullOrEmpty()) "" else viewModel.profiledetails?.mobileNumber
    viewModel.signUpModel.value?.etAddressValue = if(viewModel.profiledetails?.address.isNullOrEmpty()) "" else viewModel.profiledetails?.address
    viewModel.signUpModel.value?.etStateValue = if(viewModel.profiledetails?.state.isNullOrEmpty()) modelval?.etStateValue else viewModel.profiledetails?.state
    viewModel.signUpModel.value?.etCityValue = if(viewModel.profiledetails?.city.isNullOrEmpty()) modelval?.etCityValue else viewModel.profiledetails?.city
    viewModel.signUpModel.value?.etPasswordValue = viewModel.profiledetails?.password.toString()
    viewModel.signUpModel.value?.etConfirmPassworValue = viewModel.profiledetails?.confirmPassword.toString()
    viewModel.signUpModel.value?.etOffEndtime = viewModel.profiledetails?.officeEndTime?.extractTimeAMPM()
    //viewModel.signUpModel.value?.etConfirmPassworValue = viewModel.profiledetails?.confirmPassword.toString()

    viewModel.signUpModel.value?.oldPassword = viewModel.profiledetails?.password.toString()
    binding.etFirmName.isEnabled = false
    binding.signUpVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.outlinedPasswordField.isVisible = false
    binding.outlinedCnfPwdField.isVisible = false
    binding.profImg.isVisible = true
    binding.outlinedOffOpentime.isVisible = true
    binding.outlinedReferalcode.isVisible = false

    binding.btnBack.setOnClickListener{
      finish()
      startActivity(AdmindashboardActivity.getIntent(this,null))
    }

    binding.imageCamera.setOnClickListener {
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imageCamera.setImageURI(path)
          mPicUri_img=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }

    binding.etState.setOnClickListener{
      /*execute state list api*/
      callpopup(true)
    }

    binding.etCity.addTextChangedListener(object : TextWatcher {

      override fun afterTextChanged(s: Editable) {
      }

      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if(viewModel.signUpModel.value?.etStateValue.isNullOrEmpty()) {
          binding.outlinedSateField.setError("Please select state")
          binding.etCity.isEnabled = false
        }else {
          if (count == 3) {
            callpopup(false, viewModel.signUpModel.value?.StateID!!, s.toString())
          }
        }
      }
    })

    binding.etOffOpenTime.setOnClickListener {
      val destinationInstance = TimePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        TimePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedDate)
        binding.etOffOpenTime.setText(selected)
      }
    }

    binding.btnRegSubmit.setOnClickListener {
      if(isValidate()){
        if(mPicUri_img != null){
          viewModel.ImageUpload(ImageFolders.Profile, mPicUri_img!!,mImageBytes)
        }else{
          viewModel.callUpdateProfileApi()
        }
      }
    }

    binding.etFirmName.addTextChangedListener(TextFieldValidation(binding.etFirmName))
    binding.etMobile.addTextChangedListener(TextFieldValidation(binding.etMobile))
    binding.etOffOpenTime.addTextChangedListener(TextFieldValidation(binding.etOffOpenTime))
  }

  /*validate eidttext fields*/
  private fun isValidate(): Boolean =
    validateRequired(binding.outlinedFirmField,binding.etFirmName) &&
            validateMobile(binding.outlinedMobileField,binding.etMobile) &&
            validateRequired(binding.outlinedOffOpentime,binding.etOffOpenTime)
  /**
   * applying text watcher on each text field
   */
  inner class TextFieldValidation(private val view: View) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      // checking ids of each text field and applying functions accordingly.
      when (view.id) {
        R.id.etFirmName -> {
          validateRequired(binding.outlinedFirmField,binding.etFirmName)
        }
        R.id.etMobile -> {
          validateMobile(binding.outlinedMobileField,binding.etMobile)
        }
        R.id.etOffOpenTime -> {
          validateRequired(binding.outlinedOffOpentime,binding.etOffOpenTime)
        }
      }
    }
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

  /*get image path from file*/
  fun setImagePath(picUri: Uri){
    val uriPathHelper = URIPathHelper()
    val filePath = uriPathHelper.getPath(this, picUri)
    if(filePath!=null)
      imageFile = File(filePath)
    mImageBytes = FileUploadHelper.compressedImageFile(picUri,this)
    Log.e("ImagePath", "path is $filePath")
  }

  override fun addObservers(): Unit {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }

    viewModel.updateProfileLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessUpdate(it)
      } else if(it is ErrorResponse) {
        onErrorMessage(it.data ?: Exception())
      }
    }

    /*get imagepath from fileUpload api on success*/
    /*upload image and get path*/
    viewModel.imageUploadLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        img_pathfrom_api = response?.imagePath
        Toast.makeText(applicationContext,response?.imagePath, Toast.LENGTH_LONG).show();
        viewModel.signUpModel.value?.etProfileImgURL = img_pathfrom_api
        /*call update api*/
        viewModel.callUpdateProfileApi()
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message, Toast.LENGTH_LONG).show();
      }
    }
  }

  private fun onSuccessUpdate(response: SuccessResponse<ApiResponse>): Unit {
    Log.e("Sattus",response.toString());
    this.alert("","${response.`data`.message}") {
      neutralButton {
        if (response.data.status == true) {
          val destIntent = AdmindashboardActivity.getIntent(context, null)
          startActivity(destIntent)
          finish()
        }else{
          it.dismiss()
        }
      }
    }
    viewModel.bindUpdateProfileResponse(response.data)
  }

  private fun onErrorMessage(exception: Exception): Unit {
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
        this.alert(MyApp.getInstance().getString(R.string.lbl_register_status),errMessage) {
          neutralButton {
              dialogInterface ->
          }
        }
      }
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("State","$selectedItem")
    if(selectedItem.isState==true){
      binding.outlinedSateField.setError("")
      binding.etCity.isEnabled = true
      binding.etState.setText(selectedItem.txtName)
      viewModel.signUpModel.value?.StateID = selectedItem.txtValue
    }else{
      binding.etCity.setText(selectedItem.txtName)
      viewModel.signUpModel.value?.cityID = selectedItem.txtValue
    }
  }

  companion object {
    const val TAG: String = "EDIT_ADMIN_PROFILE_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, EditAdminProfileActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
