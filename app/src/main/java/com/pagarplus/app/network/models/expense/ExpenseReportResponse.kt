package com.pagarplus.app.network.models.expense

import com.google.gson.annotations.SerializedName
import java.io.Serializable

 data class ExpenseReportResponse  (

@field:SerializedName("Status")
val status: Boolean? = false,

@field:SerializedName("Message")
val message: String? = "",

@field:SerializedName("ExpenseList")
val expenseList:ArrayList<ExpenseRowModel>? = arrayListOf(),

@field:SerializedName("Data")
val expenseReportList:ArrayList<ExpenseReportListItem>? = arrayListOf())

   data class ExpenseRowModel  (

    @field:SerializedName("ExpenseID")
    val ExpenseID: Int? = 0,

    @field:SerializedName("Icon")
    val Icon: String? = "",

    @field:SerializedName("AdminID")
    val AdminID: Int? = 0,

    @field:SerializedName("EmployeeID")
    val EmployeeID: Int? = 0,

    @field:SerializedName("ExpenseTypeID")
    val ExpenseTypeID: Int? = 0,

    @field:SerializedName("SubExpenseTypeID")
    val SubExpenseTypeID: Int? = 0,

    @field:SerializedName("EmployeeName")
    val EmployeeName: String? = "",

    @field:SerializedName("ExpenseTypeName")
    val ExpenseTypeName: String? = "",

    @field:SerializedName("SubExpenseTypeName")
    var SubExpenseTypeName: String? = "",

    @field:SerializedName("SourceLocation")
    val SourceLocation: String? = "",

    @field:SerializedName("DestinationLocation")
    val DestinationLocation: String? = "",

    @field:SerializedName("ExpenseDateTime")
    var ExpenseDateTime: String? = "",

    @field:SerializedName("TravelDateTime")
    val TravelDateTime: String? = "",

    @field:SerializedName("Amount")
    val Amount: Int? = 0,

    var AmountValue: String? = "₹$Amount",

    @field:SerializedName("CommentByEmployee")
    val CommentByEmployee: String? = "",

    @field:SerializedName("CommentByAdmin")
    val CommentByAdmin: String? = "",

    @field:SerializedName("HotelName")
    val HotelName: String? = "",

    @field:SerializedName("HotelDetails")
    val HotelDetails: String? = "",

    @field:SerializedName("LocationLatitude")
    val LocationLatitude: String? = "",

    @field:SerializedName("LocationLongitude")
    val LocationLongitude: String? = "",

    @field:SerializedName("BoardFromDate")
    val BoardFromDate: String? = "",

    @field:SerializedName("BoardToDate")
    val BoardToDate: String? = "",

    @field:SerializedName("Status")
    var Status: String? = "",
    @field:SerializedName("ProfileImgUrl")
    val ProfileImgUrl: String? = "",

    @field:SerializedName("BillImages")
    val billImages: ArrayList<ImageItems>? = null,
    @field:SerializedName("BillImageUrl")
    val BillImageUrl: String? = null, ):Serializable

data class ImageItems(
    @field:SerializedName("BillImageUrl")
    val billImageUrl: String? = null, ):Serializable



data class ExpenseReportListItem(
    @field:SerializedName("EmployeeID")
    val EmployeeID: Int? = 0,

    @field:SerializedName("EmployeeName")
    val EmployeeName : String? = "",

    @field:SerializedName("DnsAmount")
    var DnsAmount: String? = "",

    @field:SerializedName("TravelAmount")
    val TravelAmount: String? = "",

    @field:SerializedName("LocalAmount")
    val LocalAmount: String? = "",
    @field:SerializedName("LodgeAmount")
    val LodgeAmount: String? = "",

    @field:SerializedName("TotalAmount")
    val TotalAmount: Int? = 0 )