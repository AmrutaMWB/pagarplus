package com.pagarplus.app.network.models.holiday

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.network.models.attendance.FeaturesTypes

data class HolidayResponse(
    @field:SerializedName("Status")
    val status: Boolean = false,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("Holidays")
    val holidays: ArrayList<HolidayItem>? = null,
)


data class SetHolidayRequest(
    @field:SerializedName("AdminID")
    val adminID: Int? = null,

    @field:SerializedName("DateList")
    val dateList: ArrayList<HolidayItem>? = null,
)
data class HolidayItem(
    @field:SerializedName("Id")
    val id: Int? = null,

    @field:SerializedName("OrgId")
    val orgId: Int? = null,

    @field:SerializedName("AdminID")
    val adminID: Int? = null,

    @field:SerializedName("Date")
    var date: String? = "",

    @field:SerializedName("IsHalfday")
    val isHalfDay: Boolean? = false,

    @field:SerializedName("IsActive")
    val isActive: Boolean? = null,

    @field:SerializedName("HolidayTypeID")
    val holidayTypeID: Int? = null,

    @field:SerializedName("HolidayType")
    val holidayType: String? = null,

    @field:SerializedName("Comment")
    val comment: String? = "",
)