package com.pagarplus.app.modules.adminattendancelist.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class AttendanceRowModel(

  var txtbranch: String? = "",
  var txtDept: String? = "",
  var txtStatus: String? = "",
  var txtempid: Int? = 0,
  var txtCheckinDate: String? = "",
  var txtCheckoutDate: String? = "",
  var txtVisit: String? = "",
  var txtDuration: String? = "",
  var txtType: String? = "",
  var txtFromDate: String? = "",
  var txtToDate: String? = "",
  var txtEmpName: String? = "",
  var txtAttendanceID: Int? = 0,
  var txtadmincomment: String? = "",
  var txtcomment: String? = "",

  var txtcheckinimage: String? = "",
  var txtcheckoutimage: String? = "",
  var txtcheckinlatitude: String? = "",
  var txtcheckinlongitude: String? = "",
  var txtcheckinLocation: String? = "",
  var txtcheckoutlatitude: String? = "",
  var txtcheckoutlongitude: String? = "",
  var txtcheckoutLocation: String? = "",
  var txtbranchlocation: String? = "",
  var txtisLeaveExist: String? = "",
  var txtrerasonLeave: String? = "",
  var txtLeaveStatus: String? = "",

  var txtMobilenumber: String? = "",

  var organizationname: String? = "",
)
