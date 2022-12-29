package com.pagarplus.app.network.models.creategetlogindetail

import com.google.gson.annotations.SerializedName

data class CreateGetLoginDetailRequest(

	@field:SerializedName("AccessToken")
	val accessToken: String? = null,

	@field:SerializedName("Username")
	val mobile: String? = null,

	@field:SerializedName("Password")
	val password: String? = null,

	@field:SerializedName("DeviceId")
	val deviceID: String? = null,

	@field:SerializedName("DeviceToken")
	val deviceToken: String? = null,
)
