package com.pagarplus.app.network.models.createReplyMsg

import com.google.gson.annotations.SerializedName

data class CreateReplyMsgResponse (

        @field:SerializedName("Status")
        val status: Boolean? = null,

        @field:SerializedName("Message")
        val message: String? = null
)