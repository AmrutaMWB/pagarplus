package com.pagarplus.app.modules.admindashboard.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.admindashboard.data.model.AdmindashboardModel
import com.pagarplus.app.modules.admindashboard.data.model.DrawerItemAdminmenuModel
import com.pagarplus.app.modules.createemployee.ui.CreateEmployeeActivity
import com.pagarplus.app.modules.editemployeedetails.data.model.EditemployeedetailsModel
import com.pagarplus.app.network.models.AdminDashboard.FetchAdminDashboardListResponse
import com.pagarplus.app.network.models.AdminDashboard.FetchDashboardListResponseListItem
import com.pagarplus.app.network.models.AdminDashboard.toAdmindashboardModel
import com.pagarplus.app.network.models.adminEditEmpdata.AdminEditEmployeeResponse
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItem
import com.pagarplus.app.network.models.adminEditEmpdata.UpdateEmployeeResponse
import com.pagarplus.app.network.models.adminEditEmpdata.toEditemployeedetailsModel
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class AdmindashboardVM : ViewModel(), KoinComponent {
  val admindashboardModel: MutableLiveData<AdmindashboardModel> =
      MutableLiveData(AdmindashboardModel())

  var navArguments: Bundle? = null

    private val networkRepository: NetworkRepository by inject()

    private val prefs: PreferenceHelper by inject()
    var userdetails = prefs.getLoginDetails<LoginResponse>()
    var profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

    val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val LogoutLiveData: MutableLiveData<Response<String>> = MutableLiveData<Response<String>>()

    public var includedModel: MutableLiveData<DrawerItemAdminmenuModel> =
        MutableLiveData(DrawerItemAdminmenuModel())

    val fetchDashboardLiveData: MutableLiveData<Response<FetchAdminDashboardListResponse>> =
        MutableLiveData<Response<FetchAdminDashboardListResponse>>()

    fun callFetchAdminDashboardDataApi(seldate: String) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchDashboardLiveData.postValue(
                networkRepository.fetchAdminDashboardList(
                    adminID = userdetails?.userID,
                    seldate = seldate,
                    branchID = admindashboardModel.value?.txtBranchId
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindAdminDashboardResponse(response: FetchDashboardListResponseListItem): Unit {
        var geteditEmployeeModelValue = response.toAdmindashboardModel()
        admindashboardModel.value  = geteditEmployeeModelValue
    }

    /*logout api*/
    fun UserLogout(accessToken: String) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            LogoutLiveData.postValue(
                networkRepository.userLogout(userdetails?.userID!!)
            )
            progressLiveData.postValue(false)
        }
    }
}
