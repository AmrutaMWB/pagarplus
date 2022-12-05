package com.pagarplus.app.modules.splashscreen.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.biometricauthentication.BiometricAuthentication
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.ApiUtil
import com.pagarplus.app.extensions.appendLog
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.splashscreen.data.model.SplashModel
import com.pagarplus.app.modules.splashscreen.data.viewmodel.splashVM
import com.pagarplus.app.modules.userdashboard.ui.UserdashboardActivity
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import org.koin.android.ext.android.inject
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: splashVM
    val pref: PreferenceHelper by inject()
    private val REQUEST_READ_CONTACTS_PERMISSION = 1
    val mLoginDetails = pref.getLoginDetails<LoginResponse>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        initViewModel()
        requestContactsPermission()
        appendLog("Splash")
    }

    private fun initViewModel() {
        splashViewModel = ViewModelProvider(this).get(splashVM::class.java)
    }

    private fun observeSplashLiveData() {
        splashViewModel.initSplashScreen()
        val observer = Observer<SplashModel> {
            if(pref.getLoginTrue() == true){
                lifecycleScope.launchWhenCreated {
                    ApiUtil(applicationContext).setProfileDetails(mLoginDetails?.userID ?: 0)
                }
                if(pref.getIsAdmin() == true) {
                    //checkBioMetric(true)
                    val intent = AdmindashboardActivity.getIntent(this, null)
                    startActivity(intent)
                    finish()
                }else{
                    //checkBioMetric(false)
                    val intent = UserdashboardActivity.getIntent(this, null)
                    startActivity(intent)
                    finish()
                }
            }else{
                val intent=UserloginActivity.getIntent(this,null)
                startActivity(intent)
                finish()
            }
        }
        splashViewModel.liveData.observe(this, observer)
    }

    fun checkBioMetric(isAdmin: Boolean) {
        val biometric = BiometricAuthentication(this, {
            val type = it.cryptoObject?.signature
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
            if(isAdmin){
                val intent = AdmindashboardActivity.getIntent(this, null)
                startActivity(intent)
                finish()
            }else{
                val intent = UserdashboardActivity.getIntent(this, null)
                startActivity(intent)
                finish()
            }
        }, onError = { message, errorCode ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
        if (biometric.canAuthenticate() == 0) {
            biometric.handleCanAuthenticateResult(
                biometric.canAuthenticate(),
                "Unlock WBRetailer",
                "Place Your Finger",
                "cancel"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestContactsPermission() {
        if (!hasContactsPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_MMS,
                    Manifest.permission.RECEIVE_WAP_PUSH,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.USE_FINGERPRINT,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.USE_FINGERPRINT,
                    ), REQUEST_READ_CONTACTS_PERMISSION
            )
        }else{
            observeSplashLiveData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_WAP_PUSH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION && grantResults.size > 0) {
            Log.e("Permission","Permission Granted")
            observeSplashLiveData()
        }
    }
}
