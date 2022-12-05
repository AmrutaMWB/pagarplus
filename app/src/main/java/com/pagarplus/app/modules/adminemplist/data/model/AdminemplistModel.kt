package com.pagarplus.app.modules.adminemplist.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class AdminemplistModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtEdit: String? = MyApp.getInstance().resources.getString(R.string.lbl_edit)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtSharelink: String? = MyApp.getInstance().resources.getString(R.string.lbl_share_link)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtView: String? = MyApp.getInstance().resources.getString(R.string.lbl_view)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtAttendance: String? = MyApp.getInstance().resources.getString(R.string.lbl_empname)
  ,

  var txtBranch: String? = ""
  ,
  var txtBranchId: Int? = 0
  ,
  var txtDept: String? = ""
  ,
  var txtDeptId: Int? = 0
  ,
  var comment: String? = "",

  var existDate: String? = "",

  var filetrType: String? = "",

)
