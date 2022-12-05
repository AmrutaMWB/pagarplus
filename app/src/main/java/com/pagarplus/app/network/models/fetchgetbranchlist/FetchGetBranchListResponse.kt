package com.pagarplus.app.network.models.fetchgetbranchlist

import com.google.gson.annotations.SerializedName

data class FetchGetBranchListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val branchList:ArrayList<FetchGetBranchListResponseListItem>? = arrayListOf(),
)

data class FetchGetBranchListResponseListItem(

	@field:SerializedName("Value")
	val value: Int? = null,

	@field:SerializedName("Text")
	val text: String? = null
)
