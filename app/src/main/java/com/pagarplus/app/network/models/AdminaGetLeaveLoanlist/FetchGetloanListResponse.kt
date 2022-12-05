package com.pagarplus.app.network.models.AdminaGetLeaveLoanlist

import com.google.gson.annotations.SerializedName

data class FetchGetloanListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("Requests")
	var msgList:List<FetchGetloanListResponseListItem>? = null,
)

data class FetchGetloanListResponseListItem(

	@field:SerializedName("LoanType")
	val loanType: String? = "Loan",

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("MonthlyDeduction")
	val monthlyDeduction: Int? = 0,

	@field:SerializedName("LoanID")
	val loanID: Int? = 0,

	@field:SerializedName("EmpID")
	val empID: Int? = 0,

	@field:SerializedName("EmpName")
	val empName: String? = null,

	@field:SerializedName("RequestedAmount")
	val requestedAmount: String? = "0",

	@field:SerializedName("ApprovedAmount")
	val approvedAmount: Int? = 0,

	@field:SerializedName("Comment")
	val comment: String? = null,

	@field:SerializedName("RequestDate")
	val requestDate: String? = null,

	@field:SerializedName("ReasonForLoan")
	val reasonForLoan: String? = null,

	@field:SerializedName("ApproveStatus")
	val approveStatus: String? = null,

	@field:SerializedName("Salary")
	val salary: Int? = 0,

	@field:SerializedName("OldLoanBalance")
	val oldLoanBalance: Int? = 0,

	@field:SerializedName("Branch")
	val branch: String? = "",

	@field:SerializedName("Department")
	val department: String? = ""
)
