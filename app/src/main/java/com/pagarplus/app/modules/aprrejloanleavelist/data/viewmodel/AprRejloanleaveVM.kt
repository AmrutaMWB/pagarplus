package com.pagarplus.app.modules.aprrejloanleavelist.data.viewmodel

import android.net.ParseException
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.extensions.extractDateT
import com.pagarplus.app.extensions.extractDateWith12
import com.pagarplus.app.extensions.extractDateWithT
import com.pagarplus.app.modules.aprrejloanleavelist.data.model.AprRejloanleaveModel
import com.pagarplus.app.modules.aprrejloanleavelist.data.model.MessageListModel
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.*
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import kotlin.collections.MutableList
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AprRejloanleaveVM : ViewModel(), KoinComponent {
  val aprrejloanleaveModel: MutableLiveData<AprRejloanleaveModel> =
      MutableLiveData(AprRejloanleaveModel())

  var navArguments: Bundle? = null

  val messageList: MutableLiveData<MutableList<MessageListModel>> = MutableLiveData(mutableListOf())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  val userdetails = prefs.getLoginDetails<LoginResponse>()
    val prfoileDetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  var reqtime = ""
  var aprvedtime = ""
  var leavefromdate = ""
  var leavetodate = ""

    /*approve or reject dapi*/
    val approveLoanLiveData: MutableLiveData<Response<FetchGetloanListResponse>> =
        MutableLiveData<Response<FetchGetloanListResponse>>()

    val approveLeaveLiveData: MutableLiveData<Response<FetchGetleaveListResponse>> =
        MutableLiveData<Response<FetchGetleaveListResponse>>()

    val rejectLoanLiveData: MutableLiveData<Response<FetchGetloanListResponse>> =
        MutableLiveData<Response<FetchGetloanListResponse>>()

    val rejectLeaveLiveData: MutableLiveData<Response<FetchGetleaveListResponse>> =
        MutableLiveData<Response<FetchGetleaveListResponse>>()
    
    /*fetch all data*/

  val fetchallapproveLoanLiveData: MutableLiveData<Response<FetchGetloanListResponse>> =
      MutableLiveData<Response<FetchGetloanListResponse>>()

  val fetchallapproveLeaveLiveData: MutableLiveData<Response<FetchGetleaveListResponse>> =
      MutableLiveData<Response<FetchGetleaveListResponse>>()

    /*approve and Reject loan*/
    fun callApproveLoanApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            approveLoanLiveData.postValue(
                networkRepository.adminApproveLoan(
                    contentType = """application/json""",
                    approveLoanRequest = ApproveLoanRequest(
                        loanID = aprrejloanleaveModel.value?.requestId,
                        monthlyDeduction = aprrejloanleaveModel.value?.monthlyDeduction,
                        approvedAmount = aprrejloanleaveModel.value?.approvedLoan,
                        comment = aprrejloanleaveModel.value?.comment)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callReejctLoanApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            rejectLoanLiveData.postValue(
                networkRepository.adminRejectLoan(
                    contentType = """application/json""",
                    rejectLoanRequest = RejectLoanRequest(
                        loanID = aprrejloanleaveModel.value?.requestId,
                        adminID = userdetails?.userID,
                        comment = aprrejloanleaveModel.value?.comment)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    /*approve and reject leave*/
    fun callApproveLeaveApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            approveLeaveLiveData.postValue(
                networkRepository.adminApproveLeave(
                    contentType = """application/json""",
                    approveLeaveRequest = ApproveLeaveRequest(
                        requestID = aprrejloanleaveModel.value?.requestId,
                        adminID = userdetails?.userID,
                        comment = aprrejloanleaveModel.value?.comment,
                        approvedLeaveType = aprrejloanleaveModel.value?.approvedleaveType)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callReejctLeaveApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            rejectLeaveLiveData.postValue(
                networkRepository.adminRejectLeave(
                    contentType = """application/json""",
                    rejectLeaveRequest = ApproveLeaveRequest(
                        requestID = aprrejloanleaveModel.value?.requestId,
                        adminID = userdetails?.userID,
                        comment = aprrejloanleaveModel.value?.comment)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindApproveLeaveResponse(response: FetchGetleaveListResponse) {
        val aprrejloanleaveModelValue = aprrejloanleaveModel.value ?: AprRejloanleaveModel()
        aprrejloanleaveModel.value = aprrejloanleaveModelValue
    }

    fun bindRejectLeaveResponse(response: FetchGetleaveListResponse) {
        val approveloanleaveValue = aprrejloanleaveModel.value ?: AprRejloanleaveModel()
        aprrejloanleaveModel.value = approveloanleaveValue
    }

    fun bindApproveLoanResponse(response: FetchGetloanListResponse) {
        val aprrejloanleaveModelValue = aprrejloanleaveModel.value ?: AprRejloanleaveModel()
        aprrejloanleaveModel.value = aprrejloanleaveModelValue
    }

    fun bindRejecteLoanResponse(response: FetchGetloanListResponse) {
        val aprrejloanleaveModelValue = aprrejloanleaveModel.value ?: AprRejloanleaveModel()
        aprrejloanleaveModel.value = aprrejloanleaveModelValue
    }
    
    /*call all approved/rejected/pending leave list api*/
    fun callGetAllApprovedLeaveListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchallapproveLeaveLiveData.postValue(
                networkRepository.fetchAllApprovedLeaveList(
                    contentType = """application/json""",
                    fetchGetleaveloanListRequest = FetchGetleaveloanListRequest(
                        adminID = userdetails?.userID,
                        fromDate = aprrejloanleaveModel.value?.txtfromdate,
                        toDate = aprrejloanleaveModel.value?.txttodate,
                        branchID = aprrejloanleaveModel.value?.branchId,
                        deptID = aprrejloanleaveModel.value?.deptId)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    /*call all approved/rejected/pending loan list api*/
    fun callGetAllApprovedLoanListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchallapproveLoanLiveData.postValue(
                networkRepository.fetchAllApprovedLoanList(
                    contentType = """application/json""",
                    fetchGetleaveloanListRequest = FetchGetleaveloanListRequest(
                        adminID = userdetails?.userID,
                        fromDate = aprrejloanleaveModel.value?.txtfromdate,
                        toDate = aprrejloanleaveModel.value?.txttodate,
                        branchID = aprrejloanleaveModel.value?.branchId,
                        deptID = aprrejloanleaveModel.value?.deptId)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    /*bind leave list*/
    fun bindFetchGetleaveListResponse(response: FetchGetleaveListResponse) {
        val aprrejloanleaveModelValue = aprrejloanleaveModel.value ?: AprRejloanleaveModel()
        val recyclerMsglist = response.msgList?.map {

            val FromDate = it.fromDateTime?.extractDateT()
            val ToDate = it.toDateTime?.extractDateT()
            val reqDate = it.requestDate?.extractDateWithT()

            MessageListModel(
                txtEmpName = it.employeeName,
                txtMessage = it.reasonForLeave,
                txtloanordate = FromDate + " To " + ToDate,
                txtComment = it.adminComment,
                txtStatus = it.approveStatus,
                txtLeaveType = it.leaveType,
                txtAprvedDate = it.approvedDate?.extractDateWithT(),
                txtEmpID = it.empID,
                txtDatetime = reqDate,
                txtRequestID = it.requestID,
                organizationname = prfoileDetails?.organization.toString(),
                txtBranch = it.branch,
                txtDept = it.department,
                txtDesignation = it.designation,
                txtApprovedLeavetype = it.approvedLeaveType
            )
        }?.toMutableList()
        messageList.value = recyclerMsglist
        aprrejloanleaveModel.value = aprrejloanleaveModelValue
    }

    /*bind loan list*/
    fun bindFetchGetloanListResponse(response: FetchGetloanListResponse) {

        val aprrejloanleaveModelValue = aprrejloanleaveModel.value ?: AprRejloanleaveModel()
        val recyclerMsglist = response.msgList?.map {
            MessageListModel(
                txtEmpName = it.empName,
                txtMessage = it.reasonForLoan,
                txtloanordate = it.requestedAmount,
                txtComment = it.comment,
                txtStatus = it.approveStatus,
                txtAprvedDate = aprvedtime,
                txtAprvedAmount = it.approvedAmount,
                txtEmpID = it.empID,
                txtDatetime = it.requestDate?.extractDateWithT(),
                txtRequestID = it.loanID,
                txtSalary = it.salary,
                organizationname = prfoileDetails?.organization.toString(),
                txtBranch = it.branch,
                txtDept = it.department,
                txtMonthlyDeduction = it.monthlyDeduction,
                txtOldBal = it.oldLoanBalance,
                txtDesignation = it.designation,
                txtLoanType = it.loanType
            )
        }?.toMutableList()
        messageList.value = recyclerMsglist
        aprrejloanleaveModel.value = aprrejloanleaveModelValue
    }
}
