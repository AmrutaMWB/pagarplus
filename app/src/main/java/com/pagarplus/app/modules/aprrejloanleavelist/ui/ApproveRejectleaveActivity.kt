package com.pagarplus.app.modules.aprrejloanleavelist.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.AprRejPenLevlonlistBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.aprrejloanleavelist.data.model.MessageListModel
import com.pagarplus.app.modules.aprrejloanleavelist.data.viewmodel.AprRejloanleaveVM
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetleaveListResponse
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetloanListResponse
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class ApproveRejectleaveActivity :
    BaseActivity<AprRejPenLevlonlistBinding>(R.layout.apr_rej_pen_levlonlist), BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: AprRejloanleaveVM by viewModels<AprRejloanleaveVM>()
  var isApproveorReject: Boolean = true
  var isLeaveOrLoan: Boolean = true
  lateinit var listAdapter : LeaveListAdapter
  lateinit var alertdialog1: AlertDialog
  var mLeaveTypesList= arrayListOf<FeaturesTypes>()
  var currentDate: String? = ""
  lateinit var leaveTypeAdapter: ArrayAdapter<Any>

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    isLeaveOrLoan = intent.getBooleanExtra(IntentParameters.IsLeaveOrLoan,true)

    currentDate = getCurrentDate()

    viewModel.aprrejloanleaveModel.value?.txtfromdate = currentDate
    viewModel.aprrejloanleaveModel.value?.txttodate = currentDate

    if(isLeaveOrLoan) {
      viewModel.aprrejloanleaveModel.value?.txtloanordateTitle = "Date"
      binding.txtLeaveLoanTitle.setText(R.string.lbl_reqleavetitle)
      viewModel.callGetAllApprovedLeaveListApi()
      CallApprovedList()
    }else{
      viewModel.aprrejloanleaveModel.value?.txtloanordateTitle = "Amount"
      binding.txtLeaveLoanTitle.setText(R.string.lbl_reqloantitle)
      viewModel.callGetAllApprovedLoanListApi()
      CallApprovedList()
    }

    if(viewModel.prfoileDetails?.showBranch == false){
      binding.txtAllBranch.isEnabled = false
      binding.txtAllBranch.setTextAppearance(R.style.disabledButton)
    }else{
      binding.txtAllBranch.isEnabled = true
    }

    if(viewModel.prfoileDetails?.showDepartment == false){
      binding.txtAllDepartment.isEnabled = false
      binding.txtAllDepartment.setTextAppearance(R.style.disabledButton)
    }else{
      binding.txtAllDepartment.isEnabled = true
    }
  }

  fun CallApprovedList(){
    listAdapter = LeaveListAdapter(viewModel.messageList.value ?: mutableListOf())
    binding.recyclerLevLonList.adapter = listAdapter
    listAdapter.setOnItemClickListener(
      object : LeaveListAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, item: MessageListModel) {
          onClickRecyclerMessage(view, position, item)
        }
      }
    )
    viewModel.messageList.observe(this) {
      listAdapter.updateData(it)
    }
    binding.aprrejloanleaveVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.imageBack.setOnClickListener{
      val intent = AdmindashboardActivity.getIntent(this, null)
      startActivity(intent)
    }

    binding.searchViewEmplist.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(s: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(s: String?): Boolean {
        if (listAdapter != null) {
          if (s != null) {
            filter(s)
          }
        }
        return false
      }
    })

    binding.fromDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.fromDate.text = selected
        if(isLeaveOrLoan) {
          viewModel.callGetAllApprovedLeaveListApi()
          CallApprovedList()
        }else{
          viewModel.callGetAllApprovedLoanListApi()
          CallApprovedList()
        }
      }
    }

    binding.toDate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.toDate.text = selected
        if(isLeaveOrLoan) {
          viewModel.callGetAllApprovedLeaveListApi()
          CallApprovedList()
        }else{
          viewModel.callGetAllApprovedLoanListApi()
          CallApprovedList()
        }
      }
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
    var filteredlist: ArrayList<MessageListModel> = ArrayList()

    // running a for loop to compare elements.
    for (item in viewModel.messageList.value!!) {
      // checking if the entered string matched with any item of our recycler view.
      if (item.txtEmpName?.lowercase()?.contains(text.lowercase())!! ||
        item.txtloanordate?.lowercase()?.contains(text.lowercase())!! ||
        item.txtStatus?.lowercase()?.contains(text.lowercase())!!) {
        // if the item is matched we are
        // adding it to our filtered list.
        filteredlist.add(item)
      }
    }
    if (filteredlist.isEmpty()) {
      filteredlist = listAdapter.list as ArrayList<MessageListModel>
      // if no item is added in filtered list we are
      // displaying a toast message as no data found.
      Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
    } else {
      // at last we are passing that filtered
      // list to our adapter class.
      listAdapter.filterList(filteredlist)
    }
  }

  /*popup dialog*/
  fun detailsDialog(pos:Int): Boolean {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()

    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.view_loanleave_details_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(true)

    val btnreject = dialogView.findViewById<Button>(R.id.btnReject)
    val btnapprove = dialogView.findViewById<Button>(R.id.btnApprove)
    val btnedit = dialogView.findViewById<Button>(R.id.btnEdit)

    val txttitle = dialogView.findViewById<TextView>(R.id.tv_label)
    val txtloanordate = dialogView.findViewById<TextView>(R.id.txtloanordate)
    val EmpNameText = dialogView.findViewById<TextView>(R.id.EmpNameText)
    val loanordateTxt = dialogView.findViewById<TextView>(R.id.loanordateTxt)
    val ReasonTxt = dialogView.findViewById<TextView>(R.id.ReasonTxt)
    val ReqdateTxt = dialogView.findViewById<TextView>(R.id.ReqdateTxt)
    val CommentTxt = dialogView.findViewById<TextView>(R.id.CommentTxt)
    val AprDateTxt = dialogView.findViewById<TextView>(R.id.AprDateTxt)
    val AprAmtTxt = dialogView.findViewById<TextView>(R.id.AprAmtTxt)
    val Empsalary = dialogView.findViewById<TextView>(R.id.txtSalary)
    val txtbranch = dialogView.findViewById<TextView>(R.id.txtbranchname)
    val txtdept = dialogView.findViewById<TextView>(R.id.txtDepartmentName)
    val txtMonDed = dialogView.findViewById<TextView>(R.id.Monthlydeduction)
    val txtDesignation = dialogView.findViewById<TextView>(R.id.txtDesignationName)
    val txtOldbal = dialogView.findViewById<TextView>(R.id.Oldbal)
    val txtleavetype = dialogView.findViewById<TextView>(R.id.LeaveTypeTxt)

    val Edtcomment = dialogView.findViewById<EditText>(R.id.etComments)
    val loanapramt = dialogView.findViewById<LinearLayout>(R.id.linearAprAmt)
    val loanMonDeduction = dialogView.findViewById<LinearLayout>(R.id.linearMonthlyDeduction)
    val loanoldBal = dialogView.findViewById<LinearLayout>(R.id.linearOldBal)
    val linAdmincomment = dialogView.findViewById<LinearLayout>(R.id.linearComment)
    val linAprveddate = dialogView.findViewById<LinearLayout>(R.id.linearAprdate)
    val linLeaveType = dialogView.findViewById<LinearLayout>(R.id.linearLeavetype)
    val spnLeaveType = dialogView.findViewById<Spinner>(R.id.LeaveTypeSpn)

    alertdialog1 = dialogBuilder.create()
    alertdialog1.show()

    leaveTypeAdapter = ArrayAdapter(this, R.layout.spinner_item,R.id.txtTitle,mLeaveTypesList.map { it.featureName })
    spnLeaveType.adapter = leaveTypeAdapter
    spnLeaveType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
        viewModel.aprrejloanleaveModel.value?.approvedleaveType = parentView?.getItemAtPosition(position).toString()
      }
      override fun onNothingSelected(parentView: AdapterView<*>?) {
        // your code here
      }
    }

    if(listAdapter.list.size > 0){
      Empsalary.setText("Salary : "+listAdapter.list.get(pos).txtSalary.toString())
      EmpNameText.setText(listAdapter.list.get(pos).txtEmpName)
      loanordateTxt.setText(listAdapter.list.get(pos).txtloanordate)
      ReasonTxt.setText(listAdapter.list.get(pos).txtMessage)
      ReqdateTxt.setText(listAdapter.list.get(pos).txtDatetime)
      CommentTxt.setText(listAdapter.list.get(pos).txtComment)
      AprDateTxt.setText(listAdapter.list.get(pos).txtAprvedDate)
      if(listAdapter.list.get(pos).txtAprvedAmount.toString().equals("null")) {
        AprAmtTxt.setText("0")
      }else{
        AprAmtTxt.setText(listAdapter.list.get(pos).txtAprvedAmount.toString())
      }
      if(listAdapter.list.get(pos).txtMonthlyDeduction.toString().equals("null")) {
        txtMonDed.setText("0")
      }else{
        txtMonDed.setText(listAdapter.list.get(pos).txtMonthlyDeduction.toString())
      }

      txtbranch.setText(listAdapter.list.get(pos).txtBranch)
      txtdept.setText(listAdapter.list.get(pos).txtDept)
      txtOldbal.setText(listAdapter.list.get(pos).txtOldBal.toString())
      txtDesignation.setText(listAdapter.list.get(pos).txtDesignation.toString())
      txtleavetype.setText(listAdapter.list.get(pos).txtLeaveType.toString())

      spnLeaveType.setSelection(mLeaveTypesList.indexOf(mLeaveTypesList.find { it.featureName==listAdapter.list.get(pos).txtLeaveType?:0}))

      viewModel.aprrejloanleaveModel.value?.requestId = listAdapter.list.get(pos).txtRequestID
      if(listAdapter.list.get(pos).txtStatus.equals("Approved")){
        btnedit.isVisible = true
        Edtcomment.isVisible = false
        btnapprove.isVisible = false
        btnreject.isVisible = false
        linAdmincomment.isVisible = true
        linAprveddate.isVisible = true
        spnLeaveType.isVisible = false
        loanapramt.isVisible = true
      }else if(listAdapter.list.get(pos).txtStatus.equals("Rejected")){
        btnedit.isVisible = true
        Edtcomment.isVisible = false
        btnapprove.isVisible = false
        btnreject.isVisible = false
        linAdmincomment.isVisible = true
        linAprveddate.isVisible = true
        spnLeaveType.isVisible = false
        loanapramt.isVisible = true
      }else{
        btnedit.isVisible = false
        Edtcomment.isVisible = true
        btnapprove.isVisible = true
        btnreject.isVisible = true
        linAdmincomment.isVisible = false
        linAprveddate.isVisible = false
        spnLeaveType.isVisible = true
        loanapramt.isVisible = false
      }
    }else{
      Empsalary.setText("Salary : "+viewModel.messageList.value?.get(pos)?.txtSalary.toString())
      EmpNameText.setText(viewModel.messageList.value?.get(pos)?.txtEmpName)
      loanordateTxt.setText(viewModel.messageList.value?.get(pos)?.txtloanordate)
      ReasonTxt.setText(viewModel.messageList.value?.get(pos)?.txtMessage)
      ReqdateTxt.setText(viewModel.messageList.value?.get(pos)?.txtDatetime)
      CommentTxt.setText(viewModel.messageList.value?.get(pos)?.txtComment)
      AprDateTxt.setText(viewModel.messageList.value?.get(pos)?.txtAprvedDate)
      txtbranch.setText(viewModel.messageList.value?.get(pos)?.txtBranch)
      txtdept.setText(viewModel.messageList.value?.get(pos)?.txtDept)
      txtOldbal.setText(viewModel.messageList.value?.get(pos)?.txtOldBal.toString())
      txtDesignation.setText(viewModel.messageList.value?.get(pos)?.txtDesignation.toString())
      txtleavetype.setText(viewModel.messageList.value?.get(pos)?.txtLeaveType.toString())

      if(viewModel.messageList.value?.get(pos)?.txtAprvedAmount.toString().equals("null")) {
        AprAmtTxt.setText("0")
      }else{
        AprAmtTxt.setText(viewModel.messageList.value?.get(pos)?.txtAprvedAmount.toString())
      }
      if(viewModel.messageList.value?.get(pos)?.txtMonthlyDeduction.toString().equals("null")) {
        txtMonDed.setText("0")
      }else{
        txtMonDed.setText(viewModel.messageList.value?.get(pos)?.txtMonthlyDeduction.toString())
      }

      listAdapter.list.get(pos).txtLeaveType

      spnLeaveType.setSelection(mLeaveTypesList.indexOf(mLeaveTypesList.find { it.featureName==viewModel.messageList.value?.get(pos)?.txtLeaveType?:0}))

      viewModel.aprrejloanleaveModel.value?.requestId = viewModel.messageList.value?.get(pos)?.txtRequestID
      if(viewModel.messageList.value?.get(pos)?.txtStatus.equals("Approved")){
        btnedit.isVisible = true
        Edtcomment.isVisible = false
        btnapprove.isVisible = false
        btnreject.isVisible = false
        spnLeaveType.isVisible = false
        linAdmincomment.isVisible = true
        linAprveddate.isVisible = true
        loanapramt.isVisible = true
      }else if(viewModel.messageList.value?.get(pos)?.txtStatus.equals("Rejected")){
        btnedit.isVisible = true
        Edtcomment.isVisible = false
        btnapprove.isVisible = false
        btnreject.isVisible = false
        spnLeaveType.isVisible = false
        linAdmincomment.isVisible = true
        linAprveddate.isVisible = true
        loanapramt.isVisible = true
      }else{
        btnedit.isVisible = false
        Edtcomment.isVisible = true
        btnapprove.isVisible = true
        btnreject.isVisible = true
        spnLeaveType.isVisible = true
        linAdmincomment.isVisible = false
        linAprveddate.isVisible = false
        loanapramt.isVisible = false
      }
    }

    if(isLeaveOrLoan) {
      txttitle.setText("Leave Details")
      txtloanordate.setText("Leave Date: ")
      Empsalary.isVisible = false
      loanapramt.isVisible = false
      loanMonDeduction.isVisible = false
      loanoldBal.isVisible = false
      linLeaveType.isVisible = true
    }else{
      txttitle.setText("Loan Details")
      txtloanordate.setText("Requested Amount: ")
      Empsalary.isVisible = true
      loanapramt.isVisible = true
      loanMonDeduction.isVisible = true
      loanoldBal.isVisible = true
      linLeaveType.isVisible = false
      Edtcomment.isVisible = false
      spnLeaveType.isVisible = false
    }

    btnedit.setOnClickListener {
      if(isLeaveOrLoan) {
        btnedit.isVisible = false
        Edtcomment.isVisible = true
        btnapprove.isVisible = true
        btnreject.isVisible = true
        spnLeaveType.isVisible = true
      }else{
        btnedit.isVisible = false
        Edtcomment.isVisible = false
        btnapprove.isVisible = true
        btnreject.isVisible = true
        spnLeaveType.isVisible = false
      }
    }

    btnreject.setOnClickListener {
      if(isLeaveOrLoan) {
        if (Edtcomment.getText().toString().isNotEmpty()) {
          viewModel.aprrejloanleaveModel.value?.comment = Edtcomment.getText().toString()
          viewModel.callReejctLeaveApi()
        } else {
          //Edtcomment.setBackgroundResource(R.drawable.rectangle_error_border)
          Toast.makeText(this, "Please write your comment", Toast.LENGTH_SHORT).show()
        }
      }else{
        approveLoanDialog(pos,"reject")
      }
    }

    btnapprove.setOnClickListener {
      viewModel.aprrejloanleaveModel.value?.comment = Edtcomment.getText().toString()
      if(isLeaveOrLoan) {
        if(Edtcomment.getText().toString().isNotEmpty()) {
          viewModel.aprrejloanleaveModel.value?.comment = Edtcomment.getText().toString()
          viewModel.callApproveLeaveApi()
        }else{
          //Edtcomment.setBackgroundResource(R.drawable.rectangle_error_border)
          Toast.makeText(this,"Please write your comment",Toast.LENGTH_SHORT).show()
        }
      }else{
        approveLoanDialog(pos,"approve")
      }
    }
    return true
  }

  /*onclick approve open approve dialog*/
  fun approveLoanDialog(pos:Int, paramVal: String): Boolean {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()

    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.add_balance_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatImageView>(R.id.iv_cross)
    val EdtTxtLoanAdvance = dialogView.findViewById<EditText>(R.id.EdtTxtAdvance)
    val EdtTxtMonthlydeduction = dialogView.findViewById<EditText>(R.id.EdtTxtMonthlydeduction)
    val EdtTxtOldSalBalance = dialogView.findViewById<EditText>(R.id.EdtTxtoldSalBalance)
    val EdtTxtComment = dialogView.findViewById<EditText>(R.id.EdtTxtComment)
    val btn_save = dialogView.findViewById<AppCompatButton>(R.id.btnSave)
    val txtLoan = dialogView.findViewById<TextView>(R.id.txtLoan)
    val txtApproved = dialogView.findViewById<TextView>(R.id.txtBalance)
    val dialogTitle = dialogView.findViewById<TextView>(R.id.tv_label)
    val txtsalary = dialogView.findViewById<TextView>(R.id.txtSalary)
    val linAdvance = dialogView.findViewById<LinearLayout>(R.id.linearAdvance)
    val linapproved = dialogView.findViewById<LinearLayout>(R.id.linearBalance)
    val linLoan = dialogView.findViewById<LinearLayout>(R.id.linearLoan)
    btn_save.setText("Submit")

    val alertDialog = dialogBuilder.create()
    alertDialog.show();

    txtLoan.setText("Requested Amount Rs.")
    txtApproved.setText("Approved Loan Rs.")

    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }

    if(paramVal.equals("reject")){
      dialogTitle.setText("Reject Loan")
      linAdvance.isVisible = false
      linapproved.isVisible = false
      linLoan.isVisible = false
      txtsalary.isVisible = false
    }else{
      dialogTitle.setText("Approve Loan")
      linAdvance.isVisible = true
      linapproved.isVisible = true
      linLoan.isVisible = true
      txtsalary.isVisible = true
    }

    if(listAdapter.list.size > 0){
      txtsalary.setText("Employee Salary : "+listAdapter.list.get(pos).txtSalary.toString())
      viewModel.aprrejloanleaveModel.value?.requestId = listAdapter.list.get(pos).txtRequestID
      EdtTxtLoanAdvance.setText(listAdapter.list.get(pos).txtloanordate)
      if(listAdapter.list.get(pos).txtMonthlyDeduction.toString().equals("null")) {
        EdtTxtMonthlydeduction.setText("0")
      }else{
        EdtTxtMonthlydeduction.setText(listAdapter.list.get(pos).txtMonthlyDeduction.toString())
      }
      EdtTxtOldSalBalance.setText(listAdapter.list.get(pos).txtloanordate)

      if(listAdapter.list.get(pos).txtLoanType.equals("Loan")){
        linLoan.isVisible = true
      }else{
        linLoan.isVisible = false
      }
    }else{
      txtsalary.setText("Employee Salary : "+viewModel.messageList.value?.get(pos)?.txtSalary.toString())
      viewModel.aprrejloanleaveModel.value?.requestId = viewModel.messageList.value?.get(pos)?.txtRequestID
      EdtTxtLoanAdvance.setText(viewModel.messageList.value?.get(pos)?.txtloanordate)
      EdtTxtMonthlydeduction.setText(viewModel.messageList.value?.get(pos)?.txtMonthlyDeduction!!)
      EdtTxtOldSalBalance.setText(viewModel.messageList.value?.get(pos)?.txtloanordate)

      if(viewModel.messageList.value?.get(pos)?.txtMonthlyDeduction.toString().equals("null")) {
        EdtTxtMonthlydeduction.setText("0")
      }else{
        EdtTxtMonthlydeduction.setText(viewModel.messageList.value?.get(pos)?.txtMonthlyDeduction.toString())
      }

      if(viewModel.messageList.value?.get(pos)?.txtLoanType.equals("Loan")){
        linLoan.isVisible = true
      }else{
        linLoan.isVisible = false
      }
    }

    btn_save.setOnClickListener{
      if(paramVal.equals("approve")) {
        if (EdtTxtLoanAdvance.getText().toString().isNotEmpty() &&
          EdtTxtMonthlydeduction.getText().toString().isNotEmpty() &&
          EdtTxtOldSalBalance.getText().toString().isNotEmpty() &&
          EdtTxtComment.getText().toString().isNotEmpty()) {

          var reqLoan = Integer.parseInt(EdtTxtLoanAdvance.getText().toString())
          var monDeduction = Integer.parseInt(EdtTxtMonthlydeduction.getText().toString())
          var approedAmt = Integer.parseInt(EdtTxtOldSalBalance.getText().toString()) ?: 0

          viewModel.aprrejloanleaveModel.value?.requestedLoan = reqLoan
          viewModel.aprrejloanleaveModel.value?.monthlyDeduction = monDeduction
          viewModel.aprrejloanleaveModel.value?.approvedLoan = approedAmt
          viewModel.aprrejloanleaveModel.value?.comment = EdtTxtComment.getText().toString()
          viewModel.callApproveLoanApi()
          alertDialog.dismiss()
        } else {
          Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
        }
      }else{
        if (EdtTxtComment.getText().toString().isNotEmpty()) {
          viewModel.aprrejloanleaveModel.value?.comment = EdtTxtComment.getText().toString()
          viewModel.callReejctLoanApi()
          alertDialog.dismiss()
        } else {
          Toast.makeText(this, "Please write your comment", Toast.LENGTH_LONG).show()
        }
      }
    }
    return true
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

    /*fetch leave request*/
    viewModel.fetchallapproveLeaveLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessRequestLeave(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    /*fetch loan request*/
    viewModel.fetchallapproveLoanLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessRequestLoan(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    /*approve loan resposne*/
    viewModel.approveLeaveLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessApproveLeave(it)
        alertdialog1.dismiss()
        binding.animationSparkle.isVisible = true
        binding.animationSparkle.setAnimation(R.raw.sparkles)
        viewModel.callGetAllApprovedLeaveListApi()
      } else if(it is ErrorResponse) {
        binding.animationSparkle.isVisible = false
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    /*Reject loan resposne*/
    viewModel.rejectLeaveLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessRejectLeave(it)
        alertdialog1.dismiss()
        binding.animationSparkle.isVisible = true
        binding.animationSparkle.setAnimation(R.raw.sadface)
        viewModel.callGetAllApprovedLeaveListApi()
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    /*approve loan resposne*/
    viewModel.approveLoanLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessApproveLoan(it)
        alertdialog1.dismiss()
        binding.animationSparkle.isVisible = true
        binding.animationSparkle.setAnimation(R.raw.sparkles)
        viewModel.callGetAllApprovedLoanListApi()
      } else if(it is ErrorResponse) {
        binding.animationSparkle.isVisible = false
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    /*Reject loan resposne*/
    viewModel.rejectLoanLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessRejectLoan(it)
        alertdialog1.dismiss()
        binding.animationSparkle.isVisible = true
        binding.animationSparkle.setAnimation(R.raw.sadface)
        viewModel.callGetAllApprovedLoanListApi()
      } else if(it is ErrorResponse) {
        binding.animationSparkle.isVisible = false
        onErrorFetchMsg(it.data ?:Exception())
      }
    }
  }

  private fun onSuccessRequestLeave(response: SuccessResponse<FetchGetleaveListResponse>): Unit {
    viewModel.bindFetchGetleaveListResponse(response.data)
    if(viewModel.messageList.value?.size!! > 0) {
      binding.recyclerLevLonList.isVisible = true
      binding.linearNoMsg.isVisible = false
    }else{
      binding.recyclerLevLonList.isVisible = false
      binding.linearNoMsg.isVisible = true
    }
    lifecycleScope.launch {
      mLeaveTypesList = ApiUtil(applicationContext).getFeatureTypes(URLParameters.Leave)
      Log.e("LeaveTypeList", "$mLeaveTypesList")
    }
  }

  private fun onSuccessRequestLoan(response: SuccessResponse<FetchGetloanListResponse>): Unit {
    viewModel.bindFetchGetloanListResponse(response.data)
    if(viewModel.messageList.value?.size!! > 0) {
      binding.recyclerLevLonList.isVisible = true
      binding.linearNoMsg.isVisible = false
    }else{
      binding.recyclerLevLonList.isVisible = false
      binding.linearNoMsg.isVisible = true
    }
  }

  /*bind after loan and leave approval or reject*/
  private fun onSuccessApproveLeave(response: SuccessResponse<FetchGetleaveListResponse>): Unit {
    viewModel.bindApproveLeaveResponse(response.data)
  }

  private fun onSuccessApproveLoan(response: SuccessResponse<FetchGetloanListResponse>): Unit {
    viewModel.bindApproveLoanResponse(response.data)
  }

  private fun onSuccessRejectLeave(response: SuccessResponse<FetchGetleaveListResponse>): Unit {
    viewModel.bindRejectLeaveResponse(response.data)
  }

  private fun onSuccessRejectLoan(response: SuccessResponse<FetchGetloanListResponse>): Unit {
    viewModel.bindRejecteLoanResponse(response.data)
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
    item: MessageListModel
  ): Unit {
    when(view.id) {
      R.id.linearlistapprovedrejected ->  {
        //creating a popup menu
        if(listAdapter.list.size > 0){
          detailsDialog(position)
        }else{
          detailsDialog(position)
        }
      }
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("Branch","$selectedItem")
    if(selectedItem.isBranch==true){
      binding.txtAllBranch.setText(selectedItem.txtName)
      viewModel.aprrejloanleaveModel.value?.branchId = selectedItem.txtValue
      if(isLeaveOrLoan) {
        viewModel.callGetAllApprovedLeaveListApi()
      }else{
        viewModel.callGetAllApprovedLoanListApi()
      }
    }else{
      binding.txtAllDepartment.setText(selectedItem.txtName)
      viewModel.aprrejloanleaveModel.value?.deptId = selectedItem.txtValue
      if(isLeaveOrLoan) {
        viewModel.callGetAllApprovedLeaveListApi()
      }else{
        viewModel.callGetAllApprovedLoanListApi()
      }
    }
  }

  companion object {
    const val TAG: String = "APPLYLOANLEAVE_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, ApproveRejectleaveActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
