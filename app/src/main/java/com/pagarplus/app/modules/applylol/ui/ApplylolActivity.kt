package com.pagarplus.app.modules.applylol.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.appcomponents.views.TimePickerFragment
import com.pagarplus.app.databinding.ActivityApplylolBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.applylol.data.model.LeaveModel
import com.pagarplus.app.modules.applylol.data.model.LoanModel
import com.pagarplus.app.modules.applylol.data.viewmodel.ApplylolVM
import com.pagarplus.app.modules.expensereport.data.model.ExpenseRatingItem
import com.pagarplus.app.modules.expensereport.ui.ExpenseRatingAdapter
import com.pagarplus.app.modules.expensereport.ui.ExpenseReportAdapter
import com.pagarplus.app.modules.workholidays.ui.WorkholidaysActivity
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetleaveloanListRequest
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.expense.ExpenseReportRequest
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import com.pagarplus.app.network.models.leavelone.LeaveRequest
import com.pagarplus.app.network.models.leavelone.LoanRequest
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import retrofit2.HttpException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import org.koin.android.ext.android.inject
import kotlin.collections.ArrayList

class ApplylolActivity : BaseActivity<ActivityApplylolBinding>(R.layout.activity_applylol),
  AdapterView.OnItemSelectedListener {
  private val viewModel: ApplylolVM by viewModels<ApplylolVM>()
  private val prefs:PreferenceHelper by inject()
  private var mIsLeaveOrLoan=false
  private val profile=prefs.getProfileDetails<CreateCreateEmployeeRequest>()
   private var mLeaveFromDate=SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
   private var mLeaveToDate=SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
  private var mLeaveFromTime=SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(Date())
  private var mCurrentTime=SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(Date())
  private var mLeaveTypesList= arrayListOf<FeaturesTypes>()
  private var mLeaveType=""
  private var mLoanType="Select Type"
  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    val cal=Calendar.getInstance()
    initLoanSpinner()
    cal.set(cal.get(Calendar.YEAR),(cal.get(Calendar.MONTH).minus(2)),1)
    binding.fromDate.text=SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
    viewModel.applylolModel.value?.ftFromDate=SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
    lifecycleScope.launchWhenCreated {
      viewModel.progressLiveData.postValue(true)
      mLeaveTypesList = ApiUtil(applicationContext).getFeatureTypes(URLParameters.Leave)
      initSpinner()
      viewModel.progressLiveData.postValue(false)
    }

     mIsLeaveOrLoan=intent.getBooleanExtra(IntentParameters.IsLeaveOrLoan,false)
    if(mIsLeaveOrLoan)
    {
      binding.txtScreenName.text=MyApp.getInstance().resources.getString(R.string.lbl_apply_leave)
      binding.LeaveLayout.visibility= View.VISIBLE
      binding.LoanLayout.visibility=View.GONE

      binding.btnAddRequest.setOnClickListener {
        binding.LeaveLayout.visibility= View.VISIBLE
        binding.LeavesListLayout.visibility=View.GONE
        binding.datefilterlayout.visibility=View.GONE
      }
      binding.btnListRequest.setOnClickListener {
        sendGetLeaveListRequest()
        binding.LeaveLayout.visibility= View.GONE
        binding.LeavesListLayout.visibility=View.VISIBLE
        binding.datefilterlayout.visibility=View.VISIBLE

      }
    }else{
      binding.txtScreenName.text=MyApp.getInstance().resources.getString(R.string.lbl_apply_loan)
      binding.LeaveLayout.visibility= View.GONE
      binding.LoanLayout.visibility=View.VISIBLE

      binding.btnAddRequest.setOnClickListener {
        binding.LoanLayout.visibility= View.VISIBLE
        binding.LoanListLayout.visibility=View.GONE
        binding.datefilterlayout.visibility=View.GONE
      }
      binding.btnListRequest.setOnClickListener {
        sendGetLoanListRequest()
        binding.LoanLayout.visibility= View.GONE
        binding.LoanListLayout.visibility=View.VISIBLE
        binding.datefilterlayout.visibility=View.VISIBLE
      }
    }

    initAdapters()
    binding.applyVM = viewModel
  }

  private fun initAdapters() {
    val recyclerLeaveAdapter = RecyclerLeaveAdapter(viewModel.recyclerLeaveList.value?: mutableListOf(), this)
    binding.recyclerLeaveList.adapter = recyclerLeaveAdapter
    recyclerLeaveAdapter.setOnItemClickListener(
      object : RecyclerLeaveAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, item: LeaveModel) {
          val builder = AlertDialog.Builder(this@ApplylolActivity)
          val strBuilder = StringBuilder()
          strBuilder.appendLine("Reason : ${item.ReasonForLeave}")
          if(!item.AdminComment.isNullOrEmpty())
            strBuilder.appendLine("Admin Comment : ${item.AdminComment}")
          if(!item.ApprovedDate.isNullOrEmpty())
            strBuilder.appendLine("Updated At : ${item.ApprovedDate?.extractDate()}")
          builder.setTitle("Leave Details")
          builder.setMessage(strBuilder.toString())
          builder.setIcon(android.R.drawable.ic_menu_info_details)
          val alertDialog: AlertDialog = builder.create()
          alertDialog.setCancelable(true)
          alertDialog.show()

        }
      }
    )
    viewModel.recyclerLeaveList.observe(this) {
      recyclerLeaveAdapter.updateData(it as ArrayList<LeaveModel>)

    }



    val recyclerLoanAdapter = RecyclerLoanAdapter(viewModel.recyclerLoanList.value?: mutableListOf(), this)
    binding.recyclerLoanList.adapter = recyclerLoanAdapter

    recyclerLoanAdapter.setOnItemClickListener(
      object : RecyclerLoanAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, item: LoanModel) {
          val builder = AlertDialog.Builder(this@ApplylolActivity)
          val strBuilder = StringBuilder()
          strBuilder.appendLine("Reason : ${item.ReasonForLoan}")
          if(!item.Comment.isNullOrEmpty())
          strBuilder.appendLine("Admin Comment : ${item.Comment}")
          builder.setTitle("Loan Details")
          builder.setMessage(strBuilder.toString())
          builder.setIcon(android.R.drawable.ic_menu_info_details)
          val alertDialog: AlertDialog = builder.create()
          alertDialog.setCancelable(true)
          alertDialog.show()

        }
      }
    )
    viewModel.recyclerLoanList.observe(this) {
      recyclerLoanAdapter.updateData(it as ArrayList<LoanModel>)

    }

    binding.leaveSearchView.isActivated = true
    binding.leaveSearchView.clearFocus()
    binding.leaveSearchView.setIconifiedByDefault(false)
    binding.leaveSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(s: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(s: String?): Boolean {
        if (recyclerLeaveAdapter != null) {
          if (s != null) {
            recyclerLeaveAdapter.getFilter().filter(s)
          }
        }
        return false
      }
    })
    binding.loanSearchView.isActivated = true
    binding.loanSearchView.clearFocus()
    binding.loanSearchView.setIconifiedByDefault(false)
    binding.loanSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(s: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(s: String?): Boolean {
        if (recyclerLoanAdapter != null) {
          if (s != null) {
            recyclerLoanAdapter.getFilter().filter(s)
          }
        }
        return false
      }
    })
  }


  fun initSpinner(){
    Log.e("LeaveTypes","$mLeaveTypesList")
    val leaveTypeAdapter = ArrayAdapter(this@ApplylolActivity, R.layout.attendance_spinner_item, mLeaveTypesList.map { it.featureName })
    binding.spinnerLeaveType.adapter = leaveTypeAdapter
    binding.spinnerLeaveType.onItemSelectedListener = this@ApplylolActivity
  }

  fun initLoanSpinner(){
    val loanTypesAdapter = ArrayAdapter(this, R.layout.attendance_spinner_item,resources.getTextArray(R.array.loan_types))
    binding.spinnerLoanType.adapter=loanTypesAdapter
    binding.spinnerLoanType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
       mLoanType= parentView?.getItemAtPosition(position).toString()
      }
      override fun onNothingSelected(parentView: AdapterView<*>?) {
        // your code here
      }
    }
  }

  fun clearLoanFeilds(){
    binding.etLoanAmount.text.clear()
    binding.etLoanDetails.text.clear()
  }
  fun clearLeaveFeilds(){
    binding.etLeaveDays.text.clear()
    binding.etReasonForLeave.text.clear()
  }
  override fun setUpClicks(): Unit {
    val modelValue=viewModel.applylolModel.value!!

    binding.btnLeaveSubmit.setOnClickListener {
      val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
      val fromdate = formatter.parse("${modelValue.txtLeaveFromDate} ${modelValue.txtLeaveFromTime}")?: Date()
      val noOfDays=modelValue.txtLeaveDays?.toDouble()?:0.0
      val dayLong:Long=noOfDays.times(86400000).toLong()
      val period=(fromdate.time + dayLong)
      val todate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(period))

      this@ApplylolActivity.hideKeyboard()
      if(validateLeaveDetails()){
        val request=LeaveRequest(
          empID = profile?.empID.toString(),comment = modelValue.txtReasonForLeave,
          fromDateTime = "${modelValue.txtLeaveFromDate} ${modelValue.txtLeaveFromTime}",
          toDateTime = "$todate",
         noOfDays = modelValue.txtLeaveDays?.toDouble(),
        leaveType = mLeaveType)
        viewModel.callCreateRequestLeaveApi(request)
      }

    }
    binding.btnBack.setOnClickListener {
      finish()
    }
    binding.btnLoanSubmit.setOnClickListener {
      this@ApplylolActivity.hideKeyboard()
      if(validateLoanDetails()){
        val request=LoanRequest(empID = profile?.empID?.toString(),amount = modelValue.txtLoanAmount,comment = modelValue.txtLoaDescription, LoanType = mLoanType)
        viewModel.callCreateRequestLoanApi(request)
      }
    }
    binding.leaveFromDateLayout.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        mLeaveFromDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtLeaveFromDate.text = mLeaveFromDate
      }
    }
    binding.leaveToDateLayout.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        mLeaveToDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
        binding.txtLeaveToDate.text = mLeaveToDate
      }
    }
    binding.leaveFromTimeLayout.setOnClickListener {
      val destinationInstance = TimePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        mLeaveFromTime = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(selectedDate)
        binding.txtLeaveFromTime.text = mLeaveFromTime
      }
    }
    binding.leaveToTimeLayout.setOnClickListener {
      val destinationInstance = TimePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        mLeaveFromTime = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(selectedDate)
        binding.txtLeaveToTime.text = mCurrentTime
      }
    }

    binding.fromDate.setOnClickListener {
      Log.e("Dates","FromDate:${modelValue?.ftFromDate}..ToDate:${modelValue?.ftToDate}")
      val destinationInstance = DatePickerFragment.getInstance(false,modelValue?.ftFromDate?.getDate())

      destinationInstance.show(this.supportFragmentManager, DatePickerFragment.TAG) { selectedDate ->
        if(selectedDate<=viewModel.applylolModel.value?.ftToDate?.getDate())
        {
          binding.fromDate.text=SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
         if(mIsLeaveOrLoan)
           sendGetLeaveListRequest()
          else
            sendGetLoanListRequest()
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()

      }

    }
    binding.toDate.setOnClickListener {
      Log.e("Dates","FromDate:${modelValue?.ftFromDate}..ToDate:${modelValue?.ftToDate}")
      val destinationInstance = DatePickerFragment.getInstance(false,modelValue?.ftToDate?.getDate())

      destinationInstance.show(this.supportFragmentManager, DatePickerFragment.TAG) { selectedDate ->
        if(selectedDate>=viewModel.applylolModel.value?.ftFromDate?.getDate())
        {
          binding.toDate.text=SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
          if(mIsLeaveOrLoan)
            sendGetLeaveListRequest()
          else
            sendGetLoanListRequest()
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()

      }

    }

  }


  fun sendGetLoanListRequest(){
    val leaveListRequest= FetchGetleaveloanListRequest(
      adminID =profile?.adminID?:0,
      fromDate = viewModel.applylolModel.value?.ftFromDate,
      toDate = viewModel.applylolModel.value?.ftToDate+" $mCurrentTime")
    viewModel.getLoanList(leaveListRequest)
  }

  fun sendGetLeaveListRequest(){
    val leaveListRequest= FetchGetleaveloanListRequest(
      adminID =profile?.adminID?:0,
      fromDate = viewModel.applylolModel.value?.ftFromDate,
      toDate = viewModel.applylolModel.value?.ftToDate+" $mCurrentTime")
    viewModel.getLeaveList(leaveListRequest)
  }
  fun validateLoanDetails():Boolean{
    val amt=binding.etLoanAmount.text.trim().toString()
    val comment=binding.etLoanDetails.text.trim().toString()
    if(!mLoanType.equals("Select Type",true))
    {
      if (amt.isNotEmpty()) {
        if (comment.isNotEmpty()) {
          return true
        } else
          Snackbar.make(
            binding.root,
            MyApp.getInstance().resources.getString(R.string.lbl_ent_ln_details),
            Snackbar.LENGTH_LONG
          ).show()
      } else
        Snackbar.make(
          binding.root,
          MyApp.getInstance().resources.getString(R.string.lbl_ent_amt),
          Snackbar.LENGTH_LONG
        ).show()
    }
    else
    Snackbar.make(
      binding.root,
      MyApp.getInstance().resources.getString(R.string.lbl_select_loan),
      Snackbar.LENGTH_LONG
    ).show()

    return false
  }

  fun validateLeaveDetails():Boolean{
    val reason=binding.etReasonForLeave.text.trim().toString()
    val noOfDays=binding.etLeaveDays.text.trim().toString().toDoubleOrNull()?:0.0
    val days =noOfDays * 2
    if((days % 1==0.0) && days > 0.0){
      if(reason.isNotEmpty()){
        return true
      }
      else
        Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_ln_details), Snackbar.LENGTH_LONG).show()
    }
    else
        Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_vald_days), Snackbar.LENGTH_LONG).show()
    return false
  }

  override fun addObservers() {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this@ApplylolActivity) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@ApplylolActivity.showProgressDialog()
      } else  {
        progressDialog?.dismiss()
      }
    }
    viewModel.createRequestLeaveLiveData.observe(this@ApplylolActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateRequestLeave(it)
      } else if(it is ErrorResponse)  {
        onError(it.data ?:Exception())
      }
    }
    viewModel.createRequestLoanLiveData.observe(this@ApplylolActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateRequestLoan(it)
      } else if(it is ErrorResponse)  {
        onError(it.data ?:Exception())
      }
    }

    viewModel.loanListLiveData.observe(this@ApplylolActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        if(!it.data.msgList.isNullOrEmpty())
          viewModel.bindLoanListResponse(it.data)

      } else if(it is ErrorResponse)  {
        onError(it.data ?:Exception())
      }
    }

    viewModel.leaveListLiveData.observe(this@ApplylolActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        if(!it.data.msgList.isNullOrEmpty())
          viewModel.bindLeaveListResponse(it.data)

      } else if(it is ErrorResponse)  {
        onError(it.data ?:Exception())
      }
    }
  }

  private fun onSuccessCreateRequestLeave(response: SuccessResponse<RetroResponse>) {
    Snackbar.make(binding.root, "${response.`data`.message}", Snackbar.LENGTH_LONG).show()
   clearLeaveFeilds()
  }
  

  private fun onSuccessCreateRequestLoan(response: SuccessResponse<RetroResponse>) {
    Snackbar.make(binding.root, "${response.`data`.message}", Snackbar.LENGTH_LONG).show()
    clearLoanFeilds()
  }

  private fun onError(exception: Exception) {
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

  companion object {
    const val TAG: String = "APPLYLOL_ACTIVITY"
    public fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, ApplylolActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun onItemSelected(parent: AdapterView<*>?, child: View?, position: Int, p3: Long) {
    mLeaveType=mLeaveTypesList[position].featureName!!
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {
  }
}
