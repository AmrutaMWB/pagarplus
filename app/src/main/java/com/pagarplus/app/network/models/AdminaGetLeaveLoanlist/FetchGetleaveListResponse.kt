package com.pagarplus.app.network.models.AdminaGetLeaveLoanlist

import com.google.gson.annotations.SerializedName

data class FetchGetleaveListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	var msgList:List<FetchGetleaveListResponseListItem>? = null,
)

data class FetchGetleaveListResponseListItem(

	@field:SerializedName("RequestID")
	val requestID: Int? = null,

	@field:SerializedName("EmpID")
	val empID: Int? = 0,

	@field:SerializedName("EmployeeName")
	val employeeName: String? = null,

	@field:SerializedName("AdminID")
	val adminID: String? = null,

	@field:SerializedName("ReasonForLeave")
	val reasonForLeave: String? = null,

	@field:SerializedName("LeaveType")
	val leaveType: String? = null,

	@field:SerializedName("AdminComment")
	val adminComment: String? = null,

	@field:SerializedName("ApproveStatus")
	val approveStatus: String? = null,

	@field:SerializedName("FromDateTime")
	val fromDateTime: String? = null,

	@field:SerializedName("ToDateTime")
	val toDateTime: String? = null,

	@field:SerializedName("RequestDate")
	val requestDate: String? = null,

	@field:SerializedName("ApprovedDate")
	val approvedDate: String? = null,

	@field:SerializedName("Branch")
	val branch: String? = "",

	@field:SerializedName("Department")
	val department: String? = "",

	@field:SerializedName("Designation")
	val designation: String? = "",

	@field:SerializedName("ApprovedLeaveType")
	val approvedLeaveType: String? = ""
)
