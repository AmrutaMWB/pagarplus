package com.pagarplus.app.network.models.AdminaGetEmplist

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponseListItem

data class GetEmpviaDeptListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val empList1:List<GetEmpviaDeptListResponseListItem?>? = null,

	@field:SerializedName("data")
	val empList2:ArrayList<FinalEmpItem>? = arrayListOf()
)

data class GetEmpviaDeptListResponseListItem(

	@field:SerializedName("ID")
	val Id: Int? = null,

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

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("DateOfJoining")
	val dateOfJoining: String? = null

)

data class FinalEmpItem(

	@field:SerializedName("Value")
	val Id: Int? = null,

	@field:SerializedName("Text")
	val name: String? = null,
)


