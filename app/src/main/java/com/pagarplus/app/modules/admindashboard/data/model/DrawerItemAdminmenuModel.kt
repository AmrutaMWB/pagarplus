package com.pagarplus.app.modules.admindashboard.data.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class DrawerItemAdminmenuModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtPrajwal: String? = MyApp.getInstance().resources.getString(R.string.lbl_prajwal)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtMWBTechnologie: String? =""
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtCreateEmpl: String? = MyApp.getInstance().resources.getString(R.string.lbl_create_employee)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtAdvertisment: String? = MyApp.getInstance().resources.getString(R.string.lbl_advertise)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtReports: String? = MyApp.getInstance().resources.getString(R.string.lbl_reports)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtNotifications: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_notifications)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtApproveLeave: String? = MyApp.getInstance().resources.getString(R.string.lbl_approve_leave)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtApproveLoan: String? = MyApp.getInstance().resources.getString(R.string.lbl_approve_loan)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtEditProfile: String? = MyApp.getInstance().resources.getString(R.string.lbl_edit_profile)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtLogout: String? = MyApp.getInstance().resources.getString(R.string.lbl_logout)

)
