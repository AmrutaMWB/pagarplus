package com.pagarplus.app.network.models.userdashboard

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest

class ProfileResponse (
    @field:SerializedName("Status")
    val status: Boolean? = false,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("List")
    val list: ArrayList<CreateCreateEmployeeRequest>? = null,

)

data class BannerResponse (
    @field:SerializedName("Status")
    val status: Boolean? = false,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("List")
    val list: ArrayList<UserBannerResponseItem>? = null,

    )