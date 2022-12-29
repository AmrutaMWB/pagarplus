package com.pagarplus.app.modules.notification.ui

import android.R.string
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityNotificationBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.modules.notification.data.model.MessageRowModel
import com.pagarplus.app.modules.notification.data.viewmodel.NotificationVM
import com.pagarplus.app.modules.notificationcreatemessage.ui.NotificationCreateMessageActivity
import com.pagarplus.app.modules.replymessage.ui.ReplyActivity
import com.pagarplus.app.modules.userdashboard.ui.UserdashboardActivity
import com.pagarplus.app.network.models.notificationMsg.FetchInOutMsgListResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


class NotificationActivity :
    BaseActivity<ActivityNotificationBinding>(R.layout.activity_notification), BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: NotificationVM by viewModels<NotificationVM>()
  private val REQUEST_CODE_HOME_ACTIVITY: Int = 597
  lateinit var messageAdapter: MessageAdapter
  var currentDate: String = ""

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.notificationVM = viewModel

    currentDate = getCurrentDate()
    viewModel.notificationModel.value?.selDate = currentDate

    viewModel.callFetchGetInboxMessagesApi()
    CallNotificationInBoxList()
    this@NotificationActivity.hideKeyboard()

    if(viewModel.userdetails?.isAdmin == true){
      binding.fabCreateMsg.isVisible = true
    }else{
      binding.fabCreateMsg.isVisible = false
    }

    if(viewModel.profiledetails?.showBranch == false){
      binding.txtAllBranch.isEnabled = false
      binding.txtAllBranch.setTextAppearance(R.style.disabledButton)
    }else{
      binding.txtAllBranch.isEnabled = true
    }

    if(viewModel.profiledetails?.showDepartment == false){
      binding.txtAllDepartment.isEnabled = false
      binding.txtAllDepartment.setTextAppearance(R.style.disabledButton)
    }else{
      binding.txtAllDepartment.isEnabled = true
    }
  }

  fun CallNotificationInBoxList(){
    messageAdapter = MessageAdapter(viewModel.messageList.value ?: mutableListOf())
    binding.recyclerMessageInbox.adapter = messageAdapter
    messageAdapter.setOnItemClickListener(
      object : MessageAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, item: MessageRowModel) {
          onClickRecyclerMessage(view, position, item)
        }
      }
    )
    viewModel.messageList.observe(this) {
      messageAdapter.updateData(it)
    }
  }

  @SuppressLint("ResourceAsColor")
  override fun setUpClicks(): Unit {
    binding.myhome.setOnClickListener{
      if(viewModel.userdetails?.isAdmin == true) {
        val destIntent = AdmindashboardActivity.getIntent(this, null)
        startActivity(destIntent)
        finish()
      }else{
        val destIntent = UserdashboardActivity.getIntent(this, null)
        startActivity(destIntent)
        finish()
      }
    }

    binding.imgRefresh.setOnClickListener{
      viewModel.notificationModel.value?.txtBranchid = 0
      viewModel.notificationModel.value?.txtDeptid = 0
      viewModel.notificationModel.value?.selDate = currentDate
      viewModel.callFetchGetInboxMessagesApi()
    }

    binding.fabCreateMsg.setOnClickListener{
      val destIntent = NotificationCreateMessageActivity.getIntent(this, null)
      startActivity(destIntent)
    }

    binding.linearDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtDate.setText(selected)
        viewModel.notificationModel.value?.selDate = selected
        viewModel.callFetchGetInboxMessagesApi()
      }
    }

    binding.searchViewEmplist.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(s: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(s: String?): Boolean {
        if (messageAdapter != null) {
          if (s != null) {
            filter(s)
          }
        }
        return false
      }
    })

    binding.txtAllBranch.setOnClickListener {
      callpopupBranchDept(true)
    }
    binding.txtAllDepartment.setOnClickListener {
      callpopupBranchDept(false)
    }
  }

  /*call branch/Dept popup*/
  fun callpopupBranchDept(isBranch:Boolean){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsBranchDept, isBranch)
    var itemlistDialog = BranchDeptlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
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

  /*filter emp list on search data*/
  private fun filter(text: String) {
    // creating a new array list to filter our data.
    var filteredlist: ArrayList<MessageRowModel> = ArrayList()

    // running a for loop to compare elements.
    for (item in viewModel.messageList.value!!) {
      // checking if the entered string matched with any item of our recycler view.
      if (item.txtEmpName?.lowercase()?.contains(text.lowercase())!!
        || item.txtDatetime?.lowercase()?.contains(text.lowercase())!!
        || item.txtMessage?.lowercase()?.contains(text.lowercase())!!) {
        // if the item is matched we are
        // adding it to our filtered list.
        filteredlist.add(item)
      }
    }
    if (filteredlist.isEmpty()) {
      filteredlist = messageAdapter.list as ArrayList<MessageRowModel>
      // if no item is added in filtered list we are
      // displaying a toast message as no data found.
      Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
    } else {
      // at last we are passing that filtered
      // list to our adapter class.
      messageAdapter.filterList(filteredlist)
    }
  }

  public override fun addObservers(): Unit {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this@NotificationActivity) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@NotificationActivity.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }

    viewModel.fetchGetInboxMessagesLiveData.observe(this@NotificationActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchMsg(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }
  }

  private fun onSuccessFetchMsg(response: SuccessResponse<FetchInOutMsgListResponse>): Unit {
    viewModel.bindFetchGetInboxMessagesResponse(response.data)
    if(viewModel.messageList.value?.size!! > 0) {
      binding.recyclerMessageInbox.isVisible = true
      binding.linearNoMsg.isVisible = false
    }else{
      binding.recyclerMessageInbox.isVisible = false
      binding.linearNoMsg.isVisible = true
    }
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

  fun onClickRecyclerMessage(
    view: View,
    position: Int,
    item: MessageRowModel
  ): Unit {
    when(view.id) {
      R.id.linearTextRow ->  {
        val destBundle = Bundle()
        if(messageAdapter.list.size > 0) {
          destBundle.putInt(IntentParameters.mainMessageId, messageAdapter.list.get(position)?.txtMainmsgID!!)
          destBundle.putInt(IntentParameters.fromEmpID, messageAdapter.list.get(position)?.txtFromempID!!)
          destBundle.putString(IntentParameters.Branch, messageAdapter.list.get(position).txtBranch)
          destBundle.putString(IntentParameters.Dept, messageAdapter.list.get(position).txtDept)
          destBundle.putString(IntentParameters.imagePath, messageAdapter.list.get(position).ProfileimgUrl)
          destBundle.putString(IntentParameters.EmpName, messageAdapter.list.get(position).txtEmpName)
        }else{
          destBundle.putInt(IntentParameters.mainMessageId, viewModel.messageList.value?.get(position)?.txtMainmsgID!!)
          destBundle.putInt(IntentParameters.fromEmpID, viewModel.messageList.value?.get(position)?.txtFromempID!!)
          destBundle.putString(IntentParameters.Branch, viewModel.messageList.value?.get(position)?.txtBranch)
          destBundle.putString(IntentParameters.Dept, viewModel.messageList.value?.get(position)?.txtDept)
          destBundle.putString(IntentParameters.imagePath, viewModel.messageList.value?.get(position)?.ProfileimgUrl)
          destBundle.putString(IntentParameters.EmpName, viewModel.messageList.value?.get(position)?.txtEmpName)
        }
        val destIntent = ReplyActivity.getIntent(this, destBundle)
        startActivityForResult(destIntent, REQUEST_CODE_HOME_ACTIVITY)
      }
      R.id.imageNoti ->  {
        if(messageAdapter.list.size > 0) {
          EnlargeImageDialog(messageAdapter.list.get(position).txtImgpath!!)
        }else{
          EnlargeImageDialog(viewModel.messageList.value?.get(position)?.txtImgpath!!)
        }
      }
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("Branch","$selectedItem")
    if(selectedItem.isBranch==true){
      binding.txtAllBranch.setText(selectedItem.txtName)
      viewModel.notificationModel.value?.txtBranchid = selectedItem.txtValue
      viewModel.callFetchGetInboxMessagesApi()
    }else{
      binding.txtAllDepartment.setText(selectedItem.txtName)
      viewModel.notificationModel.value?.txtDeptid = selectedItem.txtValue
      viewModel.callFetchGetInboxMessagesApi()
    }
  }

  companion object {
    const val TAG: String = "NOTIFICATION_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, NotificationActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
