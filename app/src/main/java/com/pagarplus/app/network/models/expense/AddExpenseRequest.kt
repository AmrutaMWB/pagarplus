package com.pagarplus.app.network.models.expense

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddExpenseRequest(
	@field:SerializedName("EmpID")
	var empID: String? = null,

	@field:SerializedName("ExpenseDateTime")
	var expenseDateTime: String? = null,

	@field:SerializedName("Amount")
	var amount: String? = null,

	@field:SerializedName("ExpHeadID")
	var expHeadID: String? = null,

	@field:SerializedName("ExpTypeID")
	var expTypeID: String? = null,

	@field:SerializedName("SourceLocation")
	var sourceLocation: String? = null,

	@field:SerializedName("DestinationLocation")
	var destinationLocation: String? = null,

	@field:SerializedName("HotelDetails")
	var hotelDetails: String? = null,

	@field:SerializedName("HotelName")
	var hotelName: String? = null,

	@field:SerializedName("LocationLongitude")
	var locationLongitude: String? = null,

	@field:SerializedName("LocationLatitude")
	var locationLatitude: String? = null,

	@field:SerializedName("CommentByEmployee")
	var commentByEmployee: String? = null,

	@field:SerializedName("BoardFromDate")
	var boardingFromDate: String? = null,

	@field:SerializedName("BoardToDate")
	var boardingToDate: String? = null,

	@field:SerializedName("BillImages")
	val billImages: ArrayList<ImageItem>? = null,
)

data class ImageItem(
	@field:SerializedName("BillImageUrl")
	val billImageUrl: String? = null,
):Serializable
