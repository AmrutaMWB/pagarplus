package com.pagarplus.app.modules.createbranch.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.createbranch.data.model.CreateBranchModel
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.models.createcreateBranch.CreateCreateBranchRequest
import com.pagarplus.app.network.models.createcreateBranch.CreateCreateBranchResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlin.Boolean
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class CreateBranchVM : ViewModel(), KoinComponent {
  val createBranchModel: MutableLiveData<CreateBranchModel> = MutableLiveData(CreateBranchModel())

  var navArguments: Bundle? = null

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  private val profile=prefs.getProfileDetails<CreateCreateEmployeeRequest>()

  val createCreateBranchLiveData: MutableLiveData<Response<CreateCreateBranchResponse>> =
      MutableLiveData<Response<CreateCreateBranchResponse>>()
  val userdetails = prefs.getLoginDetails<LoginResponse>()

  fun callCreateCreateBranchApi() {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      createCreateBranchLiveData.postValue(
      networkRepository.createCreateBranch(
      contentType = """application/json""",
          createCreateBranchRequest = CreateCreateBranchRequest(
          address = createBranchModel.value?.etEdtTxtBranchadrValue,
          state = createBranchModel.value?.etEdtTxtStateValue,
          city = createBranchModel.value?.etEdtTxtCityValue,
          orgId = userdetails?.orgID,
          name = createBranchModel.value?.etEdtTxtBranchnameValue,
          latitude = createBranchModel.value?.LatitudeValue,
          longitude = createBranchModel.value?.LongitudeValue)
      )
      )
      progressLiveData.postValue(false)
    }
  }

  fun bindCreateCreateBranchResponse(response: CreateCreateBranchResponse) {
    val createBranchModelValue = createBranchModel.value ?:CreateBranchModel()
    createBranchModel.value = createBranchModelValue
  }
}
