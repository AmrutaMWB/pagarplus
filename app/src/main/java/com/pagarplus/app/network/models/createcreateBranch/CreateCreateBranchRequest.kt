package com.pagarplus.app.network.models.createcreateBranch

import com.google.gson.annotations.SerializedName

data class CreateCreateBranchRequest(

	@field:SerializedName("OrgID")
	val orgId: Int? = null,

	@field:SerializedName("Name")
	val name: String? = null,

	@field:SerializedName("Address")
	val address: String? = null,

	@field:SerializedName("City")
	val city: String? = null,

	@field:SerializedName("State")
	val state: String? = null,

	@field:SerializedName("Latitude")
	val latitude: String? = null,

	@field:SerializedName("Longitude")
	val longitude: String? = null,


)
