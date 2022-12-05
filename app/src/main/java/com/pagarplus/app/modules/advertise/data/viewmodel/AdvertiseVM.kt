package com.pagarplus.app.modules.advertise.`data`.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.advertise.data.model.AdvertiseModel
import com.pagarplus.app.modules.advertise.data.model.AdvertiseRowModel
import com.pagarplus.app.modules.notification.data.model.MessageRowModel
import com.pagarplus.app.modules.notification.data.model.NotificationModel
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerRequest
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.notificationMsg.FetchInOutMsgListResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlin.Boolean
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

class AdvertiseVM : ViewModel(), KoinComponent {
  val advertiseModel: MutableLiveData<AdvertiseModel> = MutableLiveData(AdvertiseModel())

  var navArguments: Bundle? = null

  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  val createCreateBannerLiveData: MutableLiveData<Response<CreateCreateBannerResponse>> =
      MutableLiveData<Response<CreateCreateBannerResponse>>()

    val updateBannerLiveData: MutableLiveData<Response<CreateCreateBannerResponse>> =
        MutableLiveData<Response<CreateCreateBannerResponse>>()

    val advertiseList: MutableLiveData<MutableList<AdvertiseRowModel>> = MutableLiveData(mutableListOf())

    val getAllBannerLiveData: MutableLiveData<Response<CreateCreateBannerResponse>> =
        MutableLiveData<Response<CreateCreateBannerResponse>>()

    val activeBannerLiveData: MutableLiveData<Response<CreateCreateBannerResponse>> =
        MutableLiveData<Response<CreateCreateBannerResponse>>()

    val deactiveBannerLiveData: MutableLiveData<Response<CreateCreateBannerResponse>> =
        MutableLiveData<Response<CreateCreateBannerResponse>>()

  val imageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

  val userdetails = prefs.getLoginDetails<LoginResponse>()
  val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

    fun ImageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            imageUploadLiveData.postValue(
                networkRepository.uploadFile(folder,uri,imgsize)
            )
            progressLiveData.postValue(false)
        }
    }

  fun callCreateCreateBannerApi() {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      createCreateBannerLiveData.postValue(
      networkRepository.createCreateBanner(
      contentType = """application/json""",
          createCreateBannerRequest = CreateCreateBannerRequest(
          adminID = userdetails?.userID,
          description = advertiseModel.value?.txtDescription,
          bannerImage = advertiseModel.value?.txtImagePath,
          title = advertiseModel.value?.txtTitle,
          fromDate = advertiseModel.value?.txtfromdate,
          toDate = advertiseModel.value?.txttodate,
          branchID = advertiseModel.value?.txtBranchId,
          departmentId = advertiseModel.value?.txtDeptId)
        )
      )
      progressLiveData.postValue(false)
    }
  }

  fun bindCreateCreateBannerResponse(response: CreateCreateBannerResponse) {
    val advertiseModelValue = advertiseModel.value ?:AdvertiseModel()
    advertiseModel.value = advertiseModelValue
  }

    /*update banner api*/
    fun callUpdateBannerApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            updateBannerLiveData.postValue(
                networkRepository.updateBanner(
                    contentType = """application/json""",
                    createCreateBannerRequest = CreateCreateBannerRequest(
                        adminID = userdetails?.userID,
                        bannerID = advertiseModel.value?.bannerId,
                        description = advertiseModel.value?.txtDescription,
                        bannerImage = advertiseModel.value?.txtImagePath,
                        title = advertiseModel.value?.txtTitle,
                        fromDate = advertiseModel.value?.txtfromdate,
                        toDate = advertiseModel.value?.txttodate,
                        branchID = advertiseModel.value?.txtBranchId,
                        departmentId = advertiseModel.value?.txtDeptId)
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callDeactiveAdvertiseApi(bannerID: Int) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            deactiveBannerLiveData.postValue(
                networkRepository.deleteAdvertise(
                    adminID = userdetails?.userID,
                    bannerID = bannerID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callActiveAdvertiseApi(bannerID: Int) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            activeBannerLiveData.postValue(
                networkRepository.activeAdvertise(
                    adminID = userdetails?.userID,
                    bannerID = bannerID
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindActiveDeactiveBannerResponse(response: CreateCreateBannerResponse) {
        val advertiseModelValue = advertiseModel.value ?:AdvertiseModel()
        advertiseModel.value = advertiseModelValue
    }

    /*fetch all the created banners*/
    fun callFetchAllAdvertiseListApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            getAllBannerLiveData.postValue(
                networkRepository.advertiseList(
                    adminID = userdetails?.userID,
                    seldate = advertiseModel.value?.seldate,
                    branchID = advertiseModel.value?.txtBranchId,
                    deptID = advertiseModel.value?.txtDeptId
                )
            )
            progressLiveData.postValue(false)
        }
    }

    /*bind all value in list*/
    fun bindFetchAllAdvertiseListResponse(response: CreateCreateBannerResponse) {
        // Read the value until the minutes only
        val pattern = "dd/MM/yyyy HH:mm:ss"
        val serverDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        val userPattern = "dd-MM-yyyy"
        val userDateFormat = SimpleDateFormat(userPattern, Locale.getDefault())

        val advertiseModelValue = advertiseModel.value ?: AdvertiseModel()
        val recyclerMsglist = response.advertiseList?.map {

            val fromDate = serverDateFormat.parse(it?.fromDate!!)
            val toDate = serverDateFormat.parse(it?.toDate!!)

            val advfromdate = userDateFormat.format(fromDate)
            val advtodate = userDateFormat.format(toDate)

            AdvertiseRowModel(
                txtAdminID = it?.adminID,
                txtMessage = it?.description,
                txtFromDate = advfromdate,
                txtToDate = advtodate,
                txtImgpath = it?.bannerImage,
                txtBannerID = it?.bannerID,
                txtBranchId = it?.branchID,
                txtDeptID = it?.departmentId,
                txtBranch = it?.branch,
                txtDept = it?.department,
                txtActivestatus = it?.activeStatus,
                organizationname = profiledetails?.organization
            )
        }?.toMutableList()
        advertiseList.value = recyclerMsglist
        advertiseModel.value = advertiseModelValue
    }
}
