package com.pagarplus.app.modules.adminreport.data.model


import com.pagarplus.app.network.models.adminreport.Employee
import com.pagarplus.app.network.models.createMessage.EmpListItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String

public data class AdminReportModel(

  public var txtFromDate: String? = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
  public var txtToDate: String? = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
  public var OfficialWorkingDays: String? = "",
  val EmpIdlist: ArrayList<Employee> = arrayListOf()
)
