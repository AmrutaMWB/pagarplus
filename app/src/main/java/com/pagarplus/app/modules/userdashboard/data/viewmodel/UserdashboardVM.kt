package com.pagarplus.app.modules.userdashboard.`data`.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.userdashboard.data.model.DrawerItemUsermenuModel
import com.pagarplus.app.modules.userdashboard.`data`.model.UserdashboardModel
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.userdashboard.BannerResponse
import com.pagarplus.app.network.models.userdashboard.UserBannerResponseItem
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlin.Boolean
import kotlin.collections.List
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserdashboardVM : ViewModel(), KoinComponent {
  val userdashboardModel: MutableLiveData<UserdashboardModel> =
      MutableLiveData(UserdashboardModel())

  var navArguments: Bundle? = null

  public var includedModel: MutableLiveData<DrawerItemUsermenuModel> =
      MutableLiveData(DrawerItemUsermenuModel())


  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  val LogoutLiveData: MutableLiveData<Response<String>> = MutableLiveData<Response<String>>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

    val userdetails = prefs.getLoginDetails<LoginResponse>()
    var profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  val fetch1LiveData: MutableLiveData<Response<CreateCreateBannerResponse>> = MutableLiveData<Response<CreateCreateBannerResponse>>()

   suspend fun callFetch1Api(id:Int) {
      progressLiveData.postValue(true)
      fetch1LiveData.postValue(
        networkRepository.advertiseList(id,"",profiledetails?.branchID,profiledetails?.departmentID)
      )
      progressLiveData.postValue(false)
    }

    /*logout api*/
    fun callUserLogout(accessToken: String) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            LogoutLiveData.postValue(
                networkRepository.userLogout(userdetails?.userID!!)
            )
            progressLiveData.postValue(false)
        }
    }
}
