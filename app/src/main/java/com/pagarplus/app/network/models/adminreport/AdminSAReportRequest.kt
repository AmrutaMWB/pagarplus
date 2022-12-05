package com.pagarplus.app.network.models.adminreport


import com.google.gson.annotations.SerializedName

data class AdminSAReportRequest(
    @SerializedName("AdminID")
    var adminID: Int? = 0,
    @SerializedName("Month")
    var month: Int? = 0,
    @SerializedName("Year")
    var year: Int? = 0,
    @SerializedName("Employee")
    var employee: List<Employee>? = listOf()
)

data class Employee(
    @SerializedName("EmployeeID")
    var employeeID: Int? = 0
)