package com.pagarplus.app.modules.adminattendancelist.`data`.viewmodel

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminattendancelist.data.model.AdminAttendancelistModel
import com.pagarplus.app.modules.adminattendancelist.data.model.AttendanceRowModel
import com.pagarplus.app.modules.adminattendancelist.data.model.VisitRowModel
import com.pagarplus.app.modules.adminemplist.data.model.AdminemplistModel
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.network.models.AdminDashboard.AdminFetchEmpAttendanceListResponse
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponse
import com.pagarplus.app.network.models.attendance.AttedanceApproveRejectRequest
import com.pagarplus.app.network.models.attendance.EmpIdItem
import com.pagarplus.app.network.models.createMessage.EmpListItem
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import kotlin.collections.MutableList
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.pagarplus.app.network.repository.NetworkRepository
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
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

    val visitList: MutableLiveData<MutableList<VisitRowModel>> =
        MutableLiveData(mutableListOf())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  private val prefs: PreferenceHelper by inject()
  val userdetails = prefs.getLoginDetails<LoginResponse>()
  val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  val fetchGetEmpAttendanceLiveData: MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>> =
        MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>>()

    val approveAttendanceLiveData: MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>> =
        MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>>()

    val AprRejAllAttendanceLiveData: MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>> =
        MutableLiveData<Response<AdminFetchEmpAttendanceListResponse>>()

    val ImagesList: MutableList<String> = ArrayList()

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
            var FinDur=""
            var FoutDur=""
            var SinDur=""
            var SoutDur=""
            var Finimage=""
            var Foutimage=""
            var Sinimage=""
            var Soutimage=""
            var duration = ""

            if(!logdata.isNullOrEmpty()){
                if(logdata.size > 1) {
                    Finimage = logdata[0]?.checkInImgURL.toString()
                    Foutimage = logdata[0]?.checkOutImgURL.toString()
                    Sinimage = logdata[1]?.checkInImgURL.toString()
                    Soutimage = logdata[1]?.checkOutImgURL.toString()

                    ImagesList.add(0,Finimage)
                    ImagesList.add(1,Foutimage)
                    ImagesList.add(2,Sinimage)
                    ImagesList.add(3,Soutimage)

                    if (logdata[0]?.sessionTypeName == "First Half") {
                        Fin = logdata[0]?.checkInDate?.extractTime().toString()
                        Fout = logdata[0]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        FinDur = logdata[0]?.checkInDate?.extractTimeFormat().toString()
                        FoutDur = logdata[0]?.checkOutDate?.extractTimeFormat().toString()

                    } else if (logdata[0]?.sessionTypeName == "Second Half") {
                        Sin = logdata[0]?.checkInDate?.extractTime().toString()
                        Sout = logdata[0]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        SinDur = logdata[0]?.checkInDate?.extractTimeFormat().toString()
                        SoutDur = logdata[0]?.checkOutDate?.extractTimeFormat().toString()

                    } else {
                        Fin = logdata[0]?.checkInDate?.extractTime().toString()

                        /*for duration calculation*/
                        FinDur = logdata[0]?.checkInDate?.extractTimeFormat().toString()
                    }

                    if (logdata[1]?.sessionTypeName == "First Half") {
                        Fin = logdata[1]?.checkInDate?.extractTime().toString()
                        Fout = logdata[1]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        FinDur = logdata[1]?.checkInDate?.extractTimeFormat().toString()
                        FoutDur = logdata[1]?.checkOutDate?.extractTimeFormat().toString()

                    } else if (logdata[1]?.sessionTypeName == "Second Half") {
                        Sin = logdata[1]?.checkInDate?.extractTime().toString()
                        Sout = logdata[1]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        SinDur = logdata[1]?.checkInDate?.extractTimeFormat().toString()
                        SoutDur = logdata[1]?.checkOutDate?.extractTimeFormat().toString()

                    } else {
                        Sout = logdata[1]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        SoutDur = logdata[1]?.checkOutDate?.extractTimeFormat().toString()
                    }

                    if(FoutDur.isEmpty() && SinDur.isEmpty()){
                        duration = diffTime(FinDur, SoutDur)
                    }else {
                        if(!FoutDur.isEmpty() && !SoutDur.isEmpty()) {
                            duration = addTime(diffTime(FinDur, FoutDur), diffTime(SinDur, SoutDur))
                        }else if(!FoutDur.isEmpty()){
                            duration = diffTime(FinDur, FoutDur)
                        }
                    }

                }else{
                    Finimage = logdata[0]?.checkInImgURL.toString()
                    Foutimage = logdata[0]?.checkOutImgURL.toString()

                    ImagesList.add(0,Finimage)
                    ImagesList.add(1,Foutimage)

                    if (logdata[0]?.sessionTypeName == "First Half") {
                        Fin = logdata[0]?.checkInDate?.extractTime().toString()
                        Fout = logdata[0]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        FinDur = logdata[0]?.checkInDate?.extractTimeFormat().toString()
                        FoutDur = logdata[0]?.checkOutDate?.extractTimeFormat().toString()

                    } else if (logdata[0]?.sessionTypeName == "Second Half") {
                        Sin = logdata[0]?.checkInDate?.extractTime().toString()
                        Sout = logdata[0]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        SinDur = logdata[0]?.checkInDate?.extractTimeFormat().toString()
                        SoutDur = logdata[0]?.checkOutDate?.extractTimeFormat().toString()

                    } else {
                        Fin = logdata[0]?.checkInDate?.extractTime().toString()
                        Sout = logdata[0]?.checkOutDate?.extractTime().toString()

                        /*for duration calculation*/
                        FinDur = logdata[0]?.checkInDate?.extractTimeFormat().toString()
                        SoutDur = logdata[0]?.checkOutDate?.extractTimeFormat().toString()
                    }

                    if(FoutDur.isEmpty() && SinDur.isEmpty()){
                        duration = diffTime(FinDur, SoutDur)
                    }else {
                        if(!FoutDur.isEmpty() && !SoutDur.isEmpty()) {
                            duration = addTime(diffTime(FinDur, FoutDur), diffTime(SinDur, SoutDur))
                        }else if(!FoutDur.isEmpty()){
                            duration = diffTime(FinDur, FoutDur)
                        }
                    }
                }
                Log.e("Imagelist",ImagesList.toString())
            }
            /*first half & second half bind in recylerview*/
            val visitRecylerList = it?.loginData?.map {
                VisitRowModel(
                    txtAttendanceID = it?.atendanceid,
                    txtVisit = it?.visitTypeName,
                    txtDuration = it?.sessionTypeName,
                    txtStatus = it?.approveStatus,
                    txtCheckinDate = it?.checkInDate?.extractDateWith12(),
                    txtCheckoutDate = it?.checkOutDate?.extractDateWith12(),
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
            visitList.value = visitRecylerList

            AttendanceRowModel(
                txtbranch = it?.branch,
                txtDept = it?.department,
                txtempid = it?.employeeID,
                txtEmpName = it?.employeeName,
                txtMobilenumber = it?.contactNumber,
                organizationname = profiledetails?.organization,
                fin = Fin,
                fout = Fout,
                secin = Sin,
                secout = Sout,
                txttotDuration = duration,
                visitList = visitList,
                txtChecked = adminAttendancelistModel.value?.EmpIdlist?.contains(EmpIdItem(it?.employeeID)),
                ImagesList = ImagesList
            )
        }?.toMutableList()
        attendList.value = recyclerMsglist
        adminAttendancelistModel.value = adminemplistModelValue
    }

    /*approve attendance*/
    fun callApproveAttendanceApi(attendanceID: Int,status: String,comment: String) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            approveAttendanceLiveData.postValue(
                networkRepository.approveAttendance(attendanceID,status,comment)
            )
            progressLiveData.postValue(false)
        }
    }

    /*approve all attendance list*/
    fun callAllAttendanceAprRejApi(request: AttedanceApproveRejectRequest) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            AprRejAllAttendanceLiveData.postValue(
                networkRepository.AprRejAllAttendance(request)
            )
            progressLiveData.postValue(false)
        }
    }

    fun removeColon(s: String?): Int {
        var strtime = s?.replace(":", "", false)
        return strtime!!.toInt()
    }

    fun addTime(s1: String?, s2: String?): String {
        val time1: Int = removeColon(s1)
        val time2: Int = removeColon(s2)

        // add hours
        var hourDiff = time2 / 100 + time1 / 100

        // add minutes
        var minDiff = time2 % 100 + time1 % 100

        /*check if min is greter than 60 then increment hour and minus minutes*/
        if (minDiff >= 60) {
            hourDiff++
            minDiff -= 60
        }
        // convert answer again in string with ':'
        Log.e("addtime",hourDiff.toString() + ':' + minDiff.toString())
        return hourDiff.toString() + ':' + minDiff.toString()
    }

    fun diffTime(s1: String?, s2: String?): String {
        val simpleDateFormat = SimpleDateFormat("hh:mm:ss")

        var date1 = simpleDateFormat.parse(s1)
        var date2 = simpleDateFormat.parse(s2)

        val difference: Long = date2.getTime() - date1.getTime()
        var days = (difference / (1000 * 60 * 60 * 24)).toInt()
        var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        var min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
        var mins = ""
        if(min.toString().length == 1){
            mins = min.toString().padStart(2,'0')
        }else{
            mins = min.toString()
        }
        hours = if (hours < 0) -hours else hours
        Log.i("Hours=", "$hours:$mins")
        return "$hours:$mins"
    }
}
