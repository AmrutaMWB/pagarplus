package com.pagarplus.app.network.models.createMessage

import com.google.gson.annotations.SerializedName

data class CreateMsgResponse (
        @field:SerializedName("Status")
        val status: Boolean? = null,

        @field:SerializedName("Message")
        val message: String? = null
)