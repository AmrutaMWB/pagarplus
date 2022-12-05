package com.pagarplus.app.network.models.createcreatebanner

import com.google.gson.annotations.SerializedName

data class CreateCreateBannerRequest(

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("BannerID")
	val bannerID: Int? = 0,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("BannerImage")
	val bannerImage: String? = null,

	@field:SerializedName("Title")
	val title: String? = null,

	@field:SerializedName("FromDate")
	val fromDate: String? = null,

	@field:SerializedName("ToDate")
	val toDate: String? = null,

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("DepartmentId")
	val departmentId: Int? = 0
)
