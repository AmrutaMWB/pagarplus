package com.pagarplus.app.network.models.createsignup

import com.google.gson.annotations.SerializedName

data class UpdateSignUpRequest(

	@field:SerializedName("Organization")
	val organization: String? = null,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("Address")
	val address: String? = null,

	@field:SerializedName("FirstName")
	val firstName: String? = null,

	@field:SerializedName("stateid")
	val stateid: Int? = 0,

	@field:SerializedName("cityid")
	val cityid: Int? = 0,

	@field:SerializedName("LastName")
	val lastName: String? = null,

	@field:SerializedName("MobileNumber")
	val mobile: String? = null,

	@field:SerializedName("Password")
	val password: String? = null,

	@field:SerializedName("ConfirmPassword")
	val confirmPassword: String? = null,

	@field:SerializedName("EmpID")
	val empID: Int? = 0,

	@field:SerializedName("ProfileImageURl")
	val profileImageURl: String? = null
)
