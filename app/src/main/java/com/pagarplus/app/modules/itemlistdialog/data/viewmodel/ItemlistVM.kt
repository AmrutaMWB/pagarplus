package com.pagarplus.app.modules.itemlistdialog.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.itemlistdialog.data.model.ItemlistModel
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.createsignup.GetStateListItem
import com.pagarplus.app.network.models.createsignup.StateListResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.collections.MutableList

public class ItemlistVM : ViewModel(),KoinComponent{
  public val itemlistModel: MutableLiveData<ItemlistModel> = MutableLiveData(ItemlistModel())

  public var navArguments: Bundle? = null

    private val prefs: PreferenceHelper by inject()
    private val networkRepository: NetworkRepository by inject()
    public val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    var userDetails=prefs.getLoginDetails<LoginResponse>()

    var profileDetails=prefs.getProfileDetails<CreateCreateEmployeeRequest>()

    /*statelist in recyclerview dialog*/
    val StateList: MutableLiveData<MutableList<Itemlistdialog1RowModel>> = MutableLiveData(mutableListOf())

    val CityList: MutableLiveData<MutableList<Itemlistdialog1RowModel>> = MutableLiveData(mutableListOf())

    val BranchList: MutableLiveData<MutableList<Itemlistdialog1RowModel>> = MutableLiveData(mutableListOf())

    val DeptList: MutableLiveData<MutableList<Itemlistdialog1RowModel>> = MutableLiveData(mutableListOf())

    val fetchStateLiveData: MutableLiveData<Response<StateListResponse>> =
        MutableLiveData<Response<StateListResponse>>()

    val fetchCityLiveData: MutableLiveData<Response<StateListResponse>> =
        MutableLiveData<Response<StateListResponse>>()

    val fetchBranchLiveData: MutableLiveData<Response<StateListResponse>> =
        MutableLiveData<Response<StateListResponse>>()

    val fetchDeptLiveData: MutableLiveData<Response<StateListResponse>> =
        MutableLiveData<Response<StateListResponse>>()

    fun callFetchStateListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchStateLiveData.postValue(
                networkRepository.fetchStateList()
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchStateListResponse(response: StateListResponse) {

        val statelistModelValue = itemlistModel.value ?: ItemlistModel()
        val recyclerMsglist = response.stateList?.map {
            Itemlistdialog1RowModel(
                txtName = it?.text,
                txtValue = it?.value,
                isState = true
            )
        }?.toMutableList()
        StateList.value = recyclerMsglist
        itemlistModel.value = statelistModelValue
    }

    fun callFetchCityListApi(stateID:Int, keyword: String) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchCityLiveData.postValue(
                networkRepository.fetchCityList(
                    stateID = stateID,
                    keyword = keyword
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchCityListResponse(response: StateListResponse) {

        val statelistModelValue = itemlistModel.value ?: ItemlistModel()
        val recyclerMsglist = response.stateList?.map {
            Itemlistdialog1RowModel(
                txtName = it?.text,
                txtValue = it?.value,
                isState = false
            )
        }?.toMutableList()
        CityList.value = recyclerMsglist
        itemlistModel.value = statelistModelValue
    }

    /*fetch branch list api*/
    fun callFetchBranchListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchBranchLiveData.postValue(
                networkRepository.fetchGetBranchList(
                    orgID = userDetails?.orgID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchBranchListResponse(response: StateListResponse) {
        val statelistModelValue = itemlistModel.value ?: ItemlistModel()
        val recyclerMsglist = response.stateList?.map {
            Itemlistdialog1RowModel(
                txtName = it?.text,
                txtValue = it?.value,
                isBranch = true,
            )
        }?.toMutableList()
        BranchList.value = recyclerMsglist
        itemlistModel.value = statelistModelValue
    }

    /*fetch branch list api*/
    fun callFetchDeptListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            fetchDeptLiveData.postValue(
                networkRepository.fetchGetAdminDeptList(
                    adminID = userDetails?.userID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindFetchDeptListResponse(response: StateListResponse) {
        val statelistModelValue = itemlistModel.value ?: ItemlistModel()
        val recyclerMsglist = response.deptList?.map {
            Itemlistdialog1RowModel(
                txtName = it?.text,
                txtValue = it?.value,
                isBranch = false,
            )
        }?.toMutableList()
        DeptList.value = recyclerMsglist
        itemlistModel.value = statelistModelValue
        DeptList.value?.add(0,Itemlistdialog1RowModel("All Department",0,false,false,false))
        DeptList.value?.add(1,Itemlistdialog1RowModel("No Department",-1,false,false,false))
    }
}
