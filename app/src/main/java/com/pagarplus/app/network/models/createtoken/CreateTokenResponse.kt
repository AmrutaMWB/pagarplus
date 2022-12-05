package com.pagarplus.app.network.models.createtoken

import com.google.gson.annotations.SerializedName

data class CreateTokenResponse(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("expires_in")
	val expiresIn: Int? = null
)
