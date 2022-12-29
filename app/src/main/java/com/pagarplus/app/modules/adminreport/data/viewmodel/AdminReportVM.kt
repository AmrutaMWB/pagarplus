package com.pagarplus.app.modules.adminreport.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.adminreport.data.model.AdminReportModel
import com.pagarplus.app.modules.adminreport.data.model.AdminReportRowModel
import com.pagarplus.app.modules.applylol.data.model.LoanModel
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel
import com.pagarplus.app.modules.notificationcreatemessage.data.model.NotificationCreateMessageModel
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponse
import com.pagarplus.app.network.models.adminreport.*
import com.pagarplus.app.network.models.createMessage.EmpListItem
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject


public class AdminReportVM : ViewModel(), KoinComponent {
  public val adminReportModel: MutableLiveData<AdminReportModel> = MutableLiveData(AdminReportModel())
    public val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    public val adminSAReportLiveData: MutableLiveData<Response<AdminReportResponse>> = MutableLiveData<Response<AdminReportResponse>>()
    public val adminWiseDepartmentLiveData: MutableLiveData<Response<AdminReportResponse>> = MutableLiveData<Response<AdminReportResponse>>()
    public val adminWiseBranchLiveData: MutableLiveData<Response<AdminReportResponse>> = MutableLiveData<Response<AdminReportResponse>>()
    val fetchGetEmpLiveData: MutableLiveData<Response<GetEmpviaDeptListResponse>> =
        MutableLiveData<Response<GetEmpviaDeptListResponse>>()

    val fetchEmpLPayslipLiveData: MutableLiveData<Response<EmployeePaySlipResponse>> =
        MutableLiveData<Response<EmployeePaySlipResponse>>()
    val detailsList: MutableLiveData<MutableList<Details2RowModel>> = MutableLiveData(mutableListOf())

    private val networkRepository: NetworkRepository by inject()
    val recyclerEmployeeSAList: MutableLiveData<MutableList<AdminReportRowModel>> = MutableLiveData(mutableListOf())

    private val prefs: PreferenceHelper by inject()
    var profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

    fun callFetchGetEmpListApi(AdminId:Int,deptId:Int) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchGetEmpLiveData.postValue(
                networkRepository.fetchEmpViaDeptList(
                    adminID = AdminId,
                    deptID = deptId))
            progressLiveData.postValue(false)
        }
    }
    fun getBranchAndDepartmentList(AdminId:Int){
        viewModelScope.launch {
            progressLiveData.postValue(true)
            adminWiseDepartmentLiveData.postValue(networkRepository.fetchGetAdminDepartment(AdminId))
            adminWiseBranchLiveData.postValue(networkRepository.fetchGetAdminbranch(AdminId))
            progressLiveData.postValue(false)
        }
    }


    fun getAdminSAList(request: AdminSAReportRequest){
        viewModelScope.launch {
            progressLiveData.postValue(true)
            adminSAReportLiveData.postValue(networkRepository.fetchEmployeeSAListForAdmin( request))
            progressLiveData.postValue(false)
        }
    }

    fun binAdminSAList(response: AdminReportResponse,MonthName:String){
        val adminemplistModelValue = adminReportModel.value ?: AdminReportModel()
        val recyclerList=response.AdminSAList?.sortedBy { it.employee }?.map {
           it.toRowModel()
        }?.toMutableList()
        adminemplistModelValue.OfficialWorkingDays="$MonthName (${ recyclerList?.get(0)?.TotalWokingDays.toString() ?:"" })"

        recyclerEmployeeSAList.value=recyclerList
        adminReportModel.value = adminemplistModelValue
    }

    fun bindFetchGetEmpListResponse(response: GetEmpviaDeptListResponse) {

        val adminemplistModelValue = adminReportModel.value ?: AdminReportModel()


        val recyclerMsglist = response.empList1?.map {
            Details2RowModel(
                txtName = it?.name,
                txtEmpID = it?.Id,
                txtChecked = adminReportModel.value?.EmpIdlist?.contains(Employee(it?.Id))
            )
        }?.toMutableList()
        detailsList.value = recyclerMsglist
        adminReportModel.value = adminemplistModelValue
    }

    fun getEmployeePaySlipDetails(EmpId:Int,Month:Int,Year:Int){
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchEmpLPayslipLiveData.postValue(networkRepository.fetchEmployeePaySlipDetails(EmpId,Month,Year))
            progressLiveData.postValue(false)
        }
    }


}
