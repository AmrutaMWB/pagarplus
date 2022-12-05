package com.pagarplus.app.modules.formaemployeeregister.data.viewmodel

import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalpagarreportscreen.app.modules.formaemployeeregister.`data`.model.FormAEmployeeRegisterModel
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
import kotlin.collections.MutableList
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
    private val networkRepository: NetworkRepository by inject()


    fun callFetchBranchListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchBranchLiveData.postValue(
                networkRepository.fetchGetAdminbranch(
                    adminID = 4//profileDetails?.orgID
                )
            )
            progressLiveData.postValue(false)
        }
    }

//    fun bindFetchBranchListResponse(response: StateListResponse) {
//        val statelistModelValue = itemlistModel.value ?: ItemlistModel()
//        val recyclerMsglist = response.stateList?.map {
//            Itemlistdialog1RowModel(
//                txtName = it?.text,
//                txtValue = it?.value,
//                isBranch = true,
//            )
//        }?.toMutableList()
//        BranchList.value = recyclerMsglist
//        itemlistModel.value = statelistModelValue
//    }

    fun callFetchDepartmentApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchDepartmentLiveData.postValue(
                networkRepository.fetchGetAdminDepartment(
                    adminID = 4//profileDetails?.orgID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callFetchEmployeeApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetcheEmployeeLiveData.postValue(
                networkRepository.fetchAdminEmployeesList(
                    adminID = 4, branchID = 0, deptID = 0, year = "2022"//profileDetails?.orgID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchGetEmpListResponse(response: GetEmpviaDeptListResponse) {

        val adminemployeeistModelValue = formAEmployeeRegisterModel.value ?: FormAEmployeeRegisterModel()
        val recyclerMsglist = response.empList1?.map {
            Details2RowModel(
                txtName = it?.name,
                txtEmpID = it?.Id,
                txtChecked = formAEmployeeRegisterModel.value?.EmpIdlist?.contains(EmployeeItem(it?.Id))
            )
        }?.toMutableList()
        detailsList.value = recyclerMsglist?.toMutableList()
        formAEmployeeRegisterModel.value = adminemployeeistModelValue
    }

    fun callFetchReportApi(employeeReportRequest: EmployeeReportRequest, activity: FormAEmployeeRegisterActivity){
        val destination = File(Environment.getExternalStorageDirectory(), System.currentTimeMillis().toString() + ".pdf")

        viewModelScope.launch {
            progressLiveData.postValue(true)
          var  responseBody=(networkRepository.fetchReport(employeeReportRequest))
          //  var _imageName = destination.name
           // var _imageExtension = destination.absolutePath.substring(destination.absolutePath.lastIndexOf("."))
            var fo: FileOutputStream
            try {
                destination.createNewFile()
                fo = FileOutputStream(destination)
                fo.write(getBytesFromInputStream(responseBody.getSuccessResponse()!!.byteStream()))
                fo.close()
                activity.previewPdf(destination)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            progressLiveData.postValue(false)
        }

    }


    fun callFetchBReportApi(employeeReportRequest: EmployeeReportRequest, activity: FormAEmployeeRegisterActivity) {
        val destination = File(
            Environment.getExternalStorageDirectory(),
            System.currentTimeMillis().toString() + ".pdf"
        )

        viewModelScope.launch {
            progressLiveData.postValue(true)
            var responseBody = (networkRepository.fetchBReport(employeeReportRequest))
            //  var _imageName = destination.name
            // var _imageExtension = destination.absolutePath.substring(destination.absolutePath.lastIndexOf("."))
            var fo: FileOutputStream
            try {
                destination.createNewFile()
                fo = FileOutputStream(destination)
                fo.write(getBytesFromInputStream(responseBody.getSuccessResponse()!!.byteStream()))
                fo.close()
                activity.previewPdf(destination)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            progressLiveData.postValue(false)
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
                //  var _imageName = destination.name
                // var _imageExtension = destination.absolutePath.substring(destination.absolutePath.lastIndexOf("."))
                var fo: FileOutputStream
                try {
                    destination.createNewFile()
                    fo = FileOutputStream(destination)
                    fo.write(
                        getBytesFromInputStream(
                            responseBody.getSuccessResponse()!!.byteStream()
                        )
                    )
                    fo.close()
                    activity.previewPdf(destination)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                progressLiveData.postValue(false)
            }

        }
            fun callFetchEReportApi(employeeReportRequest: EmployeeReportRequest, activity: FormAEmployeeRegisterActivity){
                val destination = File(Environment.getExternalStorageDirectory(), System.currentTimeMillis().toString() + ".pdf")

                viewModelScope.launch {
                    progressLiveData.postValue(true)
                    var  responseBody=(networkRepository.fetchEReport(employeeReportRequest))
                    //  var _imageName = destination.name
                    // var _imageExtension = destination.absolutePath.substring(destination.absolutePath.lastIndexOf("."))
                    var fo: FileOutputStream
                    try {
                        destination.createNewFile()
                        fo = FileOutputStream(destination)
                        fo.write(getBytesFromInputStream(responseBody.getSuccessResponse()!!.byteStream()))
                        fo.close()
                        activity.previewPdf(destination)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    progressLiveData.postValue(false)
                }

    }

    @Throws(IOException::class)
    fun getBytesFromInputStream(inputStream: InputStream): ByteArray? {
        return try {
            val buffer = ByteArray(8192)
            var bytesRead: Int
            val output = ByteArrayOutputStream()
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.toByteArray()
        } catch (error: OutOfMemoryError) {
            null
        }
    }

}
