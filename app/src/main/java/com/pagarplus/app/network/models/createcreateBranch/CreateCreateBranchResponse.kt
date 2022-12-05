package com.pagarplus.app.network.models.createcreateBranch

import com.google.gson.annotations.SerializedName

data class CreateCreateBranchResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null
)
