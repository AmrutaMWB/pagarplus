package com.pagarplus.app.modules.notificationcreatemessage.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.databinding.ActivityNotificationCreateMessageBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.modules.notification.ui.NotificationActivity
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel
import com.pagarplus.app.modules.notificationcreatemessage.data.viewmodel.NotificationCreateMessageVM
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponse
import com.pagarplus.app.network.models.createMessage.CreateMsgResponse
import com.pagarplus.app.network.models.createMessage.EmpListItem
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import com.yuvapagar.app.modules.notificationcreatemessage.ui.DetailsAdapter2
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File

class NotificationCreateMessageActivity :
    BaseActivity<ActivityNotificationCreateMessageBinding>(R.layout.activity_notification_create_message), BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: NotificationCreateMessageVM by viewModels<NotificationCreateMessageVM>()
  private var imageFile: File? = null
  private lateinit var mImageBytes:ByteArray
  var mPicUri_img: Uri?=null
  var img_pathfrom_api: String? = null
  lateinit var detailsAdapter : DetailsAdapter2

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.notificationCreateMessageVM = viewModel

    if(viewModel.profiledetails?.showBranch == false){
      binding.SpnBranch.isEnabled = false
      binding.SpnBranch.setTextAppearance(R.style.disabledButton)
    }else{
      binding.SpnBranch.isEnabled = true
    }

    if(viewModel.profiledetails?.showDepartment == false){
      binding.SpnDepartment.isEnabled = false
      binding.SpnDepartment.setTextAppearance(R.style.disabledButton)
    }else{
      binding.SpnDepartment.isEnabled = true
    }
    viewModel.callFetchGetEmpListApi()
  }

  override fun setUpClicks(): Unit {
    binding.imageCamera.setOnClickListener {
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imageCamera.setImageURI(path)
          mPicUri_img=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }

    binding.btnBack.setOnClickListener {
      finish()
    }

    binding.myhome.setOnClickListener {
      val destIntent = AdmindashboardActivity.getIntent(this, null)
      startActivity(destIntent)
      finish()
    }

    binding.btnSend.setOnClickListener {
      if(viewModel.notificationCreateMessageModel.value?.txtDescription.isNullOrEmpty()){
        Snackbar.make(binding.root, "Please write your message", Snackbar.LENGTH_LONG).show()
      }else if(mPicUri_img != null){
        viewModel.ImageUpload(ImageFolders.NotificationMessage, mPicUri_img!!,mImageBytes)
      }else{
        if(viewModel.notificationCreateMessageModel.value?.EmpIdlist?.size!!.equals(0)){
          Toast.makeText(this,"Please select employees",Toast.LENGTH_LONG).show()
        }else{
            viewModel.callCreateMessageApi()
        }
      }
    }

    binding.SpnBranch.setOnClickListener {
      callpopup(true)
    }

    binding.SpnDepartment.setOnClickListener {
      callpopup(false)
    }

    binding.SpnEmployee.setOnClickListener {
      showEmployeeDialog()
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

  fun callpopup(isBranch:Boolean){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsBranchDept, isBranch)

    var itemlistDialog = BranchDeptlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
  }

  /*ID Proof Upload dialog*/
  fun showEmployeeDialog() {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()
    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.sel_emp_search_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatButton>(R.id.iv_cross)
    val emp_recyclerlist = dialogView.findViewById<RecyclerView>(R.id.emplistRecycler)
    val txtok = dialogView.findViewById<TextView>(R.id.txtok)
    val searchbar = dialogView.findViewById<SearchView>(R.id.searchEMp)
    val chkSelectAll = dialogView.findViewById<CheckBox>(R.id.chkSelectAll)

    val alertDialog = dialogBuilder.create()

    alertDialog.show();

    if(viewModel.detailsList.value?.size!! > 1){
      chkSelectAll.isVisible = true
    }else{
      chkSelectAll.isVisible = false
    }

    detailsAdapter = DetailsAdapter2(viewModel.detailsList.value?:mutableListOf())
    Log.e("adapter", (viewModel.detailsList.value?: mutableListOf()).toString())
    emp_recyclerlist.adapter = detailsAdapter
    detailsAdapter.setOnItemClickListener(
      object : DetailsAdapter2.OnItemClickListener {
        override fun onItemClick(view:View, position:Int, item : Details2RowModel) {
          onClickRecyclerMessage(view, position, item)
        }
      }
    )
    viewModel.detailsList.observe(this@NotificationCreateMessageActivity) {
      detailsAdapter.updateData(it)
    }
    binding.notificationCreateMessageVM = viewModel

    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }

    searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(s: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(s: String?): Boolean {
        if (detailsAdapter != null) {
          if (s != null) {
            filter(s)
          }
        }
        return false
      }
    })

    txtok.setOnClickListener {
      alertDialog.dismiss()
      binding.SpnEmployee.setText("Employee Selected")
    }

    chkSelectAll.setOnClickListener {
      var arr = viewModel.detailsList.value
      var i = 0
      if(chkSelectAll.isChecked()){
        while (i < arr!!.size) {
          var name = viewModel.detailsList.value?.get(i)?.txtName
          var empid = viewModel.detailsList.value?.get(i)?.txtEmpID
          viewModel.detailsList.value?.set(i,Details2RowModel(name,empid,true))
          i++
        }
      } else {
        while (i < arr!!.size) {
          var name = viewModel.detailsList.value?.get(i)?.txtName
          var empid = viewModel.detailsList.value?.get(i)?.txtEmpID
          viewModel.detailsList.value?.set(i,Details2RowModel(name,empid,false))
          i++
        }
      }
      detailsAdapter.notifyDataSetChanged()
    }
  }

  private fun filter(text: String) {
    // creating a new array list to filter our data.
    val filteredlist: ArrayList<Details2RowModel> = ArrayList()

    // running a for loop to compare elements.
    for (item in viewModel.detailsList.value!!) {
      // checking if the entered string matched with any item of our recycler view.
      if (item.txtName?.toLowerCase()?.contains(text.toLowerCase())!!) {
        // if the item is matched we are
        // adding it to our filtered list.
        filteredlist.add(item)
      }
    }
    if (filteredlist.isEmpty()) {
      // if no item is added in filtered list we are
      // displaying a toast message as no data found.
      Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
    } else {
      // at last we are passing that filtered
      // list to our adapter class.
      detailsAdapter.filterList(filteredlist)
    }
  }

  override fun addObservers() {
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

    /*bind emp list based on department*/
    viewModel.fetchGetEmpLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchEmplist(it)
      } else if(it is ErrorResponse) {
        onErrorMessage(it.data ?:Exception())
      }
    }

    /*bind create message on success data*/
    viewModel.createMessageLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateMessage(it)
      } else if(it is ErrorResponse)  {
        onErrorMessage(it.data ?:Exception())
      }
    }

    /*get imagepath from fileUpload api on success*/
    /*upload image and get path*/
    viewModel.imageUploadLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        img_pathfrom_api = response?.imagePath
        viewModel.notificationCreateMessageModel.value?.txtImagePath = img_pathfrom_api
        if(viewModel.notificationCreateMessageModel.value?.EmpIdlist?.size!!.equals(0)){
          Toast.makeText(this,"Please select employees",Toast.LENGTH_LONG).show()
        }else{
          viewModel.callCreateMessageApi()
        }
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message, Toast.LENGTH_LONG).show();
      }
    }
  }

  private fun onSuccessFetchEmplist(response: SuccessResponse<GetEmpviaDeptListResponse>): Unit {
    viewModel.bindFetchGetEmpListResponse(response.data)
  }

  private fun onSuccessCreateMessage(response: SuccessResponse<CreateMsgResponse>) {
    Log.e("Sattus",response.toString())
    this.alert(MyApp.getInstance().getString(R.string.lbl_register_status),"${response.`data`.message}") {
      neutralButton {
        if (response.data.status == true) {
          finish()
          startActivity(NotificationActivity.getIntent(this@NotificationCreateMessageActivity,null))
        }else{
          it.dismiss()
        }
      }
    }
    viewModel.bindCreateMessageResponse(response.data)
  }

  private fun onErrorMessage(exception: Exception) {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
      is HttpException -> {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
        else JSONObject()
        val errMessage = if(!errorObject.optString("Message").isNullOrEmpty()) {
          errorObject.optString("Message").toString()
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
    item: Details2RowModel
  ): Unit {
    when(view.id) {
      R.id.chkEmplist ->  {
        var selitem: Int? = 0
        if(detailsAdapter.list.size > 0) {
          selitem = detailsAdapter.list.get(position).txtEmpID
        }else{
          selitem = viewModel.detailsList.value?.get(position)!!.txtEmpID
        }
        if(viewModel.notificationCreateMessageModel.value?.EmpIdlist?.contains(EmpListItem(selitem)) == true) {
          viewModel.notificationCreateMessageModel.value?.EmpIdlist?.remove(EmpListItem(selitem!!))
        }else{
          viewModel.notificationCreateMessageModel.value?.EmpIdlist?.add(EmpListItem(selitem!!))
        }
      }
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("State","$selectedItem")
    if(selectedItem.isBranch==true){
      binding.SpnBranch.setText(selectedItem.txtName)
      viewModel.notificationCreateMessageModel.value?.txtBranch = selectedItem.txtName
      viewModel.notificationCreateMessageModel.value?.txtBranchID = selectedItem.txtValue
    }else{
      viewModel.notificationCreateMessageModel.value?.txtDepartment = selectedItem.txtName
      viewModel.notificationCreateMessageModel.value?.txtDeptID = selectedItem.txtValue
      viewModel.callFetchGetEmpListApi()
    }
  }

  companion object {
    const val TAG: String = "NOTIFICATION_CREATE_MESSAGE_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, NotificationCreateMessageActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun onBackPressed() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(R.string.msg_closeapp)
    builder.setPositiveButton("Yes") { dialogInterface, which ->
      finish()
    }
    //performing negative action
    builder.setNegativeButton("No") { dialogInterface, which ->
      dialogInterface.dismiss()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()
  }
}
