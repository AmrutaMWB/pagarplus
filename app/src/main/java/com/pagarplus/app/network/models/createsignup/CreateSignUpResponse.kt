package com.pagarplus.app.network.models.createsignup

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponseListItem

data class CreateSignUpResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,
)