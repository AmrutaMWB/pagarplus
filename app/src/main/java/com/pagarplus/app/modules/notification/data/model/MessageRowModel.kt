package com.pagarplus.app.modules.notification.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class MessageRowModel(
  /**
   * TODO Replace with dynamic value
   */
  public var txtEmpName: String? = "",
  public var txtMessage: String? = "",
  public var txtDatetime: String? = "",
  public var txtImgpath: String? = "",
  public var txtMainmsgID: Int? = 0,
  public var txtFromempID: Int? = 0,
  public var txtReply: String? =  MyApp.getInstance().resources.getString(R.string.lbl_reply),
  public var txtDelete: String? =  MyApp.getInstance().resources.getString(R.string.lbl_delete_employee),
  public var txtBranch: String? = "",
  public var txtDept: String? = "",
  public var ProfileimgUrl: String? = "",
  public var organizationName: String? = "",

  )
