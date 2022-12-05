package com.pagarplus.app.network.models.others

import com.google.gson.annotations.SerializedName


data class ApiResponse(

    @field:SerializedName("Status")
    val status: Boolean = false,

    @field:SerializedName("Message")
    val message: String? = null
)
