package com.pagarplus.app.modules.userdashboard.data.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class DrawerItemUsermenuModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtPavanKumar: String? = MyApp.getInstance().resources.getString(R.string.lbl_pavan_kumar)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDeveloper: String? = MyApp.getInstance().resources.getString(R.string.lbl_developer)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtMWBTechnologie: String? =
      MyApp.getInstance().resources.getString(R.string.msg_mwb_technologie)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtAttendance: String? = MyApp.getInstance().resources.getString(R.string.lbl_attendance)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtExpenses: String? = MyApp.getInstance().resources.getString(R.string.lbl_expenses)
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
  var txtApplyLeave: String? = MyApp.getInstance().resources.getString(R.string.lbl_apply_leave)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtApplyLoan: String? = MyApp.getInstance().resources.getString(R.string.lbl_apply_loan)
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
