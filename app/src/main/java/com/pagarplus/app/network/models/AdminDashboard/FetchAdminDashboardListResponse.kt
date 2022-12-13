package com.pagarplus.app.network.models.AdminDashboard

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.modules.admindashboard.data.model.AdmindashboardModel
import com.pagarplus.app.modules.editemployeedetails.data.model.EditemployeedetailsModel
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItem
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponseListItem

data class FetchAdminDashboardListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("Dashboard")
	val dshboardList:List<FetchDashboardListResponseListItem?>? = null,
)

data class FetchDashboardListResponseListItem(

	@field:SerializedName("Id")
	val Id: Int? = null,

	@field:SerializedName("TotalExpense")
	val totalExpense: Int? = null,

	@field:SerializedName("Dnsexpenses")
	val dnsexpenses: Int? = null,

	@field:SerializedName("Travelexpenses")
	val travelexpenses: Int? = null,

	@field:SerializedName("Localexpenses")
	val localexpenses: Int? = null,

	@field:SerializedName("Lodgingexpenses")
	val lodgingexpenses: Int? = null,

	@field:SerializedName("Total")
	val totalEmployees: Int? = null,

	@field:SerializedName("Present")
	val presentEmployees: Int? = null,

	@field:SerializedName("Absent")
	val absentEmployees: Int? = null,

	@field:SerializedName("Branches")
	val branches:List<FetchBranchListResponseListItem?>? = null,
)

data class FetchBranchListResponseListItem(

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("Branch")
	val branch: String? = "",

	@field:SerializedName("Present")
	val present: Int? = 0,

	@field:SerializedName("Absent")
	val absent: Int? = 0,

	@field:SerializedName("Total")
	val total: Int? = 0,
)

/*
fun FetchBranchListResponseListItem.toAdmindashboardModel(): AdmindashboardModel {
	return AdmindashboardModel(txttotAbsentVal = this.absent.toString(), txttotEmpVal = this.total.toString(),
		txttotPresesntVal = this.present.toString(), txtBranch = this.branch.toString())
}*/
