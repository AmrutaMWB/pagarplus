package com.pagarplus.app.modules.createemployee.data.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractTimeto24
import com.pagarplus.app.modules.createemployee.data.model.CreateEmployeeModel
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeResponse
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlin.Boolean
import kotlin.Unit
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class CreateEmployeeVM : ViewModel(), KoinComponent {
  val createEmployeeModel: MutableLiveData<CreateEmployeeModel> =
      MutableLiveData(CreateEmployeeModel())

  var navArguments: Bundle? = null

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  val createCreateEmployeeLiveData: MutableLiveData<Response<CreateCreateEmployeeResponse>> =
      MutableLiveData<Response<CreateCreateEmployeeResponse>>()

  val FrontimageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

  val BackimageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

  val userdetails = prefs.getLoginDetails<LoginResponse>()

  val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  /*image upload api*/
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

  fun callCreateCreateEmployeeApi(): Unit {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      createCreateEmployeeLiveData.postValue(
      networkRepository.createCreateEmployee(
      contentType = """application/json""",
      createCreateEmployeeRequest = CreateCreateEmployeeRequest(
        departmentID = createEmployeeModel.value?.txtDeptId,
        branchID = createEmployeeModel.value?.txtBranchId,
        password = createEmployeeModel.value?.etEdtTxtPwdValue,
        confirmPassword = createEmployeeModel.value?.etEdtTxtcnfPwdValue,
        email = createEmployeeModel.value?.etEdtTxtemailValue,
        adminID = userdetails?.userID,
        firstName = createEmployeeModel.value?.etEdtTxtFirstnameValue,
        lastName = createEmployeeModel.value?.etEdtTxtLastnameValue,
        orgID = userdetails?.orgID,
        mobileNumber = createEmployeeModel.value?.etEdtTxtmobileNoValue,
        loanAdvance = createEmployeeModel.value?.loan_advance,
        salaryBalance = createEmployeeModel.value?.old_salary_balance,
        deduction = createEmployeeModel.value?.monthly_deduction,
        comment = createEmployeeModel.value?.comment_on_balance,
        noOfSickLeave = createEmployeeModel.value?.etEdtTxtSickleaveValue,
        basicSalary = createEmployeeModel.value?.etEdtTxtSalaryValue,
        noOfPaidLeave = createEmployeeModel.value?.etEdtTxtPaidleaveValue,
        designation = createEmployeeModel.value?.etEdtTxtdesignation,
        dateOfJoining = createEmployeeModel.value?.etEdtTxtdatofjoining,
        proofs = createEmployeeModel.value?.Prooflist,
        stateID = createEmployeeModel.value?.StateID,
        cityID = createEmployeeModel.value?.cityID,
        address = createEmployeeModel.value?.etEdtTxtAddressValue,
        emergencyNumber = createEmployeeModel.value?.etEdtTxtEmergencynum,
        bloodGroup = createEmployeeModel.value?.etEdtTxtBloodgroup,
        officeStartTime = createEmployeeModel.value?.etEdtTxtcheckintime?.extractTimeto24(),
        officeEndTime = createEmployeeModel.value?.etEdtTxtcheckouttime?.extractTimeto24())
      )
      )
      progressLiveData.postValue(false)
    }
  }

  fun bindCreateCreateEmployeeResponse(response: CreateCreateEmployeeResponse): Unit {
    val createEmployeeModelValue = createEmployeeModel.value ?: CreateEmployeeModel()
    createEmployeeModel.value = createEmployeeModelValue
  }
}
