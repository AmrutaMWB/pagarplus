package com.pagarplus.app.modules.signup.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.databinding.ActivitySignUpBinding
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.appcomponents.views.TimePickerFragment
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.ItemlistDialog
import com.pagarplus.app.modules.itemlistdialog.ui.RecyclerItemlistdialogAdapter
import com.pagarplus.app.modules.signup.data.viewmodel.SignUpVM
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.network.models.createsignup.CreateSignUpResponse
import java.lang.Exception
import kotlin.String
import kotlin.Unit
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : BaseActivity<ActivitySignUpBinding>(R.layout.activity_sign_up),ItemlistDialog.OnCompleteListener {
  private val viewModel: SignUpVM by viewModels<SignUpVM>()
  lateinit var detailsAdapter : RecyclerItemlistdialogAdapter
  var strOTP: String? = "0000"

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.signUpVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.btnBack.setOnClickListener {
      val intent = UserloginActivity.getIntent(this, null)
      finish()
      startActivity(intent)
    }

    binding.btnRegSubmit.setOnClickListener {
      if(isValidate()){
        this@SignUpActivity.hideKeyboard()
        viewModel.callCreateSignUpApi()
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
          binding.outlinedSateField.setError("Please select State")
          binding.etCity.isEnabled = false
        }else{
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

    binding.etFirmName.addTextChangedListener(TextFieldValidation(binding.etFirmName))
    binding.etMobile.addTextChangedListener(TextFieldValidation(binding.etMobile))
    binding.etPassword.addTextChangedListener(TextFieldValidation(binding.etPassword))
    binding.etConfirmPassword.addTextChangedListener(TextFieldValidation(binding.etConfirmPassword))
    binding.etOffOpenTime.addTextChangedListener(TextFieldValidation(binding.etOffOpenTime))
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

  /*OTP dialog*/
  fun showConfirmDialog() {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()
    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.otp_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatImageView>(R.id.iv_cross)
    val txt_resend = dialogView.findViewById<AppCompatTextView>(R.id.tv_resend_otp)
    val txt_ok = dialogView.findViewById<AppCompatTextView>(R.id.tv_ok_otp)
    val txt_phone = dialogView.findViewById<AppCompatTextView>(R.id.tv_phone_no)
    val edt_otp1 = dialogView.findViewById<AppCompatEditText>(R.id.et_otp1)
    val edt_otp2 = dialogView.findViewById<AppCompatEditText>(R.id.et_otp2)
    val edt_otp3 = dialogView.findViewById<AppCompatEditText>(R.id.et_otp3)
    val edt_otp4 = dialogView.findViewById<AppCompatEditText>(R.id.et_otp4)
    val txtchange = dialogView.findViewById<AppCompatTextView>(R.id.tv_change)
    val alertDialog = dialogBuilder.create()
    //In Android, AlertDialog insert into another container, to avoid that , we need to make back ground transparent

    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()

    txt_phone.setText(viewModel.signUpModel.value?.etMobileValue)
    for (i in 0..strOTP!!.length-1) {
      val str: CharSequence = strOTP!![i].toString()
      if(i == 0) {
        edt_otp1.setText(str)
      }else if(i == 1) {
        edt_otp2.setText(str)
      }else if(i == 2) {
        edt_otp3.setText(str)
      }else{
        edt_otp4.setText(str)
      }
    }

    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }

    txtchange.setOnClickListener {
      alertDialog.dismiss()
    }

    txt_ok.setOnClickListener {
      alertDialog.dismiss()
      alertDialog.dismiss()
      val destIntent = UserloginActivity.getIntent(applicationContext, null)
      finish()
      startActivity(destIntent)
    }

    txt_resend.setOnClickListener {
      // TODO please, add your navigation code here
      viewModel.callSendOTP()
      alertDialog.dismiss()
    }
  }

  /*validate eidttext fields*/
  private fun isValidate(): Boolean =
    validateRequired(binding.outlinedFirmField,binding.etFirmName) &&
            validateMobile(binding.outlinedMobileField,binding.etMobile) &&
            validatePassword(binding.outlinedPasswordField,binding.etPassword) &&
            validateConfirmPassword(binding.outlinedCnfPwdField,binding.etConfirmPassword,binding.etPassword) &&
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
        R.id.etPassword -> {
          validatePassword(binding.outlinedPasswordField,binding.etPassword)
        }
        R.id.etConfirmPassword -> {
          validateConfirmPassword(binding.outlinedCnfPwdField,binding.etConfirmPassword,binding.etPassword)
        }
      }
    }
  }

  override fun addObservers(): Unit {
    var progressDialog: AlertDialog? = null
    viewModel.progressLiveData.observe(this@SignUpActivity) {
      if (it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@SignUpActivity.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }

    viewModel.createSignUpLiveData.observe(this@SignUpActivity) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateSignUp(it)
      } else if (it is ErrorResponse) {
        onErrorMessage(it.data ?: Exception())
      }
    }

    viewModel.GetOTPLiveData.observe(this@SignUpActivity) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        strOTP = response.toString()
        showConfirmDialog()
      } else if (it is ErrorResponse) {
        onErrorMessage(it.data ?: Exception())
      }
    }
  }

  private fun onSuccessCreateSignUp(response: SuccessResponse<CreateSignUpResponse>): Unit {
    Log.e("Sattus",response.toString());
    this@SignUpActivity.alert("","${response.`data`.message}") {
      neutralButton {
        if (response.data.status == true) {
          viewModel.callSendOTP()
        }else{
          it.dismiss()
        }
      }
    }
    viewModel.bindCreateSignUpResponse(response.data)
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
        this@SignUpActivity.alert(MyApp.getInstance().getString(R.string.lbl_register_status),errMessage) {
          neutralButton {
            dialogInterface ->
          }
        }
      }
    }
  }
  companion object {
    const val TAG: String = "SIGN_UP_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, SignUpActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
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
}
