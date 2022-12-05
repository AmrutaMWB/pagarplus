package com.pagarplus.app.network.models.AdminDashboard

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.modules.admindashboard.data.model.AdmindashboardModel
import com.pagarplus.app.modules.attendance_details.data.model.AttendanceDetailsModel
import com.pagarplus.app.modules.editemployeedetails.data.model.EditemployeedetailsModel
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItem
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponseListItem
import java.io.Serializable

data class AdminFetchEmpAttendanceListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val attendanceList:List<AdminFetchEmpAttendanceListResponseItem?>? = arrayListOf(),

	@field:SerializedName("AttendanceList")
	val empattendanceList:List<AttendanceItem>?= arrayListOf())

data class AttendanceItem(
	@field:SerializedName("Date")
	val Date: String? = "",
	@field:SerializedName("IsLeaveApplied")
	val IsLeaveApplied: Boolean? = false,
	@field:SerializedName("LeaveDetails")
	val LeaveDetails:ArrayList<LeaveDetailItem>? = arrayListOf(),
	@field:SerializedName("IsHoliday")
	val IsHoliday: Boolean? = false,
	@field:SerializedName("HolidayDetails")
	val HolidayDetails:ArrayList<HolidayDetailItem>? = arrayListOf(),
	@field:SerializedName("IsWorkingDay")
	val IsWorkingDay: Boolean? = false,
	@field:SerializedName("Attendance")
	val Attendance: ArrayList<AdminFetchEmpAttendanceListResponseItem>? = arrayListOf(),
	var AttenDanceDetailList:MutableList<AttendanceDetailsModel>?= arrayListOf()
)
data class LeaveDetailItem(
	@field:SerializedName("ReasonForLeave")
	val ReasonForLeave: String? = "",
	@field:SerializedName("LeaveStatus")
	val LeaveStatus: String? = "",
	@field:SerializedName("LeaveType")
	val LeaveType: String? = "N/A",
	@field:SerializedName("FromDate")
	val FromDate: String? = "",
	@field:SerializedName("ToDate")
	val ToDate: String? = "",
)
data class HolidayDetailItem(
	@field:SerializedName("IsHalfDay")
	val IsHalfDay: Boolean? = false,
	@field:SerializedName("HolidayType")
	val HolidayType: String? = "",
	@field:SerializedName("ReasonForHoliday")
	val ReasonForHoliday: String? = "",
)
data class AdminFetchEmpAttendanceListResponseItem(
	@field:SerializedName("ID")
	val atendanceid: Int? = 0,

	@field:SerializedName("EmployeeID")
	val employeeID: Int? = 0,

	@field:SerializedName("EmployeeName")
	val employeeName: String? = null,

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("Branch")
	val branch: String? = "",

	@field:SerializedName("Department")
	val department: String? = "",

	@field:SerializedName("ProfileImgUrl")
	val profileImgUrl: String? = null,

	@field:SerializedName("Type")
	val type: String? = null,

	@field:SerializedName("VisitTypeName")
	val visitTypeName: String? = null,

	@field:SerializedName("SessionTypeName")
	val sessionTypeName: String? = null,

	@field:SerializedName("CheckInDate")
	val checkInDate: String? = null,

	@field:SerializedName("CheckOutDate")
	val checkOutDate: String? = null,

	@field:SerializedName("ApproveStatus")
	val approveStatus: String? = null,

	@field:SerializedName("Admin_Comments")
	val admin_Comments: String? = null,

	@field:SerializedName("CheckIn_Latitude")
	val checkInLatitude: String? = "",

	@field:SerializedName("CheckIn_Longitude")
	val checkInLongitude: String? = "",

	@field:SerializedName("CheckOut_Latitude")
	val checkOutLatitude: String? = "",

	@field:SerializedName("CheckOut_Longitude")
	val checkOutLongitude: String? = "",

	@field:SerializedName("CheckInAddress")
	val checkinLocation: String? = "",

	@field:SerializedName("CheckOutAddress")
	val checkoutLocation: String? = "",

	@field:SerializedName("CheckInImgURL")
	val checkInImgURL: String? = "",

	@field:SerializedName("CheckOutImgURL")
	val checkOutImgURL: String? = "",

	@field:SerializedName("IsLeaveExist")
	val isLeaveExist: String? = "false",

	@field:SerializedName("LeaveStatus")
	val leaveStatus: String? = "",

	@field:SerializedName("ReasonForLeave")
	val reasonForLeave: String? = "",

	@field:SerializedName("ContactNumber")
	val contactNumber: String? = ""
)