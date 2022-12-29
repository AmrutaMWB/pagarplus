package com.pagarplus.app.modules.notification.`data`.viewmodel

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractDateWithT
import com.pagarplus.app.modules.notification.data.model.MessageRowModel
import com.pagarplus.app.modules.notification.data.model.NotificationModel
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.notificationMsg.FetchInOutMsgListResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

class NotificationVM : ViewModel(), KoinComponent {
  val notificationModel: MutableLiveData<NotificationModel> = MutableLiveData(NotificationModel())

  var navArguments: Bundle? = null

  val messageList: MutableLiveData<MutableList<MessageRowModel>> = MutableLiveData(mutableListOf())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  var msgtime = ""

  val fetchGetInboxMessagesLiveData: MutableLiveData<Response<FetchInOutMsgListResponse>> =
    MutableLiveData<Response<FetchInOutMsgListResponse>>()

  val userdetails = prefs.getLoginDetails<LoginResponse>()
  val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  fun callFetchGetInboxMessagesApi() {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      fetchGetInboxMessagesLiveData.postValue(
        networkRepository.fetchNotiMsgList(
          userID = userdetails?.userID,
          seldate = notificationModel.value?.selDate,
          branchID = notificationModel.value?.txtBranchid,
          deptID = notificationModel.value?.txtDeptid
        )
      )
      progressLiveData.postValue(false)
    }
  }

  fun bindFetchGetInboxMessagesResponse(response: FetchInOutMsgListResponse) {

    val pattern = "yyyy-MM-dd'T'HH:mm"
    val serverDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    val notificationModelValue = notificationModel.value ?: NotificationModel()
    val recyclerMsglist = response.msgList?.map {

      val defaultDate = serverDateFormat.parse(it!!.recieveDate)
      if ( defaultDate != null ) {
        msgtime = getMyPrettyDate(defaultDate)
      }
      MessageRowModel(
        txtEmpName = it?.fromEmp,
        txtMessage = it?.message.toString(),
        txtDatetime = msgtime,
        txtImgpath = it?.imagePath,
        txtMainmsgID = it?.mainMsgId,
        txtFromempID = it?.fromEmpId,
        txtBranch = it?.branch,
        txtDept = it?.department,
        ProfileimgUrl = it?.userProfileImage,
        organizationName = profiledetails?.organization
      )
    }?.toMutableList()
    messageList.value = recyclerMsglist
    notificationModel.value = notificationModelValue
  }

  /*convert msg time format*/
  fun getMyPrettyDate(neededTimeMilis: Date): String {
    val nowTime = Calendar.getInstance()
    val neededTime = Calendar.getInstance()
    neededTime.setTime(neededTimeMilis)
    Log.e("nowtime",nowTime.toString())

    return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
      if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
        if (nowTime[Calendar.DATE] == neededTime[Calendar.DATE]) {
          //here return like "Today at 12:00"
          "Today "+DateFormat.format("hh:mm a", neededTime).toString()
        } else if (nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1) {
          //here return like "Yesterday at 12:00"
          "Yesterday "+DateFormat.format("hh:mm a", neededTime).toString()
        } else {
          //here return like "May 31, 12:00"
          DateFormat.format("dd-MM-yyyy hh:mm a", neededTime).toString()
        }
      } else {
        //here return like "May 31, 12:00"
        DateFormat.format("dd-MM-yyyy hh:mm a", neededTime).toString()
      }
    } else {
      //here return like "May 31 2010, 12:00" - it's a different year we need to show it
      DateFormat.format("dd-MM-yyyy hh:mm a", neededTime).toString()
    }
  }
}
