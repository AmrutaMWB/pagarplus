package com.pagarplus.app.modules.adminemplist.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.databinding.ActivityAdminemplistBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.modules.adminemplist.data.viewmodel.AdminemplistVM
import com.pagarplus.app.modules.createemployee.ui.CreateEmployeeActivity
import com.pagarplus.app.modules.editemployeedetails.ui.EditemployeedetailsActivity
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class AdminemplistActivity :
    BaseActivity<ActivityAdminemplistBinding>(R.layout.activity_adminemplist), BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: AdminemplistVM by viewModels<AdminemplistVM>()
  lateinit var detailsAdapter : DetailsAdapter
  var currentDate: String = ""
  var isBranch: Boolean = true
  var isDept: Boolean = true

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")

    currentDate = getCurrentDate()
    viewModel.adminemplistModel.value?.existDate = currentDate

    /*onstart activity run get emp list api & assign to recylerview*/
    viewModel.callFetchGetEmpListApi()

    detailsAdapter = DetailsAdapter(viewModel.detailsList.value?:mutableListOf())
    binding.recyclerDetails.adapter = detailsAdapter
    detailsAdapter.setOnItemClickListener(
    object : DetailsAdapter.OnItemClickListener {
      override fun onItemClick(view:View, position:Int, item : DetailsRowModel) {
        onClickRecyclerDetails(view, position, item)
      }
    }
    )
    viewModel.detailsList.observe(this) {
      detailsAdapter.updateData(it)
    }
    binding.adminemplistVM = viewModel
    this.hideKeyboard()
  }

  override fun setUpClicks(): Unit {
    binding.imgRefresh.setOnClickListener{
      viewModel.adminemplistModel.value?.existDate = currentDate
      viewModel.adminemplistModel.value?.filetrType = MyApp.getInstance().resources.getString(R.string.lbl_all)
      viewModel.callFetchGetEmpListApi()
    }

    binding.imgFilter.setOnClickListener{
      var PopUpmenu = PopupMenu(this, it)
      PopUpmenu.setOnMenuItemClickListener { item ->
        when (item.itemId) {
          R.id.item_all -> {
            viewModel.adminemplistModel.value?.filetrType = MyApp.getInstance().resources.getString(R.string.lbl_all)
            viewModel.callFetchGetEmpListApi()
            true
          }
          R.id.item_active -> {
            viewModel.adminemplistModel.value?.filetrType = MyApp.getInstance().resources.getString(R.string.item_active)
            viewModel.callFetchGetEmpListApi()
            true
          }
          R.id.item_inactive -> {
            viewModel.adminemplistModel.value?.filetrType = MyApp.getInstance().resources.getString(R.string.item_inactive)
            viewModel.callFetchGetEmpListApi()
            true
          }
          else -> false
        }

      }
      PopUpmenu.inflate(R.menu.detailsmenu)
      PopUpmenu.show()
    }

    binding.myhome.setOnClickListener{
      val destIntent = AdmindashboardActivity.getIntent(this, null)
      startActivity(destIntent)
      finish()
    }

    binding.searchViewEmplist.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    binding.fabCreate.setOnClickListener {
      val destIntent = CreateEmployeeActivity.getIntent(this, null)
      startActivity(destIntent)
    }
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

  /*filter emp list on search data*/
  private fun filter(text: String) {
    // creating a new array list to filter our data.
    var filteredlist: ArrayList<DetailsRowModel> = ArrayList()

    // running a for loop to compare elements.
    for (item in viewModel.detailsList.value!!) {
      // checking if the entered string matched with any item of our recycler view.
      if (item.txtName?.lowercase()?.contains(text.lowercase())!!) {
        // if the item is matched we are
        // adding it to our filtered list.
        filteredlist.add(item)
      }
    }
    if (filteredlist.isEmpty()) {
      filteredlist = detailsAdapter.list as ArrayList<DetailsRowModel>
    } else {
      // at last we are passing that filtered
      // list to our adapter class.
      detailsAdapter.filterList(filteredlist)
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

    viewModel.fetchGetEmpLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchMsg(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    viewModel.fetchDeleteEmpLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        this.alert("","${response?.message}") {
          neutralButton {
            startActivity(Companion.getIntent(this@AdminemplistActivity,null))
            finish()
          }
        }
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    viewModel.fetchActiveEmpLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        this.alert("","${response?.message}") {
          neutralButton {
            finish()
            startActivity(Companion.getIntent(this@AdminemplistActivity,null))
          }
        }
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }
  }

  private fun onSuccessFetchMsg(response: SuccessResponse<FetchGetEmpListResponse>): Unit {
    viewModel.bindFetchGetEmpListResponse(response.data)
    var empcount = viewModel.detailsList.value?.size
    if(isBranch)
      binding.txtAllBranch.append("($empcount)")
    if(isDept)
      binding.txtAllDepartment.append("($empcount)")
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

  fun onClickRecyclerDetails(
    view: View,
    position: Int,
    item: DetailsRowModel
  ): Unit {
    when(view.id) {
      R.id.frameEdit ->  {
        val destBundle = Bundle()
        if(detailsAdapter.list.size > 0) {
          destBundle.putString(IntentParameters.EmpID,
            detailsAdapter.list.get(position)!!.txtEmpID.toString())
        }else{
          destBundle.putString(IntentParameters.EmpID,
            viewModel.detailsList.value?.get(position)!!.txtEmpID.toString())
        }
        val destIntent = EditemployeedetailsActivity.getIntent(this, destBundle)
        startActivity(destIntent)
        finish()
      }
      R.id.frameShare ->  {
        myCustomSnackbar(position)
      }
      R.id.frameDeactivate ->  {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Confirmation")
        // set message of alert dialog
        dialogBuilder.setMessage(R.string.msg_delete_emp)
          // if the dialog is cancelable
          .setCancelable(false)
          // positive button text and action
          .setPositiveButton("Yes", DialogInterface.OnClickListener {
              dialog, id ->
            if(detailsAdapter.list.size > 0) {
              deactivateDialog(detailsAdapter.list.get(position).txtEmpID,detailsAdapter.list.get(position).txtName)
            }else{
              deactivateDialog(viewModel.detailsList.value?.get(position)!!.txtEmpID,viewModel.detailsList.value?.get(position)!!.txtName)
            }
          })
          // negative button text and action
          .setNegativeButton("No", DialogInterface.OnClickListener {
              dialog, id -> dialog.cancel()
          })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
      }
      R.id.frameActivate ->  {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Confirmation")
        // set message of alert dialog
        dialogBuilder.setMessage(R.string.msg_activate_emp)
          // if the dialog is cancelable
          .setCancelable(false)
          // positive button text and action
          .setPositiveButton("Yes", DialogInterface.OnClickListener {
              dialog, id ->
            if(detailsAdapter.list.size > 0) {
              viewModel.callActiveEmpApi(detailsAdapter.list.get(position).txtEmpID)
            }else{
              viewModel.callActiveEmpApi(viewModel.detailsList.value?.get(position)!!.txtEmpID)
            }
          })
          // negative button text and action
          .setNegativeButton("No", DialogInterface.OnClickListener {
              dialog, id -> dialog.cancel()
          })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
      }
    }
  }

  private fun deactivateDialog(empId: Int?, empName:String?){
    val dialogBuilder = AlertDialog.Builder(this@AdminemplistActivity)
    val inflater = this.layoutInflater

    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.add_balance_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatImageView>(R.id.iv_cross)
    val linlayAdvance = dialogView.findViewById<LinearLayout>(R.id.linearRowAdvance)
    val linlayLoan = dialogView.findViewById<LinearLayout>(R.id.linearLoan)
    val linlaybalance = dialogView.findViewById<LinearLayout>(R.id.linearBalance)
    val EdtTxtComment = dialogView.findViewById<EditText>(R.id.EdtTxtComment)
    val btn_save = dialogView.findViewById<AppCompatButton>(R.id.btnSave)
    val dialogTitle = dialogView.findViewById<TextView>(R.id.tv_label)
    linlayAdvance.isVisible = false
    linlayLoan.isVisible = false
    linlaybalance.isVisible = false

    val alertDialog = dialogBuilder.create()
    alertDialog.show();

    btn_save.setText("Deactivate")
    dialogTitle.setText("Name - "+empName)

    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }

    btn_save.setOnClickListener{
      viewModel.adminemplistModel.value?.comment = EdtTxtComment.getText().toString()
      if(EdtTxtComment.getText().toString().equals("")){
        //EdtTxtComment.setBackgroundResource(R.drawable.rectangle_error_border)
        Toast.makeText(this,"Please write your comment",Toast.LENGTH_SHORT).show()
      }else {
        viewModel.callDeleteEmpApi(empId)
        alertDialog.dismiss()
      }
    }
  }

  fun myCustomSnackbar(pos:Int) {
    val dialogBuilder = Dialog(this)
    val inflater = this.getLayoutInflater()

    //@SuppressLint("InflateParams")
    dialogBuilder.setContentView(R.layout.layout_image_picker_dialog)
    dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    //val dialogView = inflater.inflate(R.layout.layout_image_picker_dialog, null)
    //dialogBuilder.setView(dialogView)
    //dialogView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val linMessage = dialogBuilder.findViewById<LinearLayout>(R.id.messagelayout)
    val linwhatsapp = dialogBuilder.findViewById<LinearLayout>(R.id.whatsappLayout)
    val linimage = dialogBuilder.findViewById<LinearLayout>(R.id.linearImage)
    val linshare = dialogBuilder.findViewById<LinearLayout>(R.id.linearShare)
    val cardView = dialogBuilder.findViewById<CardView>(R.id.cardLayout)
    cardView.setBackgroundColor(Color.TRANSPARENT)
    linshare.setBackgroundColor(Color.TRANSPARENT)
    linMessage.setBackgroundColor(Color.TRANSPARENT)
    linimage.isVisible = false
    linshare.isVisible = true

    // val alertDialog = dialogBuilder.create()
    // alertDialog.setGravity(Gravity.BOTTOM)

    val window: Window = dialogBuilder.window!!
    val wpl: WindowManager.LayoutParams = window.getAttributes()
    wpl.gravity = Gravity.BOTTOM
    dialogBuilder.show();

    var phoneNumber = ""
    var password = ""

    if(detailsAdapter.list.size > 0) {
      phoneNumber = detailsAdapter.list.get(pos)?.txtUsername.toString()
      password = detailsAdapter.list.get(pos)?.txtPassword!!
    }else{
      phoneNumber = viewModel.detailsList.value?.get(pos)?.txtUsername.toString()
      password = viewModel.detailsList.value?.get(pos)?.txtPassword!!
    }

    var orgname = viewModel.detailsList.value?.get(pos)?.organizationname
    val message = "https://play.google.com/store/apps \n"+orgname+
            " - Invited you to download the app using above link and login to your account with following credentials. \nUserName: "+phoneNumber+" \nPassword: "+password

    linMessage.setOnClickListener{
      val uri = Uri.parse("smsto:$phoneNumber")
      val intent = Intent(Intent.ACTION_SENDTO, uri)
      intent.putExtra("sms_body", message)
      startActivity(intent)
      /*val smsManager = SmsManager.getDefault()
      val parts = smsManager.divideMessage(message)
      smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)*/
      dialogBuilder.dismiss()
    }

    linwhatsapp.setOnClickListener{
      /*val packageManager : PackageManager = packageManager
      val i = Intent(Intent.ACTION_VIEW)
      val url = "https://api.whatsapp.com/send?phone=+91" + phoneNumber + "&text="+ URLEncoder.encode(message,"UTF-8")
      i.setPackage("com.whatsapp")
      i.data = Uri.parse(url)
      if(i.resolveActivity(packageManager) != null){
        startActivity(i)
      }
      dialogBuilder.dismiss()*/
      try {
        packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
        val intent = Intent(
          Intent.ACTION_VIEW,
          Uri.parse("https://wa.me/+91$phoneNumber?text=$message")
        )
        intent.setPackage("com.whatsapp")
        startActivity(intent)
        dialogBuilder.dismiss()
      } catch (e: PackageManager.NameNotFoundException) {
        Toast.makeText(
          this,
          "Please install WhatsApp",
          Toast.LENGTH_SHORT
        ).show()
        e.printStackTrace()
        dialogBuilder.dismiss()
      }
    }
  }

  override fun onBackPressed() {
    finish()
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("Branch","$selectedItem")
    if(selectedItem.isBranch==true){
      binding.txtAllBranch.setText(selectedItem.txtName)
      viewModel.adminemplistModel.value?.txtBranchId = selectedItem.txtValue
      isBranch = true
      isDept = false
      viewModel.callFetchGetEmpListApi()
    }else{
      binding.txtAllDepartment.setText(selectedItem.txtName)
      viewModel.adminemplistModel.value?.txtDeptId = selectedItem.txtValue
      isBranch = false
      isDept = true
      viewModel.callFetchGetEmpListApi()
    }
  }

  companion object {
      const val TAG: String = "ADMINEMPLIST_ACTIVITY"

      fun getIntent(context: Context, bundle: Bundle?): Intent {
        val destIntent = Intent(context, AdminemplistActivity::class.java)
        destIntent.putExtra("bundle", bundle)
        return destIntent
      }
    }
  }
