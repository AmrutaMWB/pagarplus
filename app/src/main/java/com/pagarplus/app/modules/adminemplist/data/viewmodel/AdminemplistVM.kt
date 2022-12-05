package com.pagarplus.app.modules.adminemplist.`data`.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.adminemplist.data.model.AdminemplistModel
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.modules.notification.data.model.MessageRowModel
import com.pagarplus.app.modules.notification.data.model.NotificationModel
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.notificationMsg.FetchInOutMsgListResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import kotlin.collections.MutableList
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

class AdminemplistVM : ViewModel(), KoinComponent {
  val adminemplistModel: MutableLiveData<AdminemplistModel> = MutableLiveData(AdminemplistModel())

  var navArguments: Bundle? = null

  val detailsList: MutableLiveData<MutableList<DetailsRowModel>> = MutableLiveData(mutableListOf())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()
  val userdetails = prefs.getLoginDetails<LoginResponse>()
  val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  val fetchGetEmpLiveData: MutableLiveData<Response<FetchGetEmpListResponse>> =
    MutableLiveData<Response<FetchGetEmpListResponse>>()

  val fetchDeleteEmpLiveData: MutableLiveData<Response<FetchGetEmpListResponse>> =
    MutableLiveData<Response<FetchGetEmpListResponse>>()

  val fetchActiveEmpLiveData: MutableLiveData<Response<FetchGetEmpListResponse>> =
    MutableLiveData<Response<FetchGetEmpListResponse>>()

  fun callFetchGetEmpListApi() {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      fetchGetEmpLiveData.postValue(
        networkRepository.fetchAdminEmpList(
          adminID = userdetails?.userID,
          branchID = adminemplistModel.value?.txtBranchId,
          deptID = adminemplistModel.value?.txtDeptId,
          listType = adminemplistModel.value?.filetrType
        )
      )
      progressLiveData.postValue(false)
    }
  }

  fun callDeleteEmpApi(empId: Int?) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      fetchDeleteEmpLiveData.postValue(
        networkRepository.adminDeleteEmp(
          empID = empId,
          reason = adminemplistModel.value?.comment,
          existDate =adminemplistModel.value?.existDate,
        )
      )
      progressLiveData.postValue(false)
    }
  }

  fun callActiveEmpApi(empId: Int?) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      fetchActiveEmpLiveData.postValue(
        networkRepository.adminActivateEmp(
          empID = empId,
        )
      )
      progressLiveData.postValue(false)
    }
  }

  fun bindFetchGetEmpListResponse(response: FetchGetEmpListResponse) {

    val adminemplistModelValue = adminemplistModel.value ?: AdminemplistModel()
    val recyclerMsglist = response.empList?.map {
      val slno: Int? = response.empList?.indexOf(it)?.plus(1)
      DetailsRowModel(
        txtName = it?.name,
        txtDesignation = it?.designation,
        imgPath = it?.profileImage,
        txtEmpID = it?.Id,
        txtSlNo = slno.toString(),
        txtPhone = it?.mobile,
        txtUsername = it?.userName,
        txtPassword = it?.password,
        txtBranch = it?.branch,
        txtDepartment = it?.department,
        organizationname = profiledetails?.organization,
        status = it?.isActive
      )
    }?.toMutableList()
    detailsList.value = recyclerMsglist
    adminemplistModel.value = adminemplistModelValue
  }
}
