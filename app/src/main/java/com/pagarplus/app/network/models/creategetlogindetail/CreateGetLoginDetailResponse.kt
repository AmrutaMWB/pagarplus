package com.pagarplus.app.network.models.creategetlogindetail

import com.google.gson.annotations.SerializedName

data class CreateGetLoginDetailResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val Message: String? = null,

	@field:SerializedName("DeviceID")
	val deviceID: String? = null,

	@field:SerializedName("list")
	val loginCustomerData: List<LoginResponse?>? = null,
)

data class LoginResponse(

	@field:SerializedName("Email")
	val email: String? = "",

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("RoleID")
	val roleID: Int? = 0,

	@field:SerializedName("OrgID")
	val orgID: Int? = 0,

	@field:SerializedName("UserID")
	val userID: Int? = 0,

	@field:SerializedName("Name")
	val username: String? = "",

	@field:SerializedName("MobileNumber")
	val mobileNumber: String? = "",

	@field:SerializedName("HasRegistered")
	val hasRegistered: Boolean? = false,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = true,

	@field:SerializedName("LoginProvider")
	val loginProvider: String? = "",

	@field:SerializedName("IsAdmin")
	val isAdmin: Boolean? = false,

	@field:SerializedName("PlanType")
	var planType: String? = "",

	@field:SerializedName("AppVersion")
	var appVersion: Int? = 0,
)

data class LogOutRequest(
	@field:SerializedName("AccessToken")
	val accessToken: String? = ""
)