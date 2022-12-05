package com.pagarplus.app.modules.adminemplist.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class DetailsRowModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtSlNo: String? = "1"
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtName: String? = ""
  ,
  var txtPhone: String? = ""
  ,
  var txtUsername: String? = ""
  ,
  var txtPassword: String? = ""
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDesignation: String? = ""
  ,
  /**
   * TODO Replace with dynamic value
   */
  var imgPath: String? = ""
  ,

  var txtEmpID: Int? = 0
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtEditOne: String? = MyApp.getInstance().resources.getString(R.string.lbl_edit)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtShareOne: String? = MyApp.getInstance().resources.getString(R.string.lbl_share)
  ,

  var txtBranch: String? = "",

  var txtDepartment: String? = "",

  var organizationname: String? = "",

  var status: Boolean? = false,
)
