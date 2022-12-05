package com.pagarplus.app.network.models.creategetlogindetail

import com.google.gson.annotations.SerializedName

data class CreateGetLoginDetailRequest(

	@field:SerializedName("AccessToken")
	val deviceID: String? = null,

	@field:SerializedName("Username")
	val mobile: String? = null,

	@field:SerializedName("Password")
	val password: String? = null
)
