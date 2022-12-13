package com.pagarplus.app.network.models.AdminaGetLeaveLoanlist

import com.google.gson.annotations.SerializedName

data class ApproveLeaveRequest(
	@field:SerializedName("RequestID")
	val requestID: Int? = null,

	@field:SerializedName("AdminID")
	val adminID: Int? = null,

	@field:SerializedName("Comment")
	val comment: String? = null,

	@field:SerializedName("ApprovedLeaveType")
	val approvedLeaveType: String? = null,
)
