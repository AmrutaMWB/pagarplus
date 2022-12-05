package com.pagarplus.app.network.models.adminreport

import com.google.gson.annotations.SerializedName


data class AdminReportResponse(

    @field:SerializedName("Status")
    val status: Boolean? = null,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("List")
    val stateList:List<AdminReportItem>? = null,

    @field:SerializedName("data")
    val dataList:List<AdminReportItem>? = null,

    @field:SerializedName("Attendance")
    val AdminSAList:List<AdminSAItem>? = null,
)

data class AdminReportItem(

    @field:SerializedName("Value")
    val value: Int? = null,

    @field:SerializedName("Text")
    val text: String? = null

)