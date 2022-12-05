package com.pagarplus.app.network.models.createReplyMsg

import com.google.gson.annotations.SerializedName

data class CreateReplyMsgRequest (

    @field:SerializedName("MessageID")
    val messageID: Int? = null,

    @field:SerializedName("FromEmployee")
    val fromEmployee: Int? = null,

    @field:SerializedName("ToEmployee")
    val toEmployee: Int? = null,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("ImagePath")
    val imagePath: String? = null,

)