package com.pagarplus.app.network.models.AdminaGetEmplist

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponseListItem

data class FetchGetEmpListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val empList:List<FetchGetEmpListResponseListItem?>? = null,
)

data class FetchGetEmpListResponseListItem(

	@field:SerializedName("ID")
	val Id: Int? = null,

	@field:SerializedName("Department")
	val department: String? = null,

	@field:SerializedName("ProfileImage")
	val profileImage: String? = null,

	@field:SerializedName("Designation")
	val designation: String? = null,

	@field:SerializedName("Name")
	val name: String? = null,

	@field:SerializedName("City")
	val city: String? = null,

	@field:SerializedName("Mobile")
	val mobile: String? = null,

	@field:SerializedName("Password")
	val password: String? = null,

	@field:SerializedName("UserName")
	val userName: String? = null,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("Branch")
	val branch: String? = null,

	@field:SerializedName("DateOfJoining")
	val dateOfJoining: String? = null,

	@field:SerializedName("SickLeave")
	val sickLeave: String? = null,

	@field:SerializedName("PaidLeave")
	val paidLeave: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = true

)
