package com.pagarplus.app.modules.formaemployeeregister.data.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalpagarreportscreen.app.modules.formaemployeeregister.data.model.FormAEmployeeRegisterModel
import com.google.android.gms.common.config.GservicesValue.value
import com.pagarplus.app.modules.formaemployeeregister.ui.FormAEmployeeRegisterActivity
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponse
import com.pagarplus.app.network.models.adminreport.AdminReportResponse
import com.pagarplus.app.network.models.employeeReports.EmployeeItem
import com.pagarplus.app.network.models.employeeReports.EmployeeReportRequest
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.*


class FormAEmployeeRegisterVM : ViewModel(), KoinComponent {
  val formAEmployeeRegisterModel: MutableLiveData<FormAEmployeeRegisterModel> =
      MutableLiveData(FormAEmployeeRegisterModel())

  var navArguments: Bundle? = null


    /*emplist in recyclerview dialog*/
    val detailsList: MutableLiveData<MutableList<Details2RowModel>> = MutableLiveData(mutableListOf())

    public val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val fetchBranchLiveData: MutableLiveData<Response<AdminReportResponse>> =
        MutableLiveData<Response<AdminReportResponse>>()
    val fetchDepartmentLiveData: MutableLiveData<Response<AdminReportResponse>> =
        MutableLiveData<Response<AdminReportResponse>>()
    val fetcheEmployeeLiveData: MutableLiveData<Response<GetEmpviaDeptListResponse>> =
        MutableLiveData<Response<GetEmpviaDeptListResponse>>()
    val fetcheEmployeeReportLiveData: MutableLiveData<Response<ResponseBody>> = MutableLiveData<Response<ResponseBody>>()
    val fetchUrlLiveData: MutableLiveData<Response<ResponseBody>> = MutableLiveData<Response<ResponseBody>>()
    private val networkRepository: NetworkRepository by inject()


    fun callFetchBranchListApi(adminId:Int) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchBranchLiveData.postValue(
                networkRepository.fetchGetAdminbranch(
                    adminID = adminId//profileDetails?.orgID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callFetchDepartmentApi(adminId:Int) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchDepartmentLiveData.postValue(
                networkRepository.fetchGetAdminDepartment(
                    adminID = adminId//profileDetails?.orgID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callFetchEmployeeApi(adminId: Int,branchId:Int,deptId:Int,year:Int) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetcheEmployeeLiveData.postValue(
                networkRepository.fetchAdminEmployeesList(
                    adminID = adminId, branchID = branchId, deptID = deptId, year = year//profileDetails?.orgID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchGetEmpListResponse(response: GetEmpviaDeptListResponse) {

        val adminemployeeistModelValue = formAEmployeeRegisterModel.value ?: FormAEmployeeRegisterModel()
        val recyclerMsglist = response.empList2?.map {
            Details2RowModel(
                txtName = it.name,
                txtEmpID = it.Id,
                txtChecked = formAEmployeeRegisterModel.value?.EmpIdlist?.contains(EmployeeItem(it.Id))
            )
        }?.toMutableList()
        detailsList.value = recyclerMsglist?.toMutableList()
        formAEmployeeRegisterModel.value = adminemployeeistModelValue
    }

    fun callFetchReportApi(employeeReportRequest: EmployeeReportRequest, activity: FormAEmployeeRegisterActivity){

        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchUrlLiveData.postValue(networkRepository.fetchReport(employeeReportRequest))
            progressLiveData.postValue(false)
           // val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(responseBody.getSuccessResponse().toString()))
          //  activity.startActivity(browserIntent)
        }

    }


    fun callFetchBReportApi(employeeReportRequest: EmployeeReportRequest, activity: FormAEmployeeRegisterActivity) {

        viewModelScope.launch {
            progressLiveData.postValue(true)
            //var responseBody = (networkRepository.fetchBReport(employeeReportRequest))
            fetchUrlLiveData.postValue(networkRepository.fetchBReport(employeeReportRequest))
            progressLiveData.postValue(false)
            //val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(responseBody.getSuccessResponse().toString()))
            //activity.startActivity(browserIntent)
        }
    }

        fun callFetchCReportApi(employeeReportRequest: EmployeeReportRequest, activity: FormAEmployeeRegisterActivity) {
            val destination = File(
                Environment.getExternalStorageDirectory(),
                System.currentTimeMillis().toString() + ".pdf"
            )

            viewModelScope.launch {
                progressLiveData.postValue(true)
                var responseBody = (networkRepository.fetchCReport(employeeReportRequest))

                progressLiveData.postValue(false)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(responseBody.getSuccessResponse().toString()))
                activity.startActivity(browserIntent)
            }

        }
            fun callFetchEReportApi(employeeReportRequest: EmployeeReportRequest, activity: FormAEmployeeRegisterActivity){
                val destination = File(Environment.getExternalStorageDirectory(), System.currentTimeMillis().toString() + ".pdf")

                viewModelScope.launch {
                    progressLiveData.postValue(true)
                    var  responseBody=(networkRepository.fetchEReport(employeeReportRequest))
                    progressLiveData.postValue(false)
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(responseBody.getSuccessResponse().toString()))
                    activity.startActivity(browserIntent)
                }

    }



}
