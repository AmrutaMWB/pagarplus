package com.pagarplus.app.modules.advertise.data.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class AdvertiseRowModel(
  /**
   * TODO Replace with dynamic value
   */
  public var txtMessage: String? = "",
  public var txtTitle: String? = "",
  public var txtFromDate: String? = "",
  public var txtToDate: String? = "",
  public var txtImgpath: String? = "",
  public var txtAdminID: Int? = 0,
  public var txtBannerID: Int? = 0,
  public var txtBranchId: Int? = 0,
  public var txtDeptID: Int? = 0,
  public var txtBranch: String? = "",
  public var txtDept: String? = "",
  public var txtActivestatus: Boolean? = false,
  public var organizationname: String? = ""

)
