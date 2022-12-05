package com.pagarplus.app.modules.notificationcreatemessage.`data`.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel
import com.pagarplus.app.modules.notificationcreatemessage.data.model.NotificationCreateMessageModel
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponse
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createMessage.CreateMsgRequest
import com.pagarplus.app.network.models.createMessage.CreateMsgResponse
import com.pagarplus.app.network.models.createMessage.EmpListItem
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class NotificationCreateMessageVM : ViewModel(), KoinComponent {
  val notificationCreateMessageModel: MutableLiveData<NotificationCreateMessageModel> =
      MutableLiveData(NotificationCreateMessageModel())

  var navArguments: Bundle? = null

    val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    private val networkRepository: NetworkRepository by inject()

    private val prefs: PreferenceHelper by inject()

    val createMessageLiveData: MutableLiveData<Response<CreateMsgResponse>> =
        MutableLiveData<Response<CreateMsgResponse>>()

    val imageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

    /*emplist in recyclerview dialog*/
    val detailsList: MutableLiveData<MutableList<Details2RowModel>> = MutableLiveData(mutableListOf())

    val fetchGetEmpLiveData: MutableLiveData<Response<GetEmpviaDeptListResponse>> =
        MutableLiveData<Response<GetEmpviaDeptListResponse>>()

    val userdetails = prefs.getLoginDetails<LoginResponse>()

    val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

    fun callFetchGetEmpListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchGetEmpLiveData.postValue(
                networkRepository.fetchEmpViaDeptList(
                    adminID = userdetails?.userID,
                    deptID = notificationCreateMessageModel.value?.txtDeptID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchGetEmpListResponse(response: GetEmpviaDeptListResponse) {

        val adminemplistModelValue = notificationCreateMessageModel.value ?: NotificationCreateMessageModel()
        val recyclerMsglist = response.empList1?.map {
            Details2RowModel(
                txtName = it?.name,
                txtEmpID = it?.Id,
                txtChecked = notificationCreateMessageModel.value?.EmpIdlist?.contains(
                    EmpListItem(it?.Id))
            )
        }?.toMutableList()
        detailsList.value = recyclerMsglist
        notificationCreateMessageModel.value = adminemplistModelValue
    }

    fun ImageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            imageUploadLiveData.postValue(
                networkRepository.uploadFile(folder,uri,imgsize)
            )
            progressLiveData.postValue(false)
        }
    }

    fun callCreateMessageApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            createMessageLiveData.postValue(
                networkRepository.createMessage(
                    contentType = """application/json""",
                    createMessageRequest = CreateMsgRequest(
                        fromEmployee = userdetails?.userID,
                        empList = notificationCreateMessageModel.value?.EmpIdlist,
                        message = notificationCreateMessageModel.value?.txtDescription,
                        imagePath = notificationCreateMessageModel.value?.txtImagePath)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindCreateMessageResponse(response: CreateMsgResponse) {
        val createMessageModelValue = notificationCreateMessageModel.value ?: NotificationCreateMessageModel()
        notificationCreateMessageModel.value = createMessageModelValue
    }
}
