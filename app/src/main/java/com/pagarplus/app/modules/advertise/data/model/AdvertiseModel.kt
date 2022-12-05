package com.pagarplus.app.modules.advertise.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class AdvertiseModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtAdvertise: String? = MyApp.getInstance().resources.getString(R.string.lbl_advertise)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtTypeMessage: String? = MyApp.getInstance().resources.getString(R.string.lbl_type_message)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDescription: String? = null,

  var txtImagePath: String? = null,

  var txtTitle: String? = null,

  var txtfromdate: String? = null,

  var txttodate: String? = null,

  var seldate: String? = "",

  var bannerId: Int? = 0,

  var txtBranchId: Int? = 0,

  var txtDeptId: Int? = 0,

  var txtBranch: String? = "",

  var txtDept: String? = ""

)
