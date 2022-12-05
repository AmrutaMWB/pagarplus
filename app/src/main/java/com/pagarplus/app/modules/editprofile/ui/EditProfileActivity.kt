package com.pagarplus.app.modules.editprofile.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.databinding.ActivityEditProfileBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.editprofile.data.model.EditProfileModel
import com.pagarplus.app.modules.editprofile.data.viewmodel.EditProfileVM
import com.pagarplus.app.modules.userdashboard.ui.UserdashboardActivity
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItem
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.editprofile.UserProfileObject
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(R.layout.activity_edit_profile)
{
  private var mImageUrl: String?=null
  private val viewModel: EditProfileVM by viewModels<EditProfileVM>()
  val prefs: PreferenceHelper by inject()
  private var mLoginDetails=prefs.getLoginDetails<LoginResponse>()
  private var mProfilePicUri:Uri?=null
  private lateinit var mImageBytes:ByteArray
  private var imageFile:File?=null
  lateinit var currentDate: Date

  @SuppressLint("SimpleDateFormat")
  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.editProfileVM = viewModel
    viewModel.callFetchGetEmpDataApi(mLoginDetails?.userID?:0)

    if(viewModel.editProfileModel.value?.etGender.equals("Male")){
      binding.radioMale.isChecked = true
    }else{
      binding.radioFemale.isChecked = true
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun setUpClicks(): Unit {

    binding.btnBack.setOnClickListener{
      val destIntent = UserdashboardActivity.getIntent(this,null)
      startActivity(destIntent)
      finish()
    }
    binding.etDOB.setOnClickListener {
      val c = Calendar.getInstance()
      var day = c.get(Calendar.DAY_OF_MONTH)
      var month = c.get(Calendar.MONTH)
      var year = c.get(Calendar.YEAR)

      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.etDOB.setText(selected)
        //viewModel.admindashboardModel.value?.txtAttendanceDate = selected
      }
    }

    binding.imgProfile.setOnClickListener {
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imgProfile.setImageURI(path)
          mProfilePicUri=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }

    binding.submit.setOnClickListener {
      val selectedId: Int = binding.radioGender.getCheckedRadioButtonId()
      var radioButton = findViewById<RadioButton>(selectedId)

      val modelValue=viewModel.editProfileModel.value
      modelValue?.etGender = radioButton.getText().toString()

      Log.e("Password","pswd:${modelValue?.etPassword}..ConPswd:${modelValue?.etConfirmPassword}")
      /*if(modelValue!!.etUIDNum!!.isAdhar()){
      if(modelValue.etPanNum!!.isPan()) {
        if (modelValue.etConfirmPassword?.equals(modelValue.etPassword) == true) {*/
      if(modelValue?.etGender != null) {
        if (mProfilePicUri != null)
          viewModel.imageUpload(ImageFolders.Profile, mProfilePicUri!!, mImageBytes)
        else {
          mImageUrl = viewModel.mCurrentProfile?.profileImageURl
          callupdateProfile()
        }
      } else{
          Snackbar.make(
            binding.root,
            "Please select gender",
            Snackbar.LENGTH_LONG
          ).show()
      }
      /*else
      Snackbar.make(
        binding.root,
        MyApp.getInstance().getString(R.string.panrerror),
        Snackbar.LENGTH_LONG
      ).show()
    }
      else
        Snackbar.make(
          binding.root,
          MyApp.getInstance().getString(R.string.adharerror),
          Snackbar.LENGTH_LONG
        ).show()*/
    }
  }

  private fun calculateAge(dob: Date): Int {
    var years = 0
    var months = 0
    var days = 0

    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val birthDate: Date = simpleDateFormat.parse(dob.toString());

    //create calendar object for birth day
    val birthDay = Calendar.getInstance()
    birthDay.timeInMillis = birthDate.time

    //create calendar object for current day
    val currentTime = System.currentTimeMillis()
    val now = Calendar.getInstance()
    now.timeInMillis = currentTime

    //Get difference between years
    years = now[Calendar.YEAR] - birthDay[Calendar.YEAR]
    val currMonth = now[Calendar.MONTH] + 1
    val birthMonth = birthDay[Calendar.MONTH] + 1

    //Get difference between months
    months = currMonth - birthMonth

    //if month difference is in negative then reduce years by one
    //and calculate the number of months.
    if (months < 0) {
      years--
      months = 12 - birthMonth + currMonth
      if (now[Calendar.DATE] < birthDay[Calendar.DATE]) months--
    } else if (months == 0 && now[Calendar.DATE] < birthDay[Calendar.DATE]) {
      years--
      months = 11
    }

    //Calculate the days
    if (now[Calendar.DATE] > birthDay[Calendar.DATE]) days =
      now[Calendar.DATE] - birthDay[Calendar.DATE] else if (now[Calendar.DATE] < birthDay[Calendar.DATE]) {
      val today = now[Calendar.DAY_OF_MONTH]
      now.add(Calendar.MONTH, -1)
      days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay[Calendar.DAY_OF_MONTH] + today
    } else {
      days = 0
      if (months == 12) {
        years++
        months = 0
      }
    }
    var age = years
    //Create new Age object
    return age
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

        viewModel.imageUploadLiveData.observe(this@EditProfileActivity) {
          if(it is SuccessResponse) {
            val response = it.getContentIfNotHandled()
            if(it.data.status!!) {
              mImageUrl = it.data.imagePath
              if (mImageUrl == null)
                mImageUrl = viewModel.mCurrentProfile?.profileImageURl ?: ""

              callupdateProfile()
            }else
              Snackbar.make(
                binding.root,
                it.data.message?:"Image is not uploaded",
                Snackbar.LENGTH_LONG
              ).show()

          } else if(it is ErrorResponse)  {
            onError(it.data ?: java.lang.Exception())
          }
        }
        viewModel.fetchGetEmpLiveData.observe(this) {
          if(it is SuccessResponse) {
            val response = it.getContentIfNotHandled()
            viewModel.bindEditEmployeeResponse(it.data.list?.get(0) ?: FetchEditEmployeeDetailsResponseListItem())
            setProofsLayout()
          } else if(it is ErrorResponse) {
            onError(it.data ?:Exception())
          }
        }

        viewModel.updateUserProfileLiveData.observe(this) {
          if(it is SuccessResponse) {
            val response = it.getContentIfNotHandled()
            Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
            val intent = UserdashboardActivity.getIntent(this, null)
            startActivity(intent)
            finish()
          } else if(it is ErrorResponse) {
            onError(it.data ?:Exception())
          }
        }
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

      private fun callupdateProfile() {
        var modelValue=viewModel.editProfileModel.value
       var request=UserProfileObject(
         empID = mLoginDetails?.userID,
         profileImageURl = mImageUrl,
         password = if(modelValue?.etPassword.isNullOrEmpty()) viewModel.mCurrentProfile?.password else modelValue?.etPassword,
         email = if(modelValue?.etEmail.isNullOrEmpty()) viewModel.mCurrentProfile?.email else modelValue?.etEmail,
         firstName =if(modelValue?.etFirstname.isNullOrEmpty()) viewModel.mCurrentProfile?.firstName else modelValue?.etFirstname,
         lastName = if(modelValue?.etLastName.isNullOrEmpty()) viewModel.mCurrentProfile?.lastName else modelValue?.etLastName,
         address = if(modelValue?.etAddress.isNullOrEmpty()) viewModel.mCurrentProfile?.address else modelValue?.etAddress,
         proofs = viewModel.mCurrentProfile?.proofs,
         gender = if(modelValue?.etGender.isNullOrEmpty()) viewModel.mCurrentProfile?.gender else modelValue?.etGender,
         fatherName = if(modelValue?.etFathername.isNullOrEmpty()) viewModel.mCurrentProfile?.fatherName else modelValue?.etFathername,
         DOB = if(modelValue?.etDOB.isNullOrEmpty()) viewModel.mCurrentProfile?.DOB else modelValue?.etDOB,
         education = if(modelValue?.etEducation.isNullOrEmpty()) viewModel.mCurrentProfile?.education else modelValue?.etEducation,
         Age = modelValue?.etAge?.toInt()
       )
        if(binding.etProof1Layout.visibility==View.VISIBLE)
        {
          request.proofs?.get(0)?.proofNumber=modelValue?.etUIDNum
        }
        if(binding.etProof2Layout.visibility==View.VISIBLE)
        {
          request.proofs?.get(1)?.proofNumber=modelValue?.etPanNum
        }
        if(binding.etProof3Layout.visibility==View.VISIBLE)
        {
          request.proofs?.get(2)?.proofNumber=modelValue?.etDLNum
        }
        viewModel.updateEmployeeDetails(request)
      }

      fun processUpdateProfile(){
        val modelValue=viewModel.editProfileModel.value
        if(validateProofs(modelValue!!)){

        }
      }

      private fun validateProofs(model:EditProfileModel): Boolean {
        if(model.etFirstname?.trim()?.isNotEmpty() == true)
        {
          return true
        }else

        return false
      }

      private fun setProofsLayout() {
        val modelValue=viewModel.editProfileModel.value
        if(!modelValue?.proofs.isNullOrEmpty()){
          val proof1= modelValue?.proofs?.get(0)
          if(proof1!=null){
            binding.etProof1Layout.visibility= View.VISIBLE
            binding.etProof1Layout.hint=proof1.proofTypeName
            binding.etProof1.setText(proof1.proofNumber)
          }

          if(modelValue?.proofs?.size!! >=2) {
            val proof2 = modelValue.proofs?.get(1)
            if (proof2 != null) {
              binding.etProof2Layout.visibility = View.VISIBLE
              binding.etProof2Layout.hint = proof2.proofTypeName
              binding.etProof2.setText(proof2.proofNumber)

            }
          }
          if(modelValue.proofs?.size!! >=3) {
            val proof3 = modelValue.proofs?.get(2)
            if (proof3 != null) {
              binding.etProof3Layout.visibility = View.VISIBLE
              binding.etProof3Layout.hint = proof3.proofTypeName
              binding.etProof3.setText(proof3.proofNumber)
            }
          }
        }
      }

      private fun onError(exception: Exception) {
        when (exception) {
          is
          NoInternetConnection -> {
            Snackbar.make(binding.root, exception.message ?: "", Snackbar.LENGTH_LONG).show()
          }

          is HttpException -> {
            val errorBody = exception.response()?.errorBody()?.string()
            val errorObject = if (errorBody != null && errorBody.isJSONObject())
              JSONObject(errorBody)
            else JSONObject()

            Snackbar.make(binding.root, MyApp.getInstance().getString(R.string.lbl_error), Snackbar.LENGTH_LONG).show()
          }
        }

      }

  companion object {
    const val TAG: String = "EDIT_PROFILE_ACTIVITY"
    public fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, EditProfileActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
  override fun onBackPressed() {
    val destIntent = UserdashboardActivity.getIntent(this,null)
    startActivity(destIntent)
    finish()
  }
}
