package com.pagarplus.app.network.models.fetchMsgHistory

import com.google.gson.annotations.SerializedName

data class FetchMsgHistoryResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val msgList:List<FetchGetReplyMsgListResponseListItem?>? = null,
)

data class FetchGetReplyMsgListResponseListItem(

	@field:SerializedName("ImagePath")
	val imagePath: String? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("FromEmployee")
	val fromEmployee: String? = null,

	@field:SerializedName("ReceivedDate")
	val receivedDate: String? = null,

)
