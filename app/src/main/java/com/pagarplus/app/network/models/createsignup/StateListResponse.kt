package com.pagarplus.app.network.models.createsignup

import com.google.gson.annotations.SerializedName

data class StateListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val stateList:List<GetStateListItem>? = null,

	@field:SerializedName("data")
	val deptList:List<GetStateListItem>? = null,
)

data class GetStateListItem(

	@field:SerializedName("Value")
	val value: Int? = null,

	@field:SerializedName("Text")
	val text: String? = null

)