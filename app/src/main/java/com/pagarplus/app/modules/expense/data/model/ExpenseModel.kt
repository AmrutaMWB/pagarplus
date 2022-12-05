package com.pagarplus.app.modules.expense.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.createcreateemployee.ProofItem
import com.pagarplus.app.network.models.expense.ImageItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String

data class ExpenseModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtMyExpense: String? = MyApp.getInstance().resources.getString(R.string.lbl_expenses)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtSelectDate: String? = MyApp.getInstance().resources.getString(R.string.lbl_sel_date)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDate: String? = MyApp.getInstance().resources.getString(R.string.lbl_sel_date),
  var txtExpenseDate: String? =SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var txtBoardingFromDate: String? =SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var txtBoardingToDate: String? =SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var txtAmount:String?=null,
  var txtSourceLocation:String?=null,
   var txtDestLocation:String?="",
   var txtComents:String?=null,
   var txtHotelName:String?=null,
   var txtHotelDetails:String?=null,
  val ImageList: ArrayList<ImageItem> = ArrayList()
)
