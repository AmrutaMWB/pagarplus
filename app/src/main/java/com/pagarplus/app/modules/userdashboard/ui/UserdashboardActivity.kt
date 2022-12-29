package com.pagarplus.app.modules.userdashboard.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.github.demono.AutoScrollViewPager
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.ui.AutoScrollPagerAdapter
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.databinding.ActivityUserdashboardBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.applylol.ui.ApplylolActivity
import com.pagarplus.app.modules.attedence.ui.AttendanceActivity
import com.pagarplus.app.modules.editprofile.ui.EditProfileActivity
import com.pagarplus.app.modules.expense.ui.ExpenseActivity
import com.pagarplus.app.modules.firebase_notifications.ui.FirebaseNotificationActivity
import com.pagarplus.app.modules.language_selection
import com.pagarplus.app.modules.notification.ui.NotificationActivity
import com.pagarplus.app.modules.userdashboard.`data`.viewmodel.UserdashboardVM
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.modules.workholidays.ui.CalenderActivity
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.koin.android.ext.android.inject
import java.lang.Exception
import kotlin.String
import kotlin.Unit

class UserdashboardActivity :
    BaseActivity<ActivityUserdashboardBinding>(R.layout.activity_userdashboard){
  private val viewModel: UserdashboardVM by viewModels<UserdashboardVM>()
  var autoScrollPagerAdapter: AutoScrollPagerAdapter? = null
  var viewPager: AutoScrollViewPager? = null
  val pref: PreferenceHelper by inject()
  val mLoginDetails=pref.getLoginDetails<LoginResponse>()
  var isBack: Boolean = true
  private val AUTO_SCROLL_THRESHOLD_IN_MILLI = 1000
  var profiledetails = pref.getProfileDetails<CreateCreateEmployeeRequest>()

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.userdashboardVM = viewModel

    if(viewModel.userdetails?.isActive == false){
      val builder = AlertDialog.Builder(this)
      builder.setMessage(R.string.msg_inactive)
      builder.setPositiveButton(R.string.material_calendar_positive_button) { dialogInterface, which ->
        viewModel.callUserLogout()
        dialogInterface.dismiss()
      }

      val alertDialog: AlertDialog = builder.create()
      alertDialog.setCancelable(false)
      alertDialog.show()
    }

    lifecycleScope.launchWhenCreated{
      viewModel.progressLiveData.postValue(true)
      //ApiUtil(applicationContext).setProfileDetails(mLoginDetails?.userID?:0)
      val visitlist = ApiUtil(applicationContext).getFeatureTypes(URLParameters.Visit)
      val sessionlist = ApiUtil(applicationContext).getFeatureTypes(URLParameters.Period)
      viewModel.callFetch1Api(profiledetails?.adminID?:0)
      viewModel.progressLiveData.postValue(false)
      pref.setSessionTypes(sessionlist)
      pref.setVisitTypes(visitlist)
    }
    this@UserdashboardActivity.hideKeyboard()

    if(viewModel.profiledetails?.showExpensemodule == false){
      binding.btnMyExpenses.isVisible = false
      binding.included.txtExpenses.isVisible = false
    }else{
      binding.btnMyExpenses.isVisible = true
      binding.included.txtExpenses.isVisible = true
    }
  }

  fun setProfileDetails(){
      binding.txtUserName.text = mLoginDetails?.username
      binding.included.txtUsername.text = mLoginDetails?.username
      binding.included.txtDesignation.text = profiledetails?.designation
      binding.included.txtFirmName.text = profiledetails?.organization
  }
  override fun setUpClicks(): Unit {
    binding.imageMenu.setOnClickListener {
      	toggleDrawer()
    }
    binding.btnMyAttendance.setOnClickListener {
      var intent=AttendanceActivity.getIntent(this,null)
      startActivity(intent)
    }
    binding.btnMyNotificationsOne.setOnClickListener {
      var intent=NotificationActivity.getIntent(this,null)
      intent.putExtra(IntentParameters.isEmployee,true)
      startActivity(intent)
    }
    binding.btnMyExpenses.setOnClickListener {
      var intent=ExpenseActivity.getIntent(this,null)
      startActivity(intent)
    }
    binding.included.txtApplyLeave.setOnClickListener {
      val intent=ApplylolActivity.getIntent(this,null)
      intent.putExtra(IntentParameters.IsLeaveOrLoan,true)
      startActivity(intent)
    }
    binding.included.txtLogout.setOnClickListener {
      isBack = false
      showLogoutDialog()
    }
    binding.included.txtApplyLoan.setOnClickListener {
      val intent=ApplylolActivity.getIntent(this,null)
      intent.putExtra(IntentParameters.IsLeaveOrLoan,false)
      startActivity(intent)
    }
    binding.included.txtEditProfile.setOnClickListener {
      val destIntent=EditProfileActivity.getIntent(this,null)
      startActivity(destIntent)
    }
    binding.included.txtNotifications.setOnClickListener {
      var intent=FirebaseNotificationActivity.getIntent(this,null)
      startActivity(intent)
    }

    binding.included.txtMessages.setOnClickListener {
      var intent= NotificationActivity.getIntent(this,null)
      startActivity(intent)
    }
    binding.included.txtHelp.setOnClickListener {
      val intent=Intent(this,CalenderActivity::class.java)
      startActivity(intent)
    }
    binding.included.txtChgLang.setOnClickListener {
      val intent = Intent(this, language_selection::class.java)
      startActivity(intent)
    }
  }

  override fun onPause() {
    if(viewPager!=null)
      viewPager!!.stopAutoScroll()
    super.onPause()
  }

  private fun showLogoutDialog() {
    val builder = AlertDialog.Builder(this)
    //set message for alert dialog
   if(isBack){
       builder.setMessage(R.string.msg_closeapp)
   }else{
     builder.setMessage(R.string.msg_logout)
   }
    //performing positive action
    builder.setPositiveButton(R.string.msg_yes) { dialogInterface, which ->
      if(isBack){
        finish()
      }else {
        viewModel.callUserLogout()
      }
    }

    //performing negative action
    builder.setNegativeButton(R.string.msg_no) { dialogInterface, which ->
      //Toast.makeText(applicationContext, "clicked No", Toast.LENGTH_LONG).show()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()
  }

  private fun toggleDrawer(): Unit {
    if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START))
    {
      binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    else {
      binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
  }

  override fun addObservers() {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this@UserdashboardActivity) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@UserdashboardActivity.showProgressDialog()
      } else  {
        progressDialog?.dismiss()
      }
    }

    viewModel.fetch1LiveData.observe(this@UserdashboardActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetch1(it)
        setProfileDetails()
      } else if(it is ErrorResponse)  {
        onErrorFetch1(it.data ?:Exception())
      }
    }

    viewModel.LogoutLiveData.observe(this@UserdashboardActivity) {
      pref.setIsAdmin(false)
      pref.setLoginTrue(false)
      val intent = Intent(this, UserloginActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      startActivity(intent)
      finish()
    }
  }

  private fun onSuccessFetch1(response: SuccessResponse<CreateCreateBannerResponse>) {
    var bannerList = response.data.advertiseList
    val imageList = ArrayList<SlideModel>()
    bannerList?.forEach {
      imageList.add(SlideModel("${it?.bannerImage}",it?.description,ScaleTypes.CENTER_CROP))
    }
    binding.scrollViewPager.setImageList(imageList)
   /* autoScrollPagerAdapter = AutoScrollPagerAdapter(supportFragmentManager, bannerList)
    viewPager = binding.viewPager //findViewById<AutoScrollViewPager>(R.id.viewPager)//
    viewPager!!.height
    viewPager!!.adapter = autoScrollPagerAdapter
    // start auto scroll
    viewPager!!.startAutoScroll()
    // set auto scroll time in mili
    viewPager!!.setSlideDuration(AUTO_SCROLL_THRESHOLD_IN_MILLI);
    // enable recycling using true
    viewPager!!.isCycle = true*/
  }

  private fun onErrorFetch1(exception: Exception) {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (viewPager != null)
      viewPager!!.startAutoScroll()
  }

  public companion object {
    public const val TAG: String = "USER_DASHBOARD_ACTIVITY"
    public fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, UserdashboardActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun onBackPressed() {
    isBack = true
    showLogoutDialog()
  }
}
