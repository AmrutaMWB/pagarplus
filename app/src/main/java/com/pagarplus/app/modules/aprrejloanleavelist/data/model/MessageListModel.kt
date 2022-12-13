package com.pagarplus.app.modules.aprrejloanleavelist.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class MessageListModel(
  /**
   * TODO Replace with dynamic value
   */
  public var txtEmpID: Int? = 0,
  public var txtRequestID: Int? = 0,
  public var txtLeaveType: String? = "",
  public var txtLoanType: String? = "",

  public var txtEmpName: String? = "",
  public var txtMessage: String? = "",
  public var txtDatetime: String? = "",
  public var txtComment: String? = "",
  public var txtAprvedDate: String? = "",
  public var txtStatus: String? = "",
  public var txtApprove: String? = MyApp.getInstance().resources.getString(R.string.lbl_approval),
  public var txtReject: String? = MyApp.getInstance().resources.getString(R.string.lbl_reject),

  public var txtloanordate: String? = "",
  public var txtAmount: Int? = 0,
  public var txtAprvedAmount: Int? = 0,
  public var txtSalary: Int? = 0,

  public var titlEmpname: String? = MyApp.getInstance().getString(R.string.lbl_empname),
  public var titlLoanDate: String? = "",
  public var titlReason: String? = "Reason : ",
  public var titlComment: String? = "Your Comment : ",
  public var titlReqDate: String? = "Requested Date : ",
  public var titlAprDate: String? = "Approved Date : ",
  public var titlAprAmt: String? = "Approved Amount : ",

  public var txtBranch: String? = "",
  public var txtDept: String? = "",
  public var txtDesignation: String? = "",
  public var txtApprovedLeavetype: String? = "",
  public var organizationname: String = "",
  public var txtMonthlyDeduction: Int? = 0,
  public var txtOldBal: Int? = 0,
)
