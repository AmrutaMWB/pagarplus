package com.pagarplus.app.network.models.AdminaGetLeaveLoanlist

import com.google.gson.annotations.SerializedName

data class ApproveLoanRequest(
	@field:SerializedName("LoanID")
	val loanID: Int? = null,

	@field:SerializedName("MonthlyDeduction")
	val monthlyDeduction: Int? = 0,

	@field:SerializedName("ApprovedAmount")
	val approvedAmount: Int? = 0,

	@field:SerializedName("Comment")
	val comment: String? = null,
)

data class RejectLoanRequest(
	@field:SerializedName("LoanID")
	val loanID: Int? = 0,

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("Comment")
	val comment: String? = null,
)