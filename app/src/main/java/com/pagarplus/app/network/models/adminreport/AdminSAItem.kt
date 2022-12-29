package com.pagarplus.app.network.models.adminreport


import com.google.gson.annotations.SerializedName
import com.pagarplus.app.modules.adminreport.data.model.AdminReportRowModel
import java.math.BigDecimal
import java.text.DecimalFormat

data class AdminSAItem(
    @SerializedName("EmployeeID")
    val employeeID: String? = "",

    @SerializedName("Employee")
    val employee: String? = "",

    @SerializedName("Branch")
    val branch: String? = "",

    @SerializedName("Department")
    val department: String? = "",

    @SerializedName("TotalWokingDays")
    val totalWokingDays: Int? = 0,

    @SerializedName("OfficialHolidays")
    val officialHolidays: Int? = 0,

    @SerializedName("EmployeeLeaves")
    val employeeLeaves: Double? = 0.0,

    @SerializedName("CalculatedLeaves")
    val calculatedLeaves: Double? = 0.0,

    @SerializedName("PresentDays")
    val presentDays: Double? = 0.0,

    @SerializedName("PerDaySalary")
    val perDaySalary: Double? =0.0,

    @SerializedName("PayableSalary")
    val payableSalary: Double? = 0.0,

    @SerializedName("leaveDeduction")
    val leaveDeduction: Double? = 0.0,

    @SerializedName("Incentive")
    val incentive: Double? = 0.0,

    @SerializedName("SickLeave")
    val sickLeave: Double? = 0.0,

    @SerializedName("PaidLeave")
    val paidLeave: Double? = 0.0,

    @SerializedName("HRA")
    val hRA: Any? = Any(),

    @SerializedName("Others")
    val others: Any? = Any(),

    @SerializedName("DA")
    val dA: Any? = Any(),

    @SerializedName("OT")
    val oT: Any? = Any(),

    @SerializedName("BasicSalary")
    val basicSalary: Any? = Any()
)

fun AdminSAItem.toRowModel():AdminReportRowModel{
    val Format = DecimalFormat("##.00")
    val total=this.totalWokingDays?:0
    val presentDays=this.presentDays?:0.0
    val percentage=if(presentDays>0)(presentDays.div(total)*100) else 0.0
    return AdminReportRowModel(
        EmployeeID = this.employeeID,
        Employee = this.employee,
        TotalWokingDays = this.totalWokingDays,
        OfficialHolidays = this.officialHolidays,
        EmployeeLeaves = this.employeeLeaves,
        CalculatedLeaves = this.calculatedLeaves,
        PresentDays = this.presentDays?.toInt()?:0,
        PerDaySalary = this.perDaySalary, PayableSalary = this.payableSalary,
        leaveDeduction = this.leaveDeduction,Incentive = this.incentive,
        SickLeave = this.sickLeave,
        PaidLeave = this.paidLeave,
        HRA = this.hRA,
        Others = this.others,
        DA = this.dA, OT = this.oT,
        AttEfficiency ="${Format.format(BigDecimal(percentage))}%",
        PayableAmt = "₹ ${this.payableSalary?:0.0}",
        DeductedAmt = "₹ ${this.leaveDeduction?:0.0}",
        Branch = this.branch,
        Department = this.department
    )
}