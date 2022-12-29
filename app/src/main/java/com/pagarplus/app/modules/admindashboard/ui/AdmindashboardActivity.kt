package com.pagarplus.app.modules.admindashboard.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityAdmindashboardBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminattendancelist.ui.AdminAttendancelistActivity
import com.pagarplus.app.modules.admindashboard.data.model.RowdashboardModel
import com.pagarplus.app.modules.admindashboard.data.viewmodel.AdmindashboardVM
import com.pagarplus.app.modules.adminemplist.ui.AdminemplistActivity
import com.pagarplus.app.modules.advertise.ui.AdvertiseActivity
import com.pagarplus.app.modules.advertise.ui.AdvertiseListActivity
import com.pagarplus.app.modules.aprrejloanleavelist.ui.ApproveRejectleaveActivity
import com.pagarplus.app.modules.createbranch.ui.CreateBranchActivity
import com.pagarplus.app.modules.createemployee.ui.CreateEmployeeActivity
import com.pagarplus.app.modules.expensereport.ui.ExpenseReportListActivity
import com.pagarplus.app.modules.firebase_notifications.ui.FirebaseNotificationActivity
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.modules.language_selection
import com.pagarplus.app.modules.notification.ui.NotificationActivity
import com.pagarplus.app.modules.payment.ui.PaymentActivity
import com.pagarplus.app.modules.reports.ui.ReportsActivity
import com.pagarplus.app.modules.signup.ui.EditAdminProfileActivity
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.modules.workholidays.ui.WorkholidaysActivity
import com.pagarplus.app.network.models.AdminDashboard.FetchAdminDashboardListResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class AdmindashboardActivity :
    BaseActivity<ActivityAdmindashboardBinding>(R.layout.activity_admindashboard), BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: AdmindashboardVM by viewModels<AdmindashboardVM>()
  val pref: PreferenceHelper by inject()
  val mLoginDetails=pref.getLoginDetails<LoginResponse>()
  var isBack: Boolean = true
  var selBranchID: Int? = 0
  lateinit var profdetails: CreateCreateEmployeeRequest
  var currentDate: String = ""
  lateinit var dashboardAdapter : DashboardAdapter

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.admindashboardVM = viewModel

    currentDate = getCurrentDate()

    binding.txtDateAttendence.setText(currentDate)
    viewModel.callFetchAdminDashboardDataApi(currentDate)

    dashboardAdapter = DashboardAdapter(viewModel.detailsList.value?:mutableListOf())
    binding.recyclerDashboardList.adapter = dashboardAdapter
    dashboardAdapter.setOnItemClickListener(
      object : DashboardAdapter.OnItemClickListener {
        override fun onItemClick(view:View, position:Int, item : RowdashboardModel) {
          onClickRecyclerDetails(view, position, item)
        }
      }
    )
    viewModel.detailsList.observe(this) {
      if(!dashboardAdapter.equals("")) {
        dashboardAdapter.updateData(it)
      }
    }
  }

  fun setProfileDetails(){
    binding.txtHi.setText("Welcome,\n"+profdetails.organization)
    binding.drawerincluded.txtMWBTechnologie.setText(profdetails.organization)

    viewModel.admindashboardModel.value?.txtImgProfUrl = profdetails.profileImageURl
    Glide.with(this).load(profdetails.profileImageURl).into(binding.drawerincluded.roundedimage)

    /*basic edition requirements*/
    if(viewModel.profiledetails?.showBranch == false){
      binding.txtAllBranch.isEnabled = false
    }else{
      binding.txtAllBranch.isEnabled = true
    }

    if(viewModel.profiledetails?.showExpensemodule == false){
      binding.drawerincluded.txtExpenseReport.isVisible = false
    }else{
      binding.drawerincluded.txtExpenseReport.isVisible = true
    }

    if(viewModel.profiledetails?.planType.equals("Basic")){
      if(viewModel.profiledetails?.showBranch == false){
        binding.drawerincluded.txtCreateBranch.isVisible = false
      }else{
        binding.drawerincluded.txtCreateBranch.isVisible = true
      }
    }else{
      binding.drawerincluded.txtCreateBranch.isVisible = true
    }

    if(viewModel.profiledetails?.planExpiryDate!! <= currentDate){
      OpenUpgradeDialog()
    }
  }

  override fun setUpClicks(): Unit {
    binding.imageMenu.setOnClickListener {
      toggleDrawer()
    }
    binding.imgrefresh.setOnClickListener {
      binding.txtDateAttendence.setText(currentDate)
      binding.txtAllBranch.setText("All Branch")
      viewModel.admindashboardModel.value?.txtBranchId = 0
      viewModel.callFetchAdminDashboardDataApi(currentDate)
    }
    binding.imageCalendarAttendence.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtDateAttendence.setText(selected)
        //viewModel.admindashboardModel.value?.txtAttendanceDate = selected
        viewModel.callFetchAdminDashboardDataApi(selected)
      }
    }

    binding.txtAllBranch.setOnClickListener {
      callpopupBranchDept(true)
    }

    binding.btnCreateEmployeeOne.setOnClickListener {
      if(viewModel.admindashboardModel.value?.txttotEmpVal!!.equals("0")){
        startActivity(CreateEmployeeActivity.getIntent(this,null))
      }else {
        val destIntent = AdminemplistActivity.getIntent(this, null)
        startActivity(destIntent)
      }
    }

    binding.btnNotifications.setOnClickListener {
      val destIntent = NotificationActivity.getIntent(this, null)
      startActivity(destIntent)
    }

    binding.btnAdvertise.setOnClickListener {
      val destIntent = AdvertiseListActivity.getIntent(this, null)
      startActivity(destIntent)
    }

    binding.btnReports.setOnClickListener {
      val intent = ReportsActivity.getIntent(this, null)
      intent.putExtra(IntentParameters.AdminId,mLoginDetails?.userID?:0)
      startActivity(intent)
    }

    /*menu drawer item click*/
    binding.drawerincluded.txtCreateEmp.setOnClickListener {
      if(viewModel.admindashboardModel.value?.txttotEmpVal!!.equals("0")){
        startActivity(CreateEmployeeActivity.getIntent(this,null))
      }else {
        val destIntent = AdminemplistActivity.getIntent(this, null)
        startActivity(destIntent)
      }
    }
    binding.drawerincluded.txtMessages.setOnClickListener {
      val intent = NotificationActivity.getIntent(this, null)
      startActivity(intent)
    }

    binding.drawerincluded.txtNotifications.setOnClickListener {
      val intent = FirebaseNotificationActivity.getIntent(this, null)
      startActivity(intent)
    }
    binding.drawerincluded.txtReports.setOnClickListener {

    }
    binding.drawerincluded.txtAdvertise.setOnClickListener {
      val destIntent = AdvertiseListActivity.getIntent(this, null)
      startActivity(destIntent)
    }
    binding.drawerincluded.txtApplyLeave.setOnClickListener {
      val intent = ApproveRejectleaveActivity.getIntent(this, null)
      intent.putExtra(IntentParameters.IsLeaveOrLoan, true)
      startActivity(intent)
    }

    binding.drawerincluded.txtApplyLoan.setOnClickListener {
      val intent = ApproveRejectleaveActivity.getIntent(this, null)
      intent.putExtra(IntentParameters.IsLeaveOrLoan, false)
      startActivity(intent)
    }

    binding.drawerincluded.txtPayment.setOnClickListener {
      val intent = PaymentActivity.getIntent(this, null)
      startActivity(intent)
    }

    binding.drawerincluded.txtCreateBranch.setOnClickListener {
      val intent = CreateBranchActivity.getIntent(this, null)
      startActivity(intent)
    }

    binding.drawerincluded.txtEditProfile.setOnClickListener {
      val intent = EditAdminProfileActivity.getIntent(this, null)
      startActivity(intent)
    }

    binding.drawerincluded.txtHolidays.setOnClickListener {
      val destIntent = WorkholidaysActivity.getIntent(this, null)
      startActivity(destIntent)
    }

    binding.drawerincluded.txtExpenseReport.setOnClickListener {
      val destIntent = ExpenseReportListActivity.getIntent(this, null)
      destIntent.putExtra(IntentParameters.AdminId,mLoginDetails?.userID?:0)
      startActivity(destIntent)
    }

    binding.drawerincluded.txtLogout.setOnClickListener {
      isBack = false
      showExitAlert()
    }

    binding.drawerincluded.txtUpgrade.setOnClickListener {
    }

    binding.drawerincluded.txtChgLang.setOnClickListener {
      val intent = Intent(this, language_selection::class.java)
      startActivity(intent)
    }
  }

  fun OpenUpgradeDialog(){
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()
    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.upgrade_popup, null)
    dialogBuilder.setView(dialogView).setCancelable(true)

    val btn_upgrade = dialogView.findViewById<AppCompatButton>(R.id.btnUpgrade)

    val alertDialog = dialogBuilder.create()

    alertDialog.show();

    btn_upgrade.setOnClickListener{
      alertDialog.dismiss()
    }
  }

  /*call branch/Dept popup*/
  fun callpopupBranchDept(isBranch:Boolean){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsBranchDept, isBranch)
    var itemlistDialog = BranchDeptlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
  }

  private fun toggleDrawer(): Unit {
    if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.openDrawer(GravityCompat.START)
    } else {
      binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
  }

  /*create employee api observe method */
  override fun addObservers(): Unit {
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
    viewModel.fetchDashboardLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        profdetails = pref.getProfileDetails<CreateCreateEmployeeRequest>()!!
        onSuccessGetDashboard(it)
      } else if(it is ErrorResponse) {
        onErrorMessage(it.data ?:Exception())
      }
    }

    viewModel.LogoutLiveData.observe(this) {
      pref.setIsAdmin(false)
      pref.setLoginTrue(false)
      val intent = Intent(this, UserloginActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      startActivity(intent)
      finish()
    }
  }

  private fun onSuccessGetDashboard(response: SuccessResponse<FetchAdminDashboardListResponse>): Unit {
    viewModel.bindadminDashboardResponse(response.data)
    if(viewModel.detailsList.value?.size!! > 0){
      binding.linearTableheader.isVisible = true
    }else{
      binding.linearTableheader.isVisible = false
    }
    setProfileDetails()
  }

  private fun onErrorMessage(exception: Exception): Unit {
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
        this.alert(MyApp.getInstance().getString(R.string.lbl_status),errMessage) {
          neutralButton {
          }
        }
      }
    }
  }

  fun onClickRecyclerDetails(
    view: View,
    position: Int,
    item: RowdashboardModel
  ): Unit {
    when(view.id) {
      R.id.linearBranchRow ->  {
        val destIntent = AdminAttendancelistActivity.getIntent(this, null)
        destIntent.putExtra(IntentParameters.AllEmp,"All")
        destIntent.putExtra(IntentParameters.selDate,binding.txtDateAttendence.getText().toString())
        destIntent.putExtra(IntentParameters.BranchID,viewModel.detailsList.value?.get(position)?.branchid)
        destIntent.putExtra(IntentParameters.Branch,viewModel.detailsList.value?.get(position)?.txtBranch)
        startActivity(destIntent)
      }
    }
  }

  companion object {
    const val TAG: String = "ADMINDASHBOARD_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, AdmindashboardActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun onBackPressed() {
    isBack = true
    showExitAlert()
  }

  fun showExitAlert(){
    val builder = AlertDialog.Builder(this)
    builder.setCancelable(false)
    if(isBack){
      builder.setMessage(R.string.msg_closeapp)
    }else{
      builder.setMessage(R.string.msg_logout)
    }
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    builder.setPositiveButton(R.string.msg_yes) { dialog, which -> //if user pressed "yes", then he is allowed to exit from application
      if(isBack){
        finish()
      }else {
        viewModel.UserLogout()
      }
    }
    builder.setNegativeButton(R.string.msg_no) { dialog, which -> //if user select "No", just cancel this dialog and continue with app
      dialog.cancel()
    }
    val alert = builder.create()
    alert.show()
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("Branch","$selectedItem")
    if(selectedItem.isBranch==true){
      binding.txtAllBranch.setText(selectedItem.txtName)
      viewModel.admindashboardModel.value?.txtBranchId = selectedItem.txtValue
      viewModel.callFetchAdminDashboardDataApi(binding.txtDateAttendence.getText().toString())
      selBranchID = selectedItem.txtValue
    }
  }
}