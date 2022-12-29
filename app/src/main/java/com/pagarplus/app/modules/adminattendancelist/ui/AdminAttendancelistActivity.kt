package com.pagarplus.app.modules.adminattendancelist.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityAdminAttendancelistBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminattendancelist.data.model.AttendanceRowModel
import com.pagarplus.app.modules.adminattendancelist.data.viewmodel.AdminAttendancelistVM
import com.pagarplus.app.modules.expensereport.ui.ExpenseDialogActivity
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.network.models.AdminDashboard.AdminFetchEmpAttendanceListResponse
import com.pagarplus.app.network.models.attendance.AttedanceApproveRejectRequest
import com.pagarplus.app.network.models.attendance.EmpIdItem
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
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
  lateinit var attendanceAdapter: AttendanceAdapter

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
      viewModel.adminAttendancelistModel.value?.txtDate = selDate
      viewModel.adminAttendancelistModel.value?.txtBranchId = branchID
      viewModel.adminAttendancelistModel.value?.txtListType = Seltype
      viewModel.callFetchGetEmpAttendanceListApi()
    }

      attendanceAdapter = AttendanceAdapter(viewModel.attendList.value ?: mutableListOf())
      binding.recyclerAttendance.adapter = attendanceAdapter
      attendanceAdapter.setOnItemClickListener(
        object : AttendanceAdapter.OnItemClickListener {
          override fun onItemClick(view: View, position: Int, item: AttendanceRowModel) {
            onClickRecyclerAttendance(view, position, item)
          }
        }
      )
      viewModel.attendList.observe(this) {
        attendanceAdapter.updateData(it)
      }

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.CALL_PHONE),
        requestCall
      )
    }
    binding.adminAttendancelistVM = viewModel

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

  override fun setUpClicks(): Unit {
    binding.linearDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtDate.setText(selected)
        viewModel.adminAttendancelistModel.value?.txtDate = selected
        recallAttendancedata()
      }
    }
    binding.imgrefresh.setOnClickListener {
      binding.txtDate.setText(currentDate)
      binding.txtAllBranch.setText("All Branch")
      viewModel.adminAttendancelistModel.value?.txtListType = MyApp.getInstance().resources.getString(R.string.lbl_all)
      viewModel.adminAttendancelistModel.value?.txtBranchId = 0
      viewModel.adminAttendancelistModel.value?.txtDeptId = 0
      viewModel.adminAttendancelistModel.value?.txtListType = Seltype
      recallAttendancedata()
    }

    binding.searchViewEmplist.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(s: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(s: String?): Boolean {
        if (attendanceAdapter != null) {
          if (s != null) {
            filter(s)
          }
        }
        return false
      }
    })

    binding.imgFilter.setOnClickListener{
      var PopUpmenu = android.widget.PopupMenu(this, it)
      PopUpmenu.setOnMenuItemClickListener { item ->
        when (item.itemId) {
          R.id.item_all -> {
            viewModel.adminAttendancelistModel.value?.txtListType = MyApp.getInstance().resources.getString(R.string.lbl_all)
            recallAttendancedata()
            true
          }
          R.id.item_absent -> {
            viewModel.adminAttendancelistModel.value?.txtListType = MyApp.getInstance().resources.getString(R.string.lbl_absent)
            recallAttendancedata()
            true
          }
          R.id.item_present -> {
            viewModel.adminAttendancelistModel.value?.txtListType = MyApp.getInstance().resources.getString(R.string.lbl_present)
            recallAttendancedata()
            true
          }
          else -> false
        }
      }
      PopUpmenu.inflate(R.menu.attendance_menu)
      PopUpmenu.show()
    }

    binding.myhome.setOnClickListener {
      finish()
    }
    binding.txtAllBranch.setOnClickListener {
      callpopupBranchDept(true)
    }
    binding.txtAllDepartment.setOnClickListener {
      callpopupBranchDept(false)
    }

    binding.chkSelectall.setOnClickListener {
      var arr = viewModel.attendList.value
      var i = 0
      if(binding.chkSelectall.isChecked()){
        while (i < arr!!.size) {
          var empid = viewModel.attendList.value?.get(i)?.txtempid
          viewModel.attendList.value?.set(i,
            AttendanceRowModel(viewModel.attendList.value?.get(i)?.txtbranch,
              viewModel.attendList.value?.get(i)?.txtDept,"",
              viewModel.attendList.value?.get(i)?.txtempid,
              viewModel.attendList.value?.get(i)?.txtEmpName,
              viewModel.attendList.value?.get(i)?.txttotDuration!!,
              viewModel.attendList.value?.get(i)?.txtMobilenumber,
              viewModel.attendList.value?.get(i)?.organizationname,
              viewModel.attendList.value?.get(i)?.fin,viewModel.attendList.value?.get(i)?.fout,
              viewModel.attendList.value?.get(i)?.secin, viewModel.attendList.value?.get(i)?.secout,
              viewModel.attendList.value?.get(i)?.finimage,viewModel.attendList.value?.get(i)?.foutimage,
              viewModel.attendList.value?.get(i)?.secinimage, viewModel.attendList.value?.get(i)?.secoutimage,
              viewModel.attendList.value?.get(i)?.visitList!!,true)
          )
          viewModel.adminAttendancelistModel.value?.EmpIdlist?.add(EmpIdItem(empid))
          i++
        }
      } else {
        while (i < arr!!.size) {
          var empid = viewModel.attendList.value?.get(i)?.txtempid
          viewModel.attendList.value?.set(i,
            AttendanceRowModel(viewModel.attendList.value?.get(i)?.txtbranch,
              viewModel.attendList.value?.get(i)?.txtDept,"",
              viewModel.attendList.value?.get(i)?.txtempid,
              viewModel.attendList.value?.get(i)?.txtEmpName,
              viewModel.attendList.value?.get(i)?.txttotDuration!!,
              viewModel.attendList.value?.get(i)?.txtMobilenumber,
              viewModel.attendList.value?.get(i)?.organizationname,
              viewModel.attendList.value?.get(i)?.fin,viewModel.attendList.value?.get(i)?.fout,
              viewModel.attendList.value?.get(i)?.secin, viewModel.attendList.value?.get(i)?.secout,
              viewModel.attendList.value?.get(i)?.finimage,viewModel.attendList.value?.get(i)?.foutimage,
              viewModel.attendList.value?.get(i)?.secinimage, viewModel.attendList.value?.get(i)?.secoutimage,
              viewModel.attendList.value?.get(i)?.visitList!!,false)
          )
          viewModel.adminAttendancelistModel.value?.EmpIdlist?.remove(EmpIdItem(empid))
          i++
        }
      }
      attendanceAdapter.notifyDataSetChanged()
    }

    binding.btnApprove.setOnClickListener {
      if(viewModel.adminAttendancelistModel.value?.EmpIdlist.isNullOrEmpty()){
        Toast.makeText(this,R.string.msg_selEmpId,Toast.LENGTH_LONG).show()
      }else{
        val Request = AttedanceApproveRejectRequest(
          empList = viewModel.adminAttendancelistModel.value?.EmpIdlist,
          date = viewModel.adminAttendancelistModel.value?.txtDate,
          status = "Approved"
        )
        viewModel.callAllAttendanceAprRejApi(Request)
      }
    }

    binding.btnReject.setOnClickListener {
      if(viewModel.adminAttendancelistModel.value?.EmpIdlist.isNullOrEmpty()){
        Toast.makeText(this,R.string.msg_selEmpId,Toast.LENGTH_LONG).show()
      }else{
        val Request = AttedanceApproveRejectRequest(
          empList = viewModel.adminAttendancelistModel.value?.EmpIdlist,
          date = viewModel.adminAttendancelistModel.value?.txtDate,
          status = "Rejected"
        )
        viewModel.callAllAttendanceAprRejApi(Request)
      }
    }
  }

  fun recallAttendancedata(){
    viewModel.ImagesList.clear()
    viewModel.callFetchGetEmpAttendanceListApi()
    attendanceAdapter.notifyDataSetChanged()
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
    var filteredlist: ArrayList<AttendanceRowModel> = ArrayList()

    // running a for loop to compare elements.
    for (item in viewModel.attendList.value!!) {
      // checking if the entered string matched with any item of our recycler view.
      if (item.txtEmpName?.lowercase()?.contains(text.lowercase())!!) {
        // if the item is matched we are
        // adding it to our filtered list.
        filteredlist.add(item)
      }
    }
    if (filteredlist.isEmpty()) {
      filteredlist = attendanceAdapter.list as ArrayList<AttendanceRowModel>

    } else {
      // at last we are passing that filtered
      // list to our adapter class.
      attendanceAdapter.filterList(filteredlist)
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
          recallAttendancedata()
        }
        Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
      } else if (it is ErrorResponse) {
        onErrorFetchMsg(it.data ?: Exception())
      }
    }

    viewModel.AprRejAllAttendanceLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        if(it.data.status == true){
          recallAttendancedata()
        }
        Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
      } else if (it is ErrorResponse) {
        onErrorFetchMsg(it.data ?: Exception())
      }
    }
  }

  private fun onSuccessFetchMsg(response: SuccessResponse<AdminFetchEmpAttendanceListResponse>): Unit {
    viewModel.bindFetchAttendanceListResponse(response.data)
    if(viewModel.attendList.value?.size!! > 0) {
      binding.linearRecylerview.isVisible = true
      binding.linearNoMsg.isVisible = false
    }else{
      binding.linearRecylerview.isVisible = false
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

  fun onClickRecyclerAttendance(
    view: View,
    position: Int,
    item: AttendanceRowModel
  ): Unit {
    when(view.id) {
      R.id.linearAttendanceRow ->  {
        /*var intent= AttendanceListDialog.getIntent(this,null)
        intent.putExtra(IntentParameters.Position,position)
        startActivity(intent)*/
        callDialog(position)
      }
      R.id.txtEmployee ->  {
        var selitem: Int? = 0
        if(attendanceAdapter.list.size > 0) {
          selitem = attendanceAdapter.list.get(position).txtempid
        }else{
          selitem = viewModel.attendList.value?.get(position)!!.txtempid
        }
        if(viewModel.adminAttendancelistModel.value?.EmpIdlist?.contains(EmpIdItem(selitem)) == true) {
          viewModel.adminAttendancelistModel.value?.EmpIdlist?.remove(EmpIdItem(selitem!!))
        }else{
          viewModel.adminAttendancelistModel.value?.EmpIdlist?.add(EmpIdItem(selitem!!))
        }
      }
    }
  }

  fun callDialog(position: Int): Boolean {
    AttendanceListDialogActivity(this, position).show()
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
      recallAttendancedata()
    }else{
      binding.txtAllDepartment.setText(selectedItem.txtName)
      viewModel.adminAttendancelistModel.value?.txtDeptId = selectedItem.txtValue
      recallAttendancedata()
    }
  }
}
