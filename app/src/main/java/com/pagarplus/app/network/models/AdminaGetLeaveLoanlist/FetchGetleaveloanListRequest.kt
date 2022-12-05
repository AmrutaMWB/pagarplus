package com.pagarplus.app.network.models.AdminaGetLeaveLoanlist

import com.google.gson.annotations.SerializedName

data class FetchGetleaveloanListRequest(
	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("FromDate")
	val fromDate: String? = null,

	@field:SerializedName("ToDate")
	val toDate: String? = null,

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("DeptID")
	val deptID: Int? = 0,
)
