package com.pagarplus.app.modules.replymessage.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.databinding.ActivityReplymsgBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.replymessage.data.model.MessageReplyModel
import com.pagarplus.app.modules.replymessage.data.viewmodel.ReplyMsgVM
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.network.models.createReplyMsg.CreateReplyMsgResponse
import com.pagarplus.app.network.models.fetchMsgHistory.FetchMsgHistoryResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import com.yuvapagar.app.modules.reports.ui.ReplyAdapter
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import kotlin.Int
import kotlin.String
import kotlin.Unit

class ReplyActivity :
    BaseActivity<ActivityReplymsgBinding>(R.layout.activity_replymsg) {
  private val viewModel: ReplyMsgVM by viewModels<ReplyMsgVM>()
  private lateinit var mImageBytes:ByteArray
  private var imageFile:File?=null
  var mPicUri_img: Uri?=null
  var img_pathfrom_api: String? = null
  lateinit var messageAdapter: ReplyAdapter

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    viewModel.replyModel.value?.messageIdValue = viewModel.navArguments?.getInt(IntentParameters.mainMessageId)
    viewModel.replyModel.value?.toEmployeeValue = viewModel.navArguments?.getInt(IntentParameters.fromEmpID)
    viewModel.replyModel.value?.empbranch = viewModel.navArguments?.getString(IntentParameters.Branch)
    viewModel.replyModel.value?.empdept = viewModel.navArguments?.getString(IntentParameters.Dept)
    viewModel.replyModel.value?.empimage = viewModel.navArguments?.getString(IntentParameters.imagePath)
    viewModel.replyModel.value?.empname = viewModel.navArguments?.getString(IntentParameters.EmpName)

    viewModel.callFetchReplyMsgHistoryApi()
    CallReplyMsgHistoryList()
    this.hideKeyboard()

    if(viewModel.replyModel.value?.empimage.isNullOrEmpty()){
      Glide.with(this).load(R.drawable.usericon).into(binding.imageUser)
    }else{
      Glide.with(this).load(viewModel.replyModel.value?.empimage).into(binding.imageUser)
    }
  }

  fun CallReplyMsgHistoryList(){
    messageAdapter = ReplyAdapter(viewModel.messageList.value?:mutableListOf())
    binding.recyclerMsglist.adapter = messageAdapter
    messageAdapter.setOnItemClickListener(
      object : ReplyAdapter.OnItemClickListener {
        override fun onItemClick(view:View, position:Int, item : MessageReplyModel) {
          onClickRecyclerMessage(view, position, item)
        }
      }
    )
    viewModel.messageList.observe(this) {
      messageAdapter.updateData(it)
    }
    binding.replyVM = viewModel
  }

  @SuppressLint("ResourceAsColor")
  override fun setUpClicks(): Unit {
    binding.edtReplyMessage.setOnClickListener{
      OpenReplyMsgDialog()
    }
  }

  /*onclick edittext open popup dialog to attach image and message*/
  fun OpenReplyMsgDialog(){

    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()
    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.reply_message_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatImageView>(R.id.close_replypopup)
    val img_camera = dialogView.findViewById<ImageView>(R.id.imageAttach)
    val btn_send = dialogView.findViewById<AppCompatButton>(R.id.btnSendReply)
    val ReplyMsg = dialogView.findViewById<EditText>(R.id.EdtTxtReplyMsg)

    val alertDialog = dialogBuilder.create()

    alertDialog.show();

    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }

    img_camera.setOnClickListener{
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          img_camera.setImageURI(path)
          mPicUri_img=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
      //txt_viewimg_front.isVisible = true
    }

    btn_send.setOnClickListener{
      viewModel.replyModel.value?.etEdtTxtMessageValue = ReplyMsg.getText().toString()
      if(viewModel.replyModel.value?.etEdtTxtMessageValue!!.isRequired() == true) {
        if (mPicUri_img != null) {
          viewModel.imageUpload(ImageFolders.NotificationMessage, mPicUri_img!!,mImageBytes)
        } else {
          viewModel.callReplyMessageApi()
        }
      }else{
        Toast.makeText(this,"Type your message",Toast.LENGTH_LONG).show()
      }
      alertDialog.dismiss()
    }
  }

  /*get image path from file*/
  fun setImagePath(picUri: Uri){
    val uriPathHelper = URIPathHelper()
    val filePath = uriPathHelper.getPath(this, picUri)
    if(filePath!=null)
      imageFile = File(filePath)
    mImageBytes = FileUploadHelper.compressedImageFile(picUri,this)
    Log.e("ImagePath", "path is $filePath")
  }

  /*view idproof image in fullview*/
  fun EnlargeImageDialog(imagepath: String) {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()

    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.imageview_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val imgview = dialogView.findViewById<ImageView>(R.id.dialog_imageview)
    val ivclose = dialogView.findViewById<AppCompatButton>(R.id.iv_close)
    val alertDialog = dialogBuilder.create()
    alertDialog.setCancelable(true)
    alertDialog.show()

    ivclose.setOnClickListener {
      alertDialog.dismiss()
    }
    Glide.with(this).load(imagepath).into(imgview)
  }

  fun onClickRecyclerMessage(
    view: View,
    position: Int,
    item: MessageReplyModel
  ): Unit {
    when(view.id) {
      R.id.imgMsgNotificationFrom ->  {
        if(messageAdapter.list.size > 0) {
          EnlargeImageDialog(messageAdapter.list.get(position).imgPath!!)
        }else{
          EnlargeImageDialog(viewModel.messageList.value?.get(position)?.imgPath!!)
        }
      }
      R.id.imgMsgNotificationUser ->  {
        if(messageAdapter.list.size > 0) {
          EnlargeImageDialog(messageAdapter.list.get(position).imgPath!!)
        }else{
          EnlargeImageDialog(viewModel.messageList.value?.get(position)?.imgPath!!)
        }
      }
    }
  }

  public override fun addObservers(): Unit {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }
    viewModel.fetchGetReplyMsgHistoryLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchMsg(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    viewModel.replyMessageLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateReplyMessage(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }
    /*get imagepath from fileUpload api on success*/
    /*upload front image and get path*/
    viewModel.imageUploadLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        img_pathfrom_api = response?.imagePath
        viewModel.replyModel.value?.imagePathValue = img_pathfrom_api
        viewModel.callReplyMessageApi()
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message,Toast.LENGTH_LONG).show();
      }
    }
  }

  private fun onSuccessFetchMsg(response: SuccessResponse<FetchMsgHistoryResponse>): Unit {
    viewModel.bindFetchReplyMsgHistoryResponse(response.data)
  }

  private
  fun onSuccessCreateReplyMessage(response: SuccessResponse<CreateReplyMsgResponse>) {
    Log.e("Sattus",response.toString());
    this.alert("","${response.`data`.message}") {
      neutralButton {
        if (response.data.status == true) {
          viewModel.callFetchReplyMsgHistoryApi()
        }else{
          it.dismiss()
        }
      }
    }
    viewModel.bindCreateReplyModelResponse(response.data)
  }

  private fun onErrorFetchMsg(exception: Exception): Unit {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
      is HttpException -> {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
        else JSONObject()
        val errMessage = if(!errorObject.optString("Errormsg").isNullOrEmpty()) {
          errorObject.optString("Errormsg").toString()
        } else {
          exception.response()?.message()?:""
        }
        Snackbar.make(binding.root, errMessage, Snackbar.LENGTH_LONG).show()
      }
    }
  }

  companion object {
    const val TAG: String = "NOTIFICATION_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, ReplyActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
