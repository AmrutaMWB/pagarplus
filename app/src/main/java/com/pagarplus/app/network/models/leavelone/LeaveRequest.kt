package com.pagarplus.app.network.models.leavelone

import com.google.gson.annotations.SerializedName

data class LeaveRequest(
	@field:SerializedName("Comment")
	val comment: String? = null,

	@field:SerializedName("EmpID")
	val empID: String? = null,

	@field:SerializedName("ToDateTime")
	val toDateTime: String? = null,

	@field:SerializedName("FromDateTime")
	val fromDateTime: String? = null,

	@field:SerializedName("NoOfDays")
	val noOfDays: Double? = null,

	@field:SerializedName("LeaveType")
	val leaveType: String? = null,
)
