package com.pagarplus.app.modules.expensereport.data.model


import java.text.SimpleDateFormat
import java.util.*
import kotlin.String

public data class ExpenseReportModel(

  public var txtFromDate: String? = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
  public var txtToDate: String? = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
  public var txtExpenseDate: String? = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
  public var TotalExpense: String? = "",
)
