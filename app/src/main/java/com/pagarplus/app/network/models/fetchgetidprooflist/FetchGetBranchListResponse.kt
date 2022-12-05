package com.pagarplus.app.network.models.fetchgetidprooflist

import com.google.gson.annotations.SerializedName

data class FetchGetIDProofListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val proofList:ArrayList<FetchGetIDProofListResponseListItem>? = arrayListOf(),
)

data class FetchGetIDProofListResponseListItem(

	@field:SerializedName("Value")
	val value: Int? = null,

	@field:SerializedName("Text")
	val text: String? = null
)
