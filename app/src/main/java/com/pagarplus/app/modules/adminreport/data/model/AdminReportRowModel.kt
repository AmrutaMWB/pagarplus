package com.pagarplus.app.modules.adminreport.data.model

data class AdminReportRowModel(
    var EmployeeID: String? = "",
    var Employee: String? = "",
    var TotalWokingDays: Int? = 0,
    var OfficialHolidays: Int? = 0,
    var EmployeeLeaves: Double? = 0.0,
    var CalculatedLeaves: Double? = 0.0,
    var PresentDays: Int? = 0,
    var PerDaySalary: Double? =0.0,
    var PayableSalary: Double? = 0.0,
    var leaveDeduction: Double? = 0.0,
    var Incentive: Double? = 0.0,
    var SickLeave: Double? = 0.0,
    var PaidLeave: Double? = 0.0,
    var HRA: Any? = Any(),
    var Others: Any? = Any(),
    var DA: Any? = Any(),
    var OT: Any? = Any(),
    var BasicSalary: Any? = Any(),
    var AttEfficiency:String="",//"${((PresentDays?.div(TotalWokingDays!!))?.times(100)).toString()} %"
    var PayableAmt:String="",//"${((PresentDays?.div(TotalWokingDays!!))?.times(100)).toString()} %"
    var DeductedAmt:String=""//"${((PresentDays?.div(TotalWokingDays!!))?.times(100)).toString()} %"

)