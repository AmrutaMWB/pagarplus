package com.pagarplus.app.network.models.createcreatebanner

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateCreateBannerResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val advertiseList:List<FetchGetAdvertiseListResponseListItem?>? = null,
)

data class FetchGetAdvertiseListResponseListItem(

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("ActiveStatus")
	val activeStatus: Boolean? = false,

	@field:SerializedName("BannerID")
	val bannerID: Int? = 0,

	@field:SerializedName("DepartmentId")
	val departmentId: Int? = 0,

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("BannerImage")
	val bannerImage: String? = null,

	@field:SerializedName("Title")
	val title: String? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("FromDate")
	val fromDate: String? = null,

	@field:SerializedName("ToDate")
	val toDate: String? = null,

	@field:SerializedName("Branch")
	val branch: String? = null,

	@field:SerializedName("Department")
	val department: String? = null,

): Serializable
