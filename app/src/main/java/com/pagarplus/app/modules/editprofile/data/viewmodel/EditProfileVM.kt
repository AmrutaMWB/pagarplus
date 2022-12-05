package com.pagarplus.app.modules.editprofile.`data`.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.editemployeedetails.data.model.EditemployeedetailsModel
import com.pagarplus.app.modules.editprofile.`data`.model.EditProfileModel
import com.pagarplus.app.network.models.adminEditEmpdata.AdminEditEmployeeResponse
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItem
import com.pagarplus.app.network.models.adminEditEmpdata.toEditemployeedetailsModel
import com.pagarplus.app.network.models.adminEditEmpdata.toUserProfile
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.editprofile.UserProfileObject
import com.pagarplus.app.network.models.others.ApiResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class EditProfileVM : ViewModel(), KoinComponent {
  val editProfileModel: MutableLiveData<EditProfileModel> = MutableLiveData(EditProfileModel())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  val fetchGetEmpLiveData: MutableLiveData<Response<AdminEditEmployeeResponse>> =
    MutableLiveData<Response<AdminEditEmployeeResponse>>()

  val updateUserProfileLiveData: MutableLiveData<Response<ApiResponse>> = MutableLiveData<Response<ApiResponse>>()

  val imageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()
  var mCurrentProfile:FetchEditEmployeeDetailsResponseListItem?=null
  var navArguments: Bundle? = null
  var profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  fun callFetchGetEmpDataApi(id:Int) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      fetchGetEmpLiveData.postValue(
        networkRepository.admingetEditEmpData(
          empID = id
        )
      )
      progressLiveData.postValue(false)
    }
  }
  fun bindEditEmployeeResponse(response: FetchEditEmployeeDetailsResponseListItem): Unit {
    var geteditEmployeeModelValue = editProfileModel.value ?: EditProfileModel()
    mCurrentProfile=response
    geteditEmployeeModelValue=response.toUserProfile()
    editProfileModel.value  = geteditEmployeeModelValue
  }

  fun imageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      imageUploadLiveData.postValue(
        networkRepository.uploadFile(folder,uri,imgsize)
      )
      progressLiveData.postValue(false)
    }
  }

  fun updateEmployeeDetails(request:UserProfileObject){
    viewModelScope.launch {
      progressLiveData.postValue(true)
      updateUserProfileLiveData.postValue(
        networkRepository.updateUserProfile(
         request
        )
      )
      progressLiveData.postValue(false)
    }
  }
}
