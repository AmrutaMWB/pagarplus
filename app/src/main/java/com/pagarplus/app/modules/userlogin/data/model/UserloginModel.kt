package com.pagarplus.app.modules.userlogin.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class UserloginModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtSIGNIN: String? = MyApp.getInstance().resources.getString(R.string.lbl_sign_in)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtUserName: String? = MyApp.getInstance().resources.getString(R.string.lbl_user_name)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtPassword: String? = MyApp.getInstance().resources.getString(R.string.lbl_password)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtForgotPassword: String? =
      MyApp.getInstance().resources.getString(R.string.msg_forgot_password)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtOR: String? = MyApp.getInstance().resources.getString(R.string.lbl_or)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtSignInWith: String? = MyApp.getInstance().resources.getString(R.string.lbl_sign_in_with)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etUseridValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etUserpasswordValue: String? = null
)
