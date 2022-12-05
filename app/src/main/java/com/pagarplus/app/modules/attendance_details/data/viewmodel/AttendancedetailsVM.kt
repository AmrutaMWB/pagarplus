package com.pagarplus.app.modules.attendance_details.data.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.extensions.extractDateWith12
import com.pagarplus.app.modules.attendance_details.data.model.AttendanceDetailsModel
import com.pagarplus.app.network.models.AdminDashboard.AdminFetchEmpAttendanceListResponse
import com.pagarplus.app.network.models.AdminDashboard.AttendanceItem
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

class AttendancedetailsVM : ViewModel(), KoinComponent {
  val attendanceDetailsModel: MutableLiveData<AttendanceDetailsModel> =
      MutableLiveData(AttendanceDetailsModel())

  var navArguments: Bundle? = null

  private val networkRepository: NetworkRepository by inject()

  val attendList: MutableLiveData<MutableList<AttendanceItem>> =
      MutableLiveData(mutableListOf())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  private val prefs: PreferenceHelper by inject()
  val userdetails = prefs.getLoginDetails<LoginResponse>()
  val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()
  var chkintime = ""
  var chkouttime = ""

  val fetchGetEmpAttendanceLiveData: MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>> =
        MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>>()

    fun callFetchGetEmpAttendanceDetailsApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchGetEmpAttendanceLiveData.postValue(
                networkRepository.GetAttendanceDetails(
                    empID = attendanceDetailsModel.value?.txtEmpID,
                    fromDate = attendanceDetailsModel.value?.txtFromDate,
                    toDate = attendanceDetailsModel.value?.txtToDate
                )
            )
            progressLiveData.postValue(false)
        }
    }

   fun bindFetchAttendanceDetailsResponse(response: AdminFetchEmpAttendanceListResponse) {
        val adminempdetailsModelValue = attendanceDetailsModel.value ?: AttendanceDetailsModel()
       response.empattendanceList?.forEach {
           if(it?.Attendance?.isNotEmpty() == true){
             it.AttenDanceDetailList=it.Attendance.map {
                AttendanceDetailsModel(
                    txtVisit = it.visitTypeName,
                    txtDuration = it.sessionTypeName,
                    txtCheckinDate = it.checkInDate?.extractDateWith12(),
                    txtCheckoutDate = it.checkOutDate?.extractDateWith12(),
                    txtStatus = it.approveStatus,
                    txtEmpName = it.employeeName,
                    txtcheckinimage = it.checkInImgURL,
                    txtcheckoutimage = it.checkOutImgURL,
                    txtbranch = it.branch,
                    txtDept = it.department,
                    txtAttendanceID = it.atendanceid,
                    txtadmincomment = it.admin_Comments,
                    txtcheckinlatitude = it.checkInLatitude,
                    txtcheckinlongitude = it.checkInLongitude,
                    txtcheckoutlatitude = it.checkOutLatitude,
                    txtcheckoutlongitude = it.checkOutLongitude,
                    txtcheckoutLocation = it.checkoutLocation,
                    txtcheckinLocation = it.checkinLocation,
                    organizationname = profiledetails?.organization
                )
             }.toMutableList()
       }
           }
        attendList.value = response.empattendanceList!!.toMutableList()
        attendanceDetailsModel.value = adminempdetailsModelValue
    }
}
