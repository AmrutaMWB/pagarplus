package com.pagarplus.app.modules.applylol.data.model

 data class LeaveModel(
     var RequestID: Int? =0,
     var EmpID: Int? =0,
     var AdminID: Int? =0,
     var LeaveType: String? ="",
     var EmployeeName: String? ="",
     var ReasonForLeave: String?="",
     var AdminComment: String?="",
     var ApproveStatus: String?="",
     var FromDateTime: String?="",
     var ToDateTime: String?="",
     var RequestDate: String?="",
     var ApprovedDate: String?="",
 )

