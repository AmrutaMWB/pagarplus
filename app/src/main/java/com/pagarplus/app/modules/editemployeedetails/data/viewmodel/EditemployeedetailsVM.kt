package com.pagarplus.app.modules.editemployeedetails.`data`.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractTimeto24
import com.pagarplus.app.modules.editemployeedetails.data.model.EditemployeedetailsModel
import com.pagarplus.app.network.models.adminEditEmpdata.*
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class EditemployeedetailsVM : ViewModel(), KoinComponent {
    val editemployeedetailsModel: MutableLiveData<EditemployeedetailsModel> =
      MutableLiveData(EditemployeedetailsModel())

    var navArguments: Bundle? = null

    private val prefs: PreferenceHelper by inject()

    val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    private val networkRepository: NetworkRepository by inject()

    val fetchGetEmpLiveData: MutableLiveData<Response<AdminEditEmployeeResponse>> =
        MutableLiveData<Response<AdminEditEmployeeResponse>>()

    val updateEmpLiveData: MutableLiveData<Response<UpdateEmployeeResponse>> =
        MutableLiveData<Response<UpdateEmployeeResponse>>()

    val userdetails = prefs.getLoginDetails<LoginResponse>()

    val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

    val FrontimageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

    val BackimageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

    fun FrontimageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            FrontimageUploadLiveData.postValue(
                networkRepository.uploadFile(folder,uri,imgsize)
            )
            progressLiveData.postValue(false)
        }
    }

    fun BackimageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            BackimageUploadLiveData.postValue(
                networkRepository.uploadFile(folder,uri,imgsize)
            )
            progressLiveData.postValue(false)
        }
    }
    fun callFetchGetEmpDataApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchGetEmpLiveData.postValue(
                networkRepository.admingetEditEmpData(
                    empID = editemployeedetailsModel.value?.empID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callUpdateEmployeeApi(): Unit {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            updateEmpLiveData.postValue(
                networkRepository.adminUpdateEmpData(
                    empID = editemployeedetailsModel.value?.txtEmpId,
                    contentType = """application/json""",
                    adminEditEmployeeRequest = AdminEditEmployeeRequest(
                        empid = editemployeedetailsModel.value?.txtEmpId,
                        adminID = userdetails?.userID,
                        email = editemployeedetailsModel.value?.etEdtTxtemailValue,
                        firstName = editemployeedetailsModel.value?.etEdtTxtFirstnameValue,
                        lastName = editemployeedetailsModel.value?.etEdtTxtLastnameValue,
                        noOfSickLeave = editemployeedetailsModel.value?.etEdtTxtSickleaveValue,
                        basicSalary = editemployeedetailsModel.value?.etEdtTxtSalaryValue,
                        noOfPaidLeave = editemployeedetailsModel.value?.etEdtTxtPaidleaveValue,
                        proofs = editemployeedetailsModel.value?.Prooflist,
                        branchID = editemployeedetailsModel.value?.txtBranchId,
                        departmentID = editemployeedetailsModel.value?.txtDeptId,
                        designation = editemployeedetailsModel.value?.etEdtTxtdesignation,
                        dateOfJoining = editemployeedetailsModel.value?.etEdtTxtdatofjoining,
                        mobileNumber = editemployeedetailsModel.value?.etEdtTxtmobileNoValue,
                        emergencyNumber = editemployeedetailsModel.value?.etEdtTxtEmergencynum,
                        bloodGroup = editemployeedetailsModel.value?.etEdtTxtBloodgroup,
                        officeStartTime = editemployeedetailsModel.value?.etEdtTxtcheckintime?.extractTimeto24(),
                        officeEndTime = editemployeedetailsModel.value?.etEdtTxtcheckouttime?.extractTimeto24(),
                        address = editemployeedetailsModel.value?.etEdtTxtaddress,
                        password = editemployeedetailsModel.value?.etEdtTxtPwdValue,
                        oldPassword = editemployeedetailsModel.value?.txtOldPassword)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindUpdateEmployeeResponse(response: UpdateEmployeeResponse): Unit {
        val updateEmployeeModelValue = editemployeedetailsModel.value ?: EditemployeedetailsModel()
        editemployeedetailsModel.value = updateEmployeeModelValue
    }

    fun bindEditEmployeeResponse(response: FetchEditEmployeeDetailsResponseListItem): Unit {
        var geteditEmployeeModelValue = editemployeedetailsModel.value ?: EditemployeedetailsModel()
        geteditEmployeeModelValue=response.toEditemployeedetailsModel()
        editemployeedetailsModel.value  = geteditEmployeeModelValue
    }
}
