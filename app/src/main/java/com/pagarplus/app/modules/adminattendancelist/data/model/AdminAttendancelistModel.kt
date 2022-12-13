package com.pagarplus.app.modules.adminattendancelist.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class AdminAttendancelistModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtAttendance: String? = "Attendance Details"
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDate: String? = ""
  ,
  var txtBranch: String? = ""
  ,
  var txtBranchId: Int? = 0
  ,
  var txtDept: String? = ""
  ,
  var txtDeptId: Int? = 0
  ,

  var txtListType: String? = MyApp.getInstance().resources.getString(R.string.lbl_all)
  ,
)
