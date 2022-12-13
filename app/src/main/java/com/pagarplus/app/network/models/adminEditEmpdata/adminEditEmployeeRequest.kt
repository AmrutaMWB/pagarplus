package com.pagarplus.app.network.models.adminEditEmpdata

import com.google.gson.annotations.SerializedName

data class AdminEditEmployeeRequest(

	@field:SerializedName("empid")
	val empid: Int? = 0,

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("Address")
	val address: String? = null,

	@field:SerializedName("FirstName")
	val firstName: String? = null,

	@field:SerializedName("BasicSalary")
	val basicSalary: String? = null,

	@field:SerializedName("NoOfSickLeave")
	val noOfSickLeave: String? = null,

	@field:SerializedName("LastName")
	val lastName: String? = null,

	@field:SerializedName("NoOfPaidLeave")
	val noOfPaidLeave: String? = null,

	@field:SerializedName("Designation")
	val designation: String? = null,

	@field:SerializedName("DateOfJoining")
	val dateOfJoining: String? = null,

	@field:SerializedName("MobileNumber")
	val mobileNumber: String? = null,

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("DepartmentID")
	val departmentID: Int? = 0,

	@field:SerializedName("EmergencyNumber")
	var emergencyNumber: String? = "",

	@field:SerializedName("BloodGroup")
	var bloodGroup: String? = "",

	@field:SerializedName("OfficeStartTime")
	var officeStartTime: String? = "",

	@field:SerializedName("OfficeEndTime")
	var officeEndTime: String? = "",

	@field:SerializedName("OldPassword")
	var oldPassword: String? = "",

	@field:SerializedName("Password")
	var password: String? = "",

	@field:SerializedName("Proofs")
	val proofs: ArrayList<FetchEditEmployeeDetailsResponseListItemProofsItem>? = null,
)
