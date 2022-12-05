package com.pagarplus.app.network.models.attendance

import com.google.gson.annotations.SerializedName


data class SaveAttendanceDataRequest(

	@field:SerializedName("EmployeeID")
	val employeeID: Int? = null,

	@field:SerializedName("VisitType")
	val visitType: String? = null,

	@field:SerializedName("SessionTypeID")
	val sessionTypeID: Int? = null,

	@field:SerializedName("LogDate")
	val logDate: String? = null,

	@field:SerializedName("Location_Latitude")
	val location_Latitude: String? = null,

	@field:SerializedName("Location_Longitude")
	val location_Longitude: String? = null,

	@field:SerializedName("ImageURl")
	val imageURl: String? = null,

	@field:SerializedName("CheckInAddress")
	val checkInAddress: String? = null,

	@field:SerializedName("CheckOutAddress")
	val checkOutAddress: String? = null,
)
