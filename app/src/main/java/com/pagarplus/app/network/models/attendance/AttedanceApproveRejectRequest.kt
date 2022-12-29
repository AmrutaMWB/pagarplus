package com.pagarplus.app.network.models.attendance

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.network.models.createMessage.EmpListItem

data class AttedanceApproveRejectRequest(
    @field:SerializedName("AttendanceID")
    var attendanceID: Int? = 0,

    @field:SerializedName("Status")
    var status: String? = "",

    @field:SerializedName("Date")
    var date: String? = "",

    @field:SerializedName("Comment")
    var comment: String? = "",

    @field:SerializedName("Employees")
    val empList: ArrayList<EmpIdItem>? = null,
)

data class EmpIdItem(
    @field:SerializedName("EmployeeID")
    val employeeID: Int? = 0
)