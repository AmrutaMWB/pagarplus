package com.pagarplus.app.network.models.fetchgetdepartmentlist

import com.google.gson.annotations.SerializedName

data class FetchGetDepartmentListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val deptList:ArrayList<FetchGetDepartmentListResponseListItem>? = arrayListOf(),
)

data class FetchGetDepartmentListResponseListItem(

	@field:SerializedName("Value")
	val value: Int? = null,

	@field:SerializedName("Text")
	val text: String? = null
)
