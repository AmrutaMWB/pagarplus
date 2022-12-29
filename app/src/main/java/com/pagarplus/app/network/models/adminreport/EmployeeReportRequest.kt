package com.pagarplus.app.network.models.employeeReports

import com.google.gson.annotations.SerializedName

data class EmployeeReportRequest (

    @field:SerializedName("AdminID")
    val AdminID: Int? = 0,

    @field:SerializedName("FromYear")
    val FromYear: Int? = 0,

    @field:SerializedName("ToYear")
    val ToYear: Int? = 0,

    @field:SerializedName("Year")
    val Adminyear: Int = 0,


    @field:SerializedName("Month")
    val Month: Int? = 0,


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
    val MonthValue: Int? = 0,

)
