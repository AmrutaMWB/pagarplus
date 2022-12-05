package com.pagarplus.app.network.models.attendance

import com.google.gson.annotations.SerializedName

data class AttedanceApproveRejectRequest(
    @field:SerializedName("AttendanceID")
    var attendanceID: Int? = 0,

    @field:SerializedName("Comment")
    var comment: String? = null,
)

