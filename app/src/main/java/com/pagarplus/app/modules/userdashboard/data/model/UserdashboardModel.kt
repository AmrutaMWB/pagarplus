package com.pagarplus.app.modules.userdashboard.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class UserdashboardModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtHi: String? = MyApp.getInstance().resources.getString(R.string.lbl_hi)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtPavan: String? = MyApp.getInstance().resources.getString(R.string.lbl_pavan)

)
