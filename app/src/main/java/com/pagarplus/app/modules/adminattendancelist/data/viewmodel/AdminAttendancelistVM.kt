package com.pagarplus.app.modules.adminattendancelist.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.extensions.extractDateWith12
import com.pagarplus.app.extensions.extractTime
import com.pagarplus.app.modules.adminattendancelist.data.model.AdminAttendancelistModel
import com.pagarplus.app.modules.adminattendancelist.data.model.AttendanceRowModel
import com.pagarplus.app.modules.adminemplist.data.model.AdminemplistModel
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.network.models.AdminDashboard.AdminFetchEmpAttendanceListResponse
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponse
import com.pagarplus.app.network.models.attendance.AttedanceApproveRejectRequest
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import kotlin.collections.MutableList
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.pagarplus.app.network.repository.NetworkRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin

class AdminAttendancelistVM : ViewModel(), KoinComponent {
  val adminAttendancelistModel: MutableLiveData<AdminAttendancelistModel> =
      MutableLiveData(AdminAttendancelistModel())
    var chkintime = ""
    var chkouttime = ""
  var navArguments: Bundle? = null

  private val networkRepository: NetworkRepository by inject()

  val attendList: MutableLiveData<MutableList<AttendanceRowModel>> =
      MutableLiveData(mutableListOf())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  private val prefs: PreferenceHelper by inject()
  val userdetails = prefs.getLoginDetails<LoginResponse>()
  val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  val fetchGetEmpAttendanceLiveData: MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>> =
        MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>>()

    val approveAttendanceLiveData: MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>> =
        MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>>()

    val rejectAttendanceLiveData: MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>> =
        MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>>()

    fun callFetchGetEmpAttendanceListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchGetEmpAttendanceLiveData.postValue(
                networkRepository.adminGetAttendance(
                    adminID = userdetails?.userID,
                    branchID = adminAttendancelistModel.value?.txtBranchId,
                    seldate = adminAttendancelistModel.value?.txtDate,
                    listType = adminAttendancelistModel.value?.txtListType,
                    deptID = adminAttendancelistModel.value?.txtDeptId
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchAttendanceListResponse(response: AdminFetchEmpAttendanceListResponse) {
        val adminemplistModelValue = adminAttendancelistModel.value ?: AdminAttendancelistModel()
        val recyclerMsglist = response.attendanceList?.map {
            var logdata=it?.loginData
            var Fin=""
            var Fout=""
            var Sin=""
            var Sout=""
            var attendanceid: Int = 0
            if(!logdata.isNullOrEmpty()){
                attendanceid = logdata[0]?.atendanceid!!
                if(logdata[0]?.sessionTypeName=="First Half"){
                    Fin = logdata[0]?.checkInDate?.extractTime().toString()
                    Fout = logdata[0]?.checkOutDate?.extractTime().toString()
                }else if(logdata[0]?.sessionTypeName=="Second Half"){
                    Sin = logdata[0]?.checkInDate?.extractTime().toString()
                    Sout = logdata[0]?.checkOutDate?.extractTime().toString()
                }else {
                    Fin = logdata[0]?.checkInDate?.extractTime().toString()
                    Sout = logdata[0]?.checkOutDate?.extractTime().toString()
                }
            }
            AttendanceRowModel(
                txtbranch = it?.branch,
                txtDept = it?.department,
                txtempid = it?.employeeID,
                txtEmpName = it?.employeeName,
                txtType = it?.type,
                txtMobilenumber = it?.contactNumber,
                organizationname = profiledetails?.organization,
                txtAttendanceID = attendanceid.toInt(),
                txtVisit = it?.visitTypeName,
                txtDuration = it?.sessionTypeName,
                fin = Fin,
                fout = Fout,
                secin = Sin,
                secout = Sout,
                txtStatus = it?.approveStatus,
                txtcheckinimage = it?.checkInImgURL,
                txtcheckoutimage = it?.checkOutImgURL,
                txtadmincomment = it?.admin_Comments,
                txtcheckinlatitude = it?.checkInLatitude,
                txtcheckinlongitude = it?.checkInLongitude,
                txtcheckoutlatitude = it?.checkOutLatitude,
                txtcheckoutlongitude = it?.checkOutLongitude,
                txtcheckoutLocation = it?.checkoutLocation,
                txtcheckinLocation = it?.checkinLocation,
                txtisLeaveExist = it?.isLeaveExist,
                txtrerasonLeave = it?.reasonForLeave,
                txtLeaveStatus = it?.leaveStatus,
            )
        }?.toMutableList()
        attendList.value = recyclerMsglist
        adminAttendancelistModel.value = adminemplistModelValue
    }

    /*approve attendance*/
    fun callApproveAttendanceApi(request: AttedanceApproveRejectRequest) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            approveAttendanceLiveData.postValue(
                networkRepository.approveAttendance(request)
            )
            progressLiveData.postValue(false)
        }
    }

    /*reject attendance*/
    fun callRejectAttendanceApi(request: AttedanceApproveRejectRequest) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            rejectAttendanceLiveData.postValue(
                networkRepository.rejectAttendance(request)
            )
            progressLiveData.postValue(false)
        }
    }
}
