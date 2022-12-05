package com.pagarplus.app.network.models.createcreateemployee

import com.google.gson.annotations.SerializedName

data class CreateCreateEmployeeResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null
)
