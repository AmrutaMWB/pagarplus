package com.pagarplus.app.network.models.attendance

import com.google.gson.annotations.SerializedName


data class FeaturesTypes(
    @field:SerializedName("Value")
    val ID: Int? = null,

    @field:SerializedName("Text")
    val featureName: String? = null)


data class RetroResponse(
    @field:SerializedName("Status")
    val status: Boolean? = false,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("List")
    val list: ArrayList<FeaturesTypes>? = null,

    @field:SerializedName("ImagePath")
    val imagePath: String? = null,
)