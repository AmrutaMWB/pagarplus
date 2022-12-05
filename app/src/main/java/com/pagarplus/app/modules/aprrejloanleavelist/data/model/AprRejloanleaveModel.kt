package com.pagarplus.app.modules.aprrejloanleavelist.data.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class AprRejloanleaveModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtApproval: String? = MyApp.getInstance().resources.getString(R.string.lbl_approval),

  var requestedLoan: Int? = 0,

  var monthlyDeduction: Int? = 0,

  var approvedLoan: Int? = 0,

  var comment: String? = "",

  var requestId: Int? = 0,

  var loanId: Int? = 0,

  var txtfromdate: String? = null,
  var txttodate: String? = null,
  var txtmonth: String? = null,
  var txtyear: String? = null,

  var txtEmpTitle: String? = "Employee",
  var txtloanordateTitle: String? = "",
  var txtStatusTtile: String? = "Status",

  var branchId: Int? = 0,
  var deptId: Int? = 0,

)
