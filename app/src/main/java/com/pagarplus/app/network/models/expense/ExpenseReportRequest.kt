package com.pagarplus.app.network.models.expense

import com.google.gson.annotations.SerializedName

data class ExpenseReportRequest (
    @field:SerializedName("ExpenseTypeID")
    val ExpenseTypeID: String? = "",

    @field:SerializedName("EmployeeID")
    val EmployeeID: String? = "",

    @field:SerializedName("AdminID")
    val AdminID: String? = "",

    @field:SerializedName("FromDate")
    val FromDate: String? = "",

    @field:SerializedName("ToDate")
    val ToDate: String? = "",
)