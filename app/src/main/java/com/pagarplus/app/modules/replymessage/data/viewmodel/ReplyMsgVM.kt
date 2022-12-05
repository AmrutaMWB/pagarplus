package com.pagarplus.app.modules.replymessage.`data`.viewmodel

import android.net.ParseException
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.createbranch.data.model.CreateBranchModel
import com.pagarplus.app.modules.replymessage.data.model.MessageReplyModel
import com.pagarplus.app.modules.replymessage.data.model.ReplyModel
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createReplyMsg.CreateReplyMsgRequest
import com.pagarplus.app.network.models.createReplyMsg.CreateReplyMsgResponse
import com.pagarplus.app.network.models.createcreateBranch.CreateCreateBranchRequest
import com.pagarplus.app.network.models.createcreateBranch.CreateCreateBranchResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeResponse
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.fetchMsgHistory.FetchMsgHistoryResponse
import com.pagarplus.app.network.models.notificationMsg.FetchInOutMsgListResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReplyMsgVM : ViewModel(), KoinComponent {
  val replyModel: MutableLiveData<ReplyModel> = MutableLiveData(ReplyModel())

  var navArguments: Bundle? = null

  val messageList: MutableLiveData<MutableList<MessageReplyModel>> = MutableLiveData(mutableListOf())

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  var msgtime = ""

  var commondate = ""

  val fetchGetReplyMsgHistoryLiveData: MutableLiveData<Response<FetchMsgHistoryResponse>> =
    MutableLiveData<Response<FetchMsgHistoryResponse>>()

  val replyMessageLiveData: MutableLiveData<Response<CreateReplyMsgResponse>> =
    MutableLiveData<Response<CreateReplyMsgResponse>>()

  val imageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

  val userdetails = prefs.getLoginDetails<LoginResponse>()
  val profdetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  /*fetch all the message history api*/
  fun callFetchReplyMsgHistoryApi() {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      fetchGetReplyMsgHistoryLiveData.postValue(
        networkRepository.fetchReplyMsgHistoryList(
          mainMessageID = replyModel.value?.messageIdValue
        )
      )
      progressLiveData.postValue(false)
    }
  }

  /*get image upload path from api*/
  fun imageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      imageUploadLiveData.postValue(
        networkRepository.uploadFile(folder,uri,imgsize)
      )
      progressLiveData.postValue(false)
    }
  }

  /*send reply message body to api*/
  fun callReplyMessageApi() {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      replyMessageLiveData.postValue(
        networkRepository.createReplyMessage(
          contentType = """application/json""",
          createReplyMessageRequest = CreateReplyMsgRequest(
            messageID = replyModel.value?.messageIdValue,
            fromEmployee = userdetails?.userID,
            toEmployee = replyModel.value?.toEmployeeValue,
            message = replyModel.value?.etEdtTxtMessageValue,
            imagePath = replyModel.value?.imagePathValue)
        )
      )
      progressLiveData.postValue(false)
    }
  }

  fun bindCreateReplyModelResponse(response: CreateReplyMsgResponse) {
    val createReplyModelValue = replyModel.value ?: ReplyModel()
    replyModel.value = createReplyModelValue
  }

  fun bindFetchReplyMsgHistoryResponse(response: FetchMsgHistoryResponse) {
    val pattern = "yyyy-MM-dd'T'HH:mm"
    val serverDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    val replyModelValue = replyModel.value ?: ReplyModel()
    val recyclerMsglist = response.msgList?.map {
      val defaultDate = serverDateFormat.parse(it!!.receivedDate)
      if ( defaultDate != null ) {
        msgtime = getMyPrettyDate(defaultDate)
      }
      MessageReplyModel(
        txtEmpName = it?.fromEmployee,
        txtMessage = it?.message,
        txtDatetime = msgtime,
        imgPath = it?.imagePath.toString(),
        adminname = profdetails?.firstName+" "+profdetails?.lastName
      )
    }?.toMutableList()
    messageList.value = recyclerMsglist
    replyModel.value = replyModelValue
  }

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
          DateFormat.format("dd/MM/yyyy, hh:mm a", neededTime).toString()
        }
      } else {
        //here return like "May 31, 12:00"
        DateFormat.format("dd/MM/yyyy, hh:mm a", neededTime).toString()
      }
    } else {
      //here return like "May 31 2010, 12:00" - it's a different year we need to show it
      DateFormat.format("dd/MM/yyyy, hh:mm a", neededTime).toString()
    }
  }
}
//here return like "May 31, 12:00"
//DateFormat.format("MMMM d, yyyy", neededTime).toString()
//here return like "May 31 2010, 12:00" - it's a different year we need to show it
//DateFormat.format("MMMM dd, yyyy", neededTime).toString()