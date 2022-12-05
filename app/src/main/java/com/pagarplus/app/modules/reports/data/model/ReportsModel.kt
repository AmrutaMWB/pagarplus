package com.pagarplus.app.modules.reports.`data`.model

import com.pagarplus.app.R

import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class ReportsModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtReports: String? = MyApp.getInstance().resources.getString(R.string.lbl_reports)

)
