package com.pagarplus.app.modules.applylol.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.modules.applylol.data.model.ApplylolModel
import com.pagarplus.app.modules.applylol.data.model.LeaveModel
import com.pagarplus.app.modules.applylol.data.model.LoanModel
import com.pagarplus.app.modules.aprrejloanleavelist.data.model.MessageListModel
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetleaveListResponse
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetleaveloanListRequest
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetloanListResponse
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.leavelone.LeaveRequest
import com.pagarplus.app.network.models.leavelone.LoanRequest
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ApplylolVM : ViewModel(), KoinComponent {
  val applylolModel: MutableLiveData<ApplylolModel> = MutableLiveData(ApplylolModel())

  var navArguments: Bundle? = null

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  val recyclerLoanList: MutableLiveData<MutableList<LoanModel>> = MutableLiveData(mutableListOf())
  val recyclerLeaveList: MutableLiveData<MutableList<LeaveModel>> = MutableLiveData(mutableListOf())

  private var userdetails=prefs.getLoginDetails<LoginResponse>()!!
  val createRequestLeaveLiveData: MutableLiveData<Response<RetroResponse>> =
      MutableLiveData<Response<RetroResponse>>()
  val loanListLiveData: MutableLiveData<Response<FetchGetloanListResponse>> =
    MutableLiveData<Response<FetchGetloanListResponse>>()

  val leaveListLiveData: MutableLiveData<Response<FetchGetleaveListResponse>> =
    MutableLiveData<Response<FetchGetleaveListResponse>>()

  val createRequestLoanLiveData: MutableLiveData<Response<RetroResponse>> =
      MutableLiveData<Response<RetroResponse>>()

  fun callCreateRequestLoanApi(request: LoanRequest) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      createRequestLoanLiveData.postValue(
      networkRepository.requestLoan(request))
      progressLiveData.postValue(false)
    }
  }


  fun callCreateRequestLeaveApi(request: LeaveRequest): Unit {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      createRequestLeaveLiveData.postValue(
      networkRepository.requestLeave(request)
      )
      progressLiveData.postValue(false)
    }
  }

  fun getLoanList(request:FetchGetleaveloanListRequest) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      loanListLiveData.postValue(
        networkRepository.fetchAllApprovedLoanList(
          contentType = """application/json""",
          fetchGetleaveloanListRequest = request))
      progressLiveData.postValue(false)
    }
  }
  fun getLeaveList(request:FetchGetleaveloanListRequest) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      leaveListLiveData.postValue(
        networkRepository.fetchAllApprovedLeaveList(
          contentType = """application/json""",
          fetchGetleaveloanListRequest = request
        )
      )
      progressLiveData.postValue(false)
    }
  }

  fun bindLeaveListResponse(responseData:FetchGetleaveListResponse){
   responseData.msgList=responseData.msgList?.filter { it.empID?:0==userdetails.userID }
    val leavelist=responseData.msgList?.map{
    LeaveModel(
      RequestID = it.requestID?:0,
      EmpID = it.empID?:0,
      LeaveType = it.leaveType?:"",
      ApproveStatus = it.approveStatus?:"",
      RequestDate = it.requestDate?.extractDate(),
      FromDateTime = "From : ${it.fromDateTime?.extractDate()}",
      ToDateTime = "To : ${it.toDateTime?.extractDate()}",
      ReasonForLeave = it.reasonForLeave?:"",
      AdminComment = it.adminComment?:"",
      ApprovedDate = it.approvedDate?.extractDate()) }?.toMutableList()

    recyclerLeaveList.value=leavelist

  }

  fun bindLoanListResponse(responseData:FetchGetloanListResponse){
    responseData.msgList=responseData.msgList?.filter { it.empID?:0==userdetails.userID }
    val loanlist=responseData.msgList?.map{
      LoanModel(
        EmpID = it.empID?:0,
        ApproveStatus = it.approveStatus?:"",
        RequestDate = it.requestDate?.extractDate(),
        RequestedAmount ="Requested :${it.requestedAmount?:0}",
        ApprovedAmount = "Approved :${it.approvedAmount?:0}",
        Comment = it.comment?:"",
        ReasonForLoan = it.reasonForLoan?:"",
      LoanType = it.loanType?:"Loan")
        }?.toMutableList()

    recyclerLoanList.value=loanlist
  }

}
