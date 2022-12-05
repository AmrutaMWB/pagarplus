package com.pagarplus.app.network.models.leavelone

import com.google.gson.annotations.SerializedName

data class LoanRequest(

	@field:SerializedName("LoanType")
	val LoanType : String? = null,

	@field:SerializedName("Comment")
	val comment: String? = null,

	@field:SerializedName("RequestedAmount")
	val amount: String? = null,

	@field:SerializedName("EmpID")
	val empID: String? = null
)
