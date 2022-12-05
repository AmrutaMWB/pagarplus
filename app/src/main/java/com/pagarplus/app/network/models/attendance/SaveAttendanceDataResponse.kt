package com.pagarplus.app.network.models.attendance

import com.google.gson.annotations.SerializedName

data class SaveAttendanceDataResponse(
	@field:SerializedName("Status")
	val status: Boolean? = null,
	@field:SerializedName("Message")
	val message: String? = null,
)
