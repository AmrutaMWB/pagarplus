package com.pagarplus.app.modules.userlogin.`data`.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.userlogin.`data`.model.UserloginModel
import com.pagarplus.app.network.models.creategetlogindetail.CreateGetLoginDetailRequest
import com.pagarplus.app.network.models.creategetlogindetail.CreateGetLoginDetailResponse
import com.pagarplus.app.network.models.createtoken.CreateTokenRequest
import com.pagarplus.app.network.models.createtoken.CreateTokenResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlin.Boolean
import kotlin.Unit
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserloginVM : ViewModel(), KoinComponent {
  val userloginModel: MutableLiveData<UserloginModel> = MutableLiveData(UserloginModel())

  var navArguments: Bundle? = null
  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  val createTokenLiveData: MutableLiveData<Response<CreateTokenResponse>> =
      MutableLiveData<Response<CreateTokenResponse>>()

  private val prefs: PreferenceHelper by inject()

  val createGetLoginDetailLiveData: MutableLiveData<Response<CreateGetLoginDetailResponse>> =
      MutableLiveData<Response<CreateGetLoginDetailResponse>>()

  lateinit var googleAuthResponse: GoogleSignInAccount

  var facebookAuthResponse: JSONObject = JSONObject()

  val LogoutLiveData: MutableLiveData<Response<String>> = MutableLiveData<Response<String>>()

  fun callCreateTokenApi(): Unit {
    viewModelScope.launch {
      val request=CreateTokenRequest()
      request.username=userloginModel.value?.etUseridValue?.trim().toString()
      request.password=userloginModel.value?.etUserpasswordValue?.trim().toString()

      Log.e("credintial","$request")
      progressLiveData.postValue(true)
      createTokenLiveData.postValue(networkRepository.createToken(request))
      progressLiveData.postValue(false)
    }
  }

  fun callCreateGetLoginDetailApi(): Unit {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      createGetLoginDetailLiveData.postValue(
      networkRepository.createGetLoginDetail(
      authorization ="bearer ${prefs.getAccess_token()}",
      createGetLoginDetailRequest = CreateGetLoginDetailRequest(
        accessToken = "",//prefs.getAccess_token(),
        mobile = userloginModel.value?.etUseridValue,
        password = userloginModel.value?.etUserpasswordValue,
        deviceID = userloginModel.value?.deviceIDIMEI,
        deviceToken = userloginModel.value?.firebaseToken)))
      progressLiveData.postValue(false)
    }
  }

  fun saveRememberMe(mobile:String?,password: String?,isSaved:Boolean): Unit {
    prefs.setMobile(mobile)
    prefs.setPassword(password)
    prefs.putloginsave(isSaved)
  }

  fun bindCreateTokenResponse(response: CreateTokenResponse): Unit {
    val userloginModelValue = userloginModel.value ?:UserloginModel()
    prefs.setAccess_token(response.accessToken)
    userloginModel.value = userloginModelValue
  }

  /*logout api*/
  fun UserLogout(deviceID: String) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      LogoutLiveData.postValue(
        networkRepository.userLogout(deviceID)
      )
      progressLiveData.postValue(false)
    }
  }
}
