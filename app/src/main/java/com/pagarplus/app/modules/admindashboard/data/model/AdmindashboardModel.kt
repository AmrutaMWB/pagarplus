package com.pagarplus.app.modules.admindashboard.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import java.time.LocalDate
import kotlin.String

data class AdmindashboardModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtHi: String? = ""
  ,

  var txtImgProfUrl: String? = ""
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtAttendanceDate: String? = ""
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTotalEmployees: String? =
      MyApp.getInstance().resources.getString(R.string.msg_total_employees)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txttotEmpVal: Int? = 0
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txttotPresesntVal: Int? = 0
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txttotAbsentVal: Int? = 0
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtPresent: String? = MyApp.getInstance().resources.getString(R.string.lbl_present)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtAbsent: String? = MyApp.getInstance().resources.getString(R.string.lbl_absent)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTwo: String? = MyApp.getInstance().resources.getString(R.string.lbl_02)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtExpense: String? = MyApp.getInstance().resources.getString(R.string.lbl_expense)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtExpenseDate: String? = ""
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTotalExpense: String? = ""
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtLocalExpense: String? = MyApp.getInstance().resources.getString(R.string.lbl_local_expense)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txttotLocExpVal: String? = "0"
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTravelExpense: String? =
      MyApp.getInstance().resources.getString(R.string.msg_travel_expense)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txttotTravExpVal: String? = "0"
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtLodgingandBoa: String? =
      MyApp.getInstance().resources.getString(R.string.msg_lodging_and_boa)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txttotLodBordVal: String? = "0"
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDNSAllowance: String? = MyApp.getInstance().resources.getString(R.string.lbl_dns_allowance)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txttotDnsVal: String? = "0",

  var txtBranchId: Int? = 0,

  var txtBranch: String? = "",

  var branchid: Int? = 0,
)
