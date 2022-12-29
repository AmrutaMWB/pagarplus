package com.pagarplus.app.modules.userlogin.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.databinding.ActivityUserloginBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.editprofile.ui.EditProfileActivity
import com.pagarplus.app.modules.signup.ui.SignUpActivity
import com.pagarplus.app.modules.userdashboard.ui.UserdashboardActivity
import com.pagarplus.app.modules.userlogin.data.viewmodel.UserloginVM
import com.pagarplus.app.network.models.creategetlogindetail.CreateGetLoginDetailResponse
import com.pagarplus.app.network.models.createtoken.CreateTokenResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.HttpException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

class UserloginActivity : BaseActivity<ActivityUserloginBinding>(R.layout.activity_userlogin){
  private val prefs: PreferenceHelper by inject()
  private var cipher: Cipher? = null
  private var fingerprintManager: FingerprintManager? = null
  private var keyguardManager: KeyguardManager? = null
  private var keyStore: KeyStore? = null
  private var keyGenerator: KeyGenerator? = null
  private var cryptoObject: FingerprintManager.CryptoObject? = null
  private val viewModel: UserloginVM by viewModels<UserloginVM>()
  var mDialogSubCategory: Dialog? = null
  private val REQUEST_CODE = 101
  private var auth: FirebaseAuth = Firebase.auth
  var customToken: String = ""

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.userloginVM = viewModel

    /** Remember ME operation**/
    if(prefs.getloginsave()) {
      viewModel.userloginModel.value!!.etUseridValue = prefs.getMobile() ?:""
      viewModel.userloginModel.value!!.etUserpasswordValue = prefs.getPassword() ?:""
      binding.checkBoxRememberMe.isChecked = true
    }
    telephonyService()
    firabaseToken()
  }

  /*get device(Phone) ID for autologout option*/
  private fun telephonyService() {
    val uid: String = Settings.Secure.getString(this.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
    val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
    var imei: String? = ""
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
      imei = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
          uid
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
          telephonyManager.imei
        }
        else -> {
          telephonyManager.deviceId
        }
      }
      viewModel.userloginModel.value?.deviceIDIMEI = imei
      Log.e("IMEI NUmber", imei.toString())
      prefs.setDeviceID(imei)
    }
  }

  /*get firebase token and send to server*/
  private fun firabaseToken(){
    /*getToken from firebase to send push notification*/
    val token = FirebaseInstanceId.getInstance().getToken()
    Log.e(TAG, token.toString())
    /*saving token in firebase database*/
    FirebaseDatabase.getInstance().getReference().child("Tokens").setValue(token)
    viewModel.userloginModel.value?.firebaseToken = token
  }

  // in the below line, we are calling on request permission result method.
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == REQUEST_CODE) {
      // in the below line, we are checking if permission is granted.
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // if permissions are granted we are displaying below toast message.
        telephonyService()
      } else {
        // in the below line, we are displaying toast message if permissions are not granted.
        Toast.makeText(this, R.string.msg_permission_deny, Toast.LENGTH_SHORT).show()
      }
    }
  }

    override fun setUpClicks(): Unit {
        binding.btnLogin.setOnClickListener {
          var phone=viewModel.userloginModel.value?.etUseridValue?.trim().toString()
          var password=viewModel.userloginModel.value?.etUserpasswordValue?.trim().toString()
          if(phone.isPhone()){
            if(password.isNotEmpty()){
              //viewModel.callCreateTokenApi()
              viewModel.callCreateGetLoginDetailApi()
            }else
              Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_enter_password), Snackbar.LENGTH_LONG).show()
          }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.msg_please_enter_valid_phone_nu), Snackbar.LENGTH_LONG).show()
        }

      binding.btnSignUp.setOnClickListener {
        val destintent=SignUpActivity.getIntent(this,null)
        startActivity(destintent)
      }
    }

      override fun addObservers() {
        var progressDialog : AlertDialog? = null
        viewModel.progressLiveData.observe(this@UserloginActivity) {
          if(it) {
            progressDialog?.dismiss()
            progressDialog = null
            progressDialog = this@UserloginActivity.showProgressDialog()
          } else  {
            progressDialog?.dismiss()
          }
        }
        viewModel.createTokenLiveData.observe(this@UserloginActivity) {
          if(it is SuccessResponse) {
            val response = it.getContentIfNotHandled()
            onSuccessCreateToken(it)
          } else if(it is ErrorResponse)  {
            onError(it.data ?:Exception())
          }
        }
        viewModel.createGetLoginDetailLiveData.observe(this@UserloginActivity) {
          if(it is SuccessResponse) {
            val response = it.getContentIfNotHandled()
            onSuccessCreateGetLoginDetail(it)
          } else if(it is ErrorResponse)  {
            onError(it.data ?:Exception())
          }
        }

        viewModel.LogoutLiveData.observe(this) {
          viewModel.callCreateGetLoginDetailApi()
        }
      }

      private fun onSuccessCreateToken(response: SuccessResponse<CreateTokenResponse>) {
        viewModel.bindCreateTokenResponse(response.data)
        viewModel.callCreateGetLoginDetailApi()
      }

      private fun onSuccessCreateGetLoginDetail(response: SuccessResponse<CreateGetLoginDetailResponse>) {
        if(!response.data.loginCustomerData.isNullOrEmpty()) {
          if (binding.checkBoxRememberMe.isChecked) {
            viewModel.saveRememberMe(
              mobile = viewModel.userloginModel.value?.etUseridValue,
              password = viewModel.userloginModel.value?.etUserpasswordValue,
              isSaved = true)
          } else {
            viewModel.saveRememberMe("null", "null", false)
          }
          if (response.data.loginCustomerData[0]?.isAdmin == true) {
            prefs.setIsAdmin(true)
            val intent = AdmindashboardActivity.getIntent(this, null)
            startActivity(intent)
          } else {
            prefs.setIsAdmin(false)
            if(prefs.getFirstLoginTrue() == true){
              prefs.setFirstLoginTrue(false)
              val intent = EditProfileActivity.getIntent(this, null)
              startActivity(intent)
            }else {
              val intent = UserdashboardActivity.getIntent(this, null)
              startActivity(intent)
            }
          }
          prefs.setLoginTrue(true)
          prefs.setLoginDetails(response.data.loginCustomerData[0])
          lifecycleScope.launch {
            ApiUtil(applicationContext).setProfileDetails(response.data.loginCustomerData[0]?.userID ?: 0)
          }
          finish()
        }else
          this.alert("","${response.`data`.Message}") {
            neutralButton {
              it.dismiss()
              viewModel.UserLogout(response.data.deviceID.toString())
            }
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
            Snackbar.make(binding.root, "Invalid mobile number or password", Snackbar.LENGTH_LONG).show()
          }
        }
      }

      companion object {
        const val TAG: String = "USERLOGIN_ACTIVITY"

        fun getIntent(context: Context, bundle: Bundle?): Intent {
          val destIntent = Intent(context, UserloginActivity::class.java)
          destIntent.putExtra("bundle", bundle)
          return destIntent
        }
      }
    }
