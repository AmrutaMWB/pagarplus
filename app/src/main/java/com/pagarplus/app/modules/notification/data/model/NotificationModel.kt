package com.pagarplus.app.modules.notification.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class NotificationModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtNotifications: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_notifications),

  var selDate: String? = "",

  var txtBranchid: Int? = 0,

  var txtDeptid: Int? = 0
)
