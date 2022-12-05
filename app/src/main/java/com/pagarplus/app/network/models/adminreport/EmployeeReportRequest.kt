package com.pagarplus.app.network.models.employeeReports

import com.google.gson.annotations.SerializedName

data class EmployeeReportRequest (

    @field:SerializedName("AdminID")
    val AdminID: Int? = 0,

    @field:SerializedName("FromYear")
    val FromYear: String? = "",

    @field:SerializedName("ToYear")
    val ToYear: String? = "",

    @field:SerializedName("Adminyear")
    val Adminyear: String? = "",


    @field:SerializedName("Month")
    val Month: String? = "",


    @field:SerializedName("MonthList")
    val Months: ArrayList<MonthItem>? = null,

    @field:SerializedName("Employee")
    val Employee: ArrayList<EmployeeItem>? = null)

data class EmployeeItem (

    @field:SerializedName("EmployeeID")
    val EmpID: Int? = 0
    )
data class MonthItem (

    @field:SerializedName("MonthName")
    val MonthName: String? = "",

    @field:SerializedName("MonthValue")
    val MonthValue: String? = "",

)
