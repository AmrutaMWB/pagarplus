package com.pagarplus.app.modules.applylol.data.model

data class LoanModel (
    var LoanId: Int? =0,
    var LoanType: String? ="",
    var EmpID: Int? =0,
    var AdminID: Int? =0,
    var MonthlyDeduction: String? ="",
    var EmployeeName: String? ="",
    var RequestedAmount: String?="",
    var ApprovedAmount: String?="",
    var Comment: String?="",
    var RequestDate: String?="",
    var ReasonForLoan: String?="",
    var ApproveStatus: String?="",
    var Salary: Int?=0,
    var OldLoanBalance: Int?=0,
)