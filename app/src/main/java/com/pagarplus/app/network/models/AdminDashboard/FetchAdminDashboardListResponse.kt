package com.pagarplus.app.network.models.AdminDashboard

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.modules.admindashboard.data.model.AdmindashboardModel
import com.pagarplus.app.modules.editemployeedetails.data.model.EditemployeedetailsModel
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItem
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponseListItem

data class FetchAdminDashboardListResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("Dashboard")
	val dshboardList:List<FetchDashboardListResponseListItem?>? = null,
)

data class FetchDashboardListResponseListItem(

	@field:SerializedName("Id")
	val Id: Int? = null,

	@field:SerializedName("TotalExpense")
	val totalExpense: Int? = null,

	@field:SerializedName("TotalEmployees")
	val totalEmployees: Int? = null,

	@field:SerializedName("Dnsexpenses")
	val dnsexpenses: Int? = null,

	@field:SerializedName("Travelexpenses")
	val travelexpenses: Int? = null,

	@field:SerializedName("Localexpenses")
	val localexpenses: Int? = null,

	@field:SerializedName("Lodgingexpenses")
	val lodgingexpenses: Int? = null,

	@field:SerializedName("PresentEmployees")
	val presentEmployees: Int? = null,

	@field:SerializedName("AbsentEmployees")
	val absentEmployees: Int? = null

)

fun FetchDashboardListResponseListItem.toAdmindashboardModel(): AdmindashboardModel {
	return AdmindashboardModel(txttotAbsentVal = this.absentEmployees.toString(), txttotEmpVal = this.totalEmployees.toString(),
		txttotPresesntVal = this.presentEmployees.toString(), txtTotalExpense = this.totalExpense.toString())
}