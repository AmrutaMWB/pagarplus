package com.pagarplus.app.modules.attedence.data.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.network.models.attendance.AttendanceStatusResponse
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.attendance.SaveAttendanceDataRequest
import com.pagarplus.app.network.models.attendance.SaveAttendanceDataResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

public class AttedenceVM : ViewModel(), KoinComponent {
  public var navArguments: Bundle? = null
  public val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  private val networkRepository: NetworkRepository by inject()
  public val saveattendanceLiveData: MutableLiveData<Response<SaveAttendanceDataResponse>> =
    MutableLiveData<Response<SaveAttendanceDataResponse>>()
  val imageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

  public val attendanceStatusLiveData: MutableLiveData<Response<AttendanceStatusResponse>> =
    MutableLiveData<Response<AttendanceStatusResponse>>()
  private val prefs: PreferenceHelper by inject()

  fun imageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      imageUploadLiveData.postValue(
        networkRepository.uploadFile(folder,uri,imgsize)
      )
      progressLiveData.postValue(false)
    }
  }

  public fun saveattendanceVM(createSaveAttendencetDataRequest: SaveAttendanceDataRequest): Unit {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      saveattendanceLiveData.postValue(networkRepository.saveattendance(createSaveAttendencetDataRequest))
      progressLiveData.postValue(false)
    }
  }
  fun getAttendanceStatus(employeeId:Int,date:String) {
    viewModelScope.launch {
     // progressLiveData.postValue(true)
      attendanceStatusLiveData.postValue(
        networkRepository.fetchAttendanceStatus(employeeId,date))
    //  progressLiveData.postValue(false)
    }
  }


}
