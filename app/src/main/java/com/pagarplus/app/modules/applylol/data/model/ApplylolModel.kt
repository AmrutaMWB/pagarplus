package com.pagarplus.app.modules.applylol.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String

data class ApplylolModel(

  var txtApplyLoan: String? = MyApp.getInstance().resources.getString(R.string.lbl_apply_loan),
  var txtLoanAmount: String? ="",
  var txtLoanDate: String? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var txtLoaDescription: String? = "",

  var txtApplyLeave: String? = MyApp.getInstance().resources.getString(R.string.lbl_apply_leave),
  var txtLeaveFromDate: String? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var txtLeaveFromTime: String? = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(Date()),
  var txtLeaveToDate: String? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var txtLeaveToTime: String? = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(Date()),
  var txtReasonForLeave: String? = "",
  var txtLeaveDays: String? =null,
  var ftFromDate: String? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var ftToDate: String? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  )
