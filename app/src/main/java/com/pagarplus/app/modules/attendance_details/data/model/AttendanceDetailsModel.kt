package com.pagarplus.app.modules.attendance_details.data.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class AttendanceDetailsModel(

  var txtAttendanceDetails: String? = "Attendance Details",
  var txtCheckinDate: String? = "",
  var txtCheckoutDate: String? = "",
  var txtVisit: String? = "",
  var txtDuration: String? = "",
  var txtType: String? = "",
  var txtStatus: String? = "",
  var txtFromDate: String? = "",
  var txtToDate: String? = "",
  var txtEmpID: Int? = 0,
  var txtEmpName: String? = "",
  var txtcheckinimage: String? = "",
  var txtcheckoutimage: String? = "",
  var txtbranch: String? = "",
  var txtDept: String? = "",
  var txtAttendanceID: Int? = 0,
  var txtadmincomment: String? = "",
  var txtcomment: String? = "",

  var txtcheckinlatitude: String? = "",
  var txtcheckinlongitude: String? = "",
  var txtcheckinLocation: String? = "",
  var txtcheckoutlatitude: String? = "",
  var txtcheckoutlongitude: String? = "",
  var txtcheckoutLocation: String? = "",
  var txtbranchlocation: String? = "",

  var organizationname: String? = "",
)
