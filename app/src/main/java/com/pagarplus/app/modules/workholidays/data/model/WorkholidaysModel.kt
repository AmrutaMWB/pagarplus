package com.pagarplus.app.modules.workholidays.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.holiday.HolidayItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String
import kotlin.collections.ArrayList

data class WorkholidaysModel(
  var txtHeader: String? = MyApp.getInstance().resources.getString(R.string.lbl_work_days),
  var txtWeekOff:String?=null,
  var txtSelectedHolidayDate:String? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
  var Holidays:ArrayList<HolidayItem>?= arrayListOf()
)
