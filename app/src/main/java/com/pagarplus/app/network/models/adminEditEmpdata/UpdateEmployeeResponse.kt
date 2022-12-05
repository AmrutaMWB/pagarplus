package com.pagarplus.app.network.models.adminEditEmpdata

import com.google.gson.annotations.SerializedName

data class UpdateEmployeeResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null
)
