package com.pagarplus.app.modules.adminattendancelist.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityAdminAttendancelistBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminattendancelist.data.model.AttendanceRowModel
import com.pagarplus.app.modules.adminattendancelist.data.viewmodel.AdminAttendancelistVM
import com.pagarplus.app.modules.attendance_details.ui.AttendanceDetailsActivity
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.network.models.AdminDashboard.AdminFetchEmpAttendanceListResponse
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import org.koin.android.ext.android.bind
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Int
import kotlin.String
import kotlin.Unit

class AdminAttendancelistActivity :
    BaseActivity<ActivityAdminAttendancelistBinding>(R.layout.activity_admin_attendancelist), BranchDeptlistDialog.OnCompleteListener {
  public val viewModel: AdminAttendancelistVM by viewModels<AdminAttendancelistVM>()
  var Seltype: String? = ""
  var selDate: String? = ""
  var branchID: Int? = 0
  var branch: String? = ""
  private val requestCall = 1
  var currentDate: String = ""

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")

    Seltype = intent.getStringExtra(IntentParameters.AllEmp)
    selDate = intent.getStringExtra(IntentParameters.selDate)
    branchID = intent.getIntExtra(IntentParameters.BranchID,0)
    branch = intent.getStringExtra(IntentParameters.Branch)

    currentDate = getCurrentDate()

    if(Seltype.isNullOrEmpty()){
      binding.txtDate.setText(currentDate)
      viewModel.adminAttendancelistModel.value?.txtDate = currentDate
      viewModel.callFetchGetEmpAttendanceListApi()
    }else{
      binding.txtDate.setText(selDate)
      binding.txtAllBranch.setText(branch)
      if(Seltype.equals("All")){
        binding.spinnerSelectype.setSelection(2)
      }else if(Seltype.equals("Present")){
        binding.spinnerSelectype.setSelection(1)
      }else{
        binding.spinnerSelectype.setSelection(0)
      }
      viewModel.adminAttendancelistModel.value?.txtDate = selDate
      viewModel.adminAttendancelistModel.value?.txtBranchId = branchID
      viewModel.adminAttendancelistModel.value?.txtListType = Seltype
      viewModel.callFetchGetEmpAttendanceListApi()
    }

    val attendanceAdapter = AttendanceAdapter(viewModel.attendList.value?:mutableListOf())
    binding.recyclerAttendance.adapter = attendanceAdapter
    attendanceAdapter.setOnItemClickListener(
    object : AttendanceAdapter.OnItemClickListener {
      override fun onItemClick(view:View, position:Int, item : AttendanceRowModel) {
        onClickRecyclerAttendance(view, position, item)
      }
    }
    )
    viewModel.attendList.observe(this) {
      attendanceAdapter.updateData(it)
    }

    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CALL_PHONE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.CALL_PHONE),
        requestCall
      )
    }
    binding.adminAttendancelistVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.imageCalendar.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtDate.setText(selected)
        viewModel.adminAttendancelistModel.value?.txtDate = selected
        viewModel.callFetchGetEmpAttendanceListApi()
      }
    }
    binding.imgrefresh.setOnClickListener {
      binding.txtDate.setText(currentDate)
      binding.txtAllBranch.setText("All Branch")
      binding.spinnerSelectype.setSelection(2)
      viewModel.adminAttendancelistModel.value?.txtBranchId = 0
      viewModel.adminAttendancelistModel.value?.txtDeptId = 0
      viewModel.adminAttendancelistModel.value?.txtListType = Seltype
      viewModel.callFetchGetEmpAttendanceListApi()
    }
    binding.btnBack.setOnClickListener {
      finish()
    }
    binding.txtAllBranch.setOnClickListener {
      callpopupBranchDept(true)
    }
    binding.txtAllDepartment.setOnClickListener {
      callpopupBranchDept(false)
    }
    binding.spinnerSelectype.onItemSelectedListener = object :
      AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>,
                                  view: View, position: Int, id: Long) {
        viewModel.adminAttendancelistModel.value?.txtListType = parent.getItemAtPosition(position).toString()
        viewModel.callFetchGetEmpAttendanceListApi()
      }

      override fun onNothingSelected(parent: AdapterView<*>) {
        // write code to perform some action
      }
    }
  }

  /*call branch/Dept popup*/
  fun callpopupBranchDept(isBranch:Boolean){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsBranchDept, isBranch)
    var itemlistDialog = BranchDeptlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
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

    viewModel.fetchGetEmpAttendanceLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchMsg(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    viewModel.approveAttendanceLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        if(it.data.status == true){
          viewModel.callFetchGetEmpAttendanceListApi()
        }
        AttendanceListDialog(this, 0).dismiss()
        Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
      } else if (it is ErrorResponse) {
        onErrorFetchMsg(it.data ?: Exception())
      }
    }
    viewModel.rejectAttendanceLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        if(it.data.status == true){
          viewModel.callFetchGetEmpAttendanceListApi()
        }
        AttendanceListDialog(this, 0).dismiss()
        Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
      } else if (it is ErrorResponse) {
        onErrorFetchMsg(it.data ?: Exception())
      }
    }
  }

  private fun onSuccessFetchMsg(response: SuccessResponse<AdminFetchEmpAttendanceListResponse>): Unit {
    viewModel.bindFetchAttendanceListResponse(response.data)
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

  fun onClickRecyclerAttendance(
    view: View,
    position: Int,
    item: AttendanceRowModel
  ): Unit {
    when(view.id) {
      R.id.linearRowname ->  {
        callDialog(position)
      }
      R.id.imgTelephone ->  {
        makePhoneCall(viewModel.attendList.value?.get(position)?.txtMobilenumber.toString())
      }
    }
  }

  fun callDialog(position: Int): Boolean {
    AttendanceListDialog(this, position).show()
    return true
  }

  private fun makePhoneCall(number: String) {
    if (number.trim { it <= ' ' }.isNotEmpty()) {
      val dial = "tel:$number"
      startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
    } else {
      Toast.makeText(this, "Phone Number is empty", Toast.LENGTH_SHORT).show()
    }
  }
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String?>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == requestCall) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
        Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
      }
    }
  }

  companion object {
    const val TAG: String = "ADMIN_ATTENDANCELIST_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, AdminAttendancelistActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("Branch","$selectedItem")
    if(selectedItem.isBranch==true){
      binding.txtAllBranch.setText(selectedItem.txtName)
      viewModel.adminAttendancelistModel.value?.txtBranchId = selectedItem.txtValue
      viewModel.callFetchGetEmpAttendanceListApi()
    }else{
      binding.txtAllDepartment.setText(selectedItem.txtName)
      viewModel.adminAttendancelistModel.value?.txtDeptId = selectedItem.txtValue
      viewModel.callFetchGetEmpAttendanceListApi()
    }
  }
}
