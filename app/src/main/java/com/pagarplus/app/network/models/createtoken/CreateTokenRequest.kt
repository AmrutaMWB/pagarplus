package com.pagarplus.app.network.models.createtoken

import com.google.gson.annotations.SerializedName

data class CreateTokenRequest(
	@field:SerializedName("password")
	var password: String? = "Dgy7t7MvU3JZx+ObnW6z3A==",

	@field:SerializedName("grant_type")
	var grantType: String? = "password",

	@field:SerializedName("username")
	var username: String? = "dMRo3oqVLOs="
)
