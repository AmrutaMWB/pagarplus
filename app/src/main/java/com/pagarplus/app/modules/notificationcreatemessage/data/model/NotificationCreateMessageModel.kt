package com.pagarplus.app.modules.notificationcreatemessage.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.createMessage.EmpListItem
import com.pagarplus.app.network.models.createcreateemployee.ProofItem
import kotlin.String

data class NotificationCreateMessageModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtNotifications: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_notifications)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtCreateMessageOne: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_create_message)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDescription: String? = null
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtImagePath: String? = null
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDepartment: String? = "Select Department"
  ,

  var txtBranch: String? = "Select Branch"
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDeptID: Int? = 0
  ,

  var txtBranchID: Int? = 0
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtEmployee: String? = "Select Employee"
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtEmpID: String? = null
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtClickImage: String? = MyApp.getInstance().resources.getString(R.string.lbl_click_image),

  val EmpIdlist: ArrayList<EmpListItem> = ArrayList()
)