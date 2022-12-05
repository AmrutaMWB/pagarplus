package com.pagarplus.app.network.models.notificationMsg

import com.google.gson.annotations.SerializedName

data class FetchInOutMsgListResponse(
	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val msgList:List<FetchInOutMsgListResponseListItem?>? = null,
)

data class FetchInOutMsgListResponseListItem(
	@field:SerializedName("MainMessageID")
	val mainMsgId: Int? = 0,

	@field:SerializedName("MessageID")
	val msgId: Int? = 0,

	@field:SerializedName("Message")
	val message: String? = "",

	@field:SerializedName("FromEmployeeID")
	val fromEmpId: Int? = 0,

	@field:SerializedName("FromEmployee")
	val fromEmp: String? = "",

	@field:SerializedName("ReceivedDate")
	val recieveDate: String? = "",

	@field:SerializedName("IsMainMessage")
	val isMainMsg: String? = "",

	@field:SerializedName("ImagePath")
	val imagePath: String? = "",

	@field:SerializedName("UserProfileImage")
	val userProfileImage: String? = "",

	@field:SerializedName("Branch")
	val branch: String? = "",

	@field:SerializedName("Department")
	val department: String? = ""
)
