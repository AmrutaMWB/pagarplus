package com.pagarplus.app.modules.attendance_details.ui

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityAdminAttendancelistBinding
import com.pagarplus.app.databinding.ActivityAttendancedetailsBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminattendancelist.data.model.AttendanceRowModel
import com.pagarplus.app.modules.adminattendancelist.data.viewmodel.AdminAttendancelistVM
import com.pagarplus.app.modules.adminattendancelist.ui.AttendanceAdapter
import com.pagarplus.app.modules.aprrejloanleavelist.ui.LeaveListAdapter
import com.pagarplus.app.modules.attendance_details.data.model.AttendanceDetailsModel
import com.pagarplus.app.modules.attendance_details.data.viewmodel.AttendancedetailsVM
import com.pagarplus.app.modules.expensereport.ui.ExpenseDialog
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.network.models.AdminDashboard.AdminFetchEmpAttendanceListResponse
import com.pagarplus.app.network.models.AdminDashboard.AttendanceItem
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

class AttendanceDetailsActivity :
    BaseActivity<ActivityAttendancedetailsBinding>(R.layout.activity_attendancedetails) {
  public val viewModel: AttendancedetailsVM by viewModels<AttendancedetailsVM>()
  var selDate: String? = ""
  var branchID: Int? = 0
  var branch: String? = ""
  private var mIsAdmin:Boolean=false
  lateinit var attendanceAdapter : AttendanceItemAdapter

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    mIsAdmin = viewModel.userdetails?.isAdmin!!
    viewModel.attendanceDetailsModel.value?.txtEmpID = intent.getStringExtra(IntentParameters.EmpID)?.toInt()
    selDate = intent.getStringExtra(IntentParameters.selDate)

    val sdf = SimpleDateFormat("dd-MM-yyyy")
    val currentDate = sdf.format(Date())

    if(selDate.isNullOrEmpty()) {
      viewModel.attendanceDetailsModel.value?.txtFromDate = currentDate
      viewModel.attendanceDetailsModel.value?.txtToDate = currentDate
    }else{
      viewModel.attendanceDetailsModel.value?.txtFromDate = selDate
      viewModel.attendanceDetailsModel.value?.txtToDate = selDate
    }
    viewModel.callFetchGetEmpAttendanceDetailsApi()

    attendanceAdapter = AttendanceItemAdapter(viewModel.attendList.value?:mutableListOf())
    binding.recyclerDetailsView.adapter = attendanceAdapter
    attendanceAdapter.setOnItemClickListener(
    object : AttendanceItemAdapter.OnItemClickListener {
      override fun onItemClick(view:View, position:Int, item : AttendanceItem) {
        //onClickRecyclerAttendance(view, position, item)
      }
    }
    )
    viewModel.attendList.observe(this) {
      attendanceAdapter.updateData(it)
    }
    binding.attendanceDetailsVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.fromDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.fromDate.setText(selected)
        viewModel.attendanceDetailsModel.value?.txtFromDate = selected
        viewModel.callFetchGetEmpAttendanceDetailsApi()
      }
    }
    binding.toDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.toDate.setText(selected)
        viewModel.attendanceDetailsModel.value?.txtToDate = selected
        viewModel.callFetchGetEmpAttendanceDetailsApi()
      }
    }
    binding.btnBack.setOnClickListener {
      finish()
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
  }

  private fun onSuccessFetchMsg(response: SuccessResponse<AdminFetchEmpAttendanceListResponse>): Unit {
    viewModel.bindFetchAttendanceDetailsResponse(response.data)
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

 /* fun onClickRecyclerAttendance(view: View, position: Int, item: AttendanceDetailsModel): Unit {
    when(view.id) {
      R.id.imgDetails ->  {
        callDialog(position)
      }
    }
  }*/

  /*fun callDialog(position: Int): Boolean {
    AttendanceDialog(this, position).show()
    return true
  }*/

  companion object {
    const val TAG: String = "ATTENDANCEDETAILS_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, AttendanceDetailsActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
