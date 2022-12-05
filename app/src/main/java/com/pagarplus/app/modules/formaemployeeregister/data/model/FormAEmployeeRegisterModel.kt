package com.finalpagarreportscreen.app.modules.formaemployeeregister.`data`.model


import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.employeeReports.EmployeeItem
import com.pagarplus.app.network.models.employeeReports.MonthItem
import kotlin.String

data class FormAEmployeeRegisterModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtFORMAEmployee: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_msg_form_a_employee2),

  val EmpIdlist: ArrayList<EmployeeItem> = ArrayList(),

  val MonthIdlist: ArrayList<MonthItem> = ArrayList(),
  /**
   *  properties of report PDF generate
   */
  var adminBranchID: Int? =null,

  var adminDepartmentID: Int? =null,

  var fromYear: String? =null,

  var toYear: String? =null,

  var adminyear: String? =null,

  var adminEmployeeID: Int? =null,

  var adminID: Int? =null


  )
