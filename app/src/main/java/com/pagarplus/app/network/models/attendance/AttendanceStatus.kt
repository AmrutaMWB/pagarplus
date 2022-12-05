package com.pagarplus.app.network.models.attendance

import com.google.gson.annotations.SerializedName


data class AttendanceStatusResponse (
    @field:SerializedName("Status")
    val Status: Boolean? = false,

    @field:SerializedName("Message")
    val Message: String? = null,

    @field:SerializedName("LoginData")
    val LoginData: AttendanceStatus? = null
)
data class AttendanceStatus (
     @field:SerializedName("LoginStatus")
     var CheckInStatus: Boolean? = false,

     @field:SerializedName("VisitTypeID")
     var VisitTypeId: Int? = 0,

    @field:SerializedName("SessionTypeID")
    var SessionTypeId: Int? = 0
)