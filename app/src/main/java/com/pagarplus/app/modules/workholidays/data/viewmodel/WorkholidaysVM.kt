package com.pagarplus.app.modules.workholidays.`data`.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.getMonthId
import com.pagarplus.app.modules.workholidays.data.model.WorkholidaysModel
import com.pagarplus.app.network.models.holiday.HolidayItem
import com.pagarplus.app.network.models.holiday.HolidayResponse
import com.pagarplus.app.network.models.holiday.SetHolidayRequest
import com.pagarplus.app.network.models.others.ApiResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class WorkholidaysVM : ViewModel(), KoinComponent {
  val workholidaysModel: MutableLiveData<WorkholidaysModel> = MutableLiveData(WorkholidaysModel())


  var navArguments: Bundle? = null


  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  public val mainrecyclerViewList: MutableLiveData<MutableList<HolidayItem>> =
    MutableLiveData(mutableListOf())
  public val recyclerViewList: MutableLiveData<MutableList<HolidayItem>> =
    MutableLiveData(mutableListOf())
  private val networkRepository: NetworkRepository by inject()

  private val prefs: PreferenceHelper by inject()

  val setHolidayLiveData: MutableLiveData<Response<ApiResponse>> =
      MutableLiveData<Response<ApiResponse>>()

  val deleteHolidayLiveData: MutableLiveData<Response<ApiResponse>> =
    MutableLiveData<Response<ApiResponse>>()


  val getHolidaysListLiveData: MutableLiveData<Response<HolidayResponse>> =
      MutableLiveData<Response<HolidayResponse>>()

  fun setHolidays(request: SetHolidayRequest) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      setHolidayLiveData.postValue(
      networkRepository.setHoliday(request))
      progressLiveData.postValue(false)
    }
  }

  fun deleteHolidays(id:Int) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      deleteHolidayLiveData.postValue(
        networkRepository.deleteHoliday(id))
      progressLiveData.postValue(false)
    }
  }
  fun getHolidaysList(AdminId:Int) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      getHolidaysListLiveData.postValue(
        networkRepository.getHolidaysList(AdminId.toString()))
      progressLiveData.postValue(false)
    }
  }

  fun bindHolidaysList(response: HolidayResponse,MonthId:Int) {
    val workholidaysModelValue = workholidaysModel.value ?:WorkholidaysModel()
    if(!response.holidays.isNullOrEmpty()){
     mainrecyclerViewList.value=response.holidays.toMutableList()
     filterHolidaysByMonth(MonthId)
    }
    workholidaysModel.value = workholidaysModelValue
  }
  fun filterHolidaysByMonth(monthId:Int) {
    try {
      var list= arrayListOf<HolidayItem>()
      for (holiday in mainrecyclerViewList.value as ArrayList) {
        if (!holiday.date.isNullOrEmpty()) {
          if (holiday.date?.getMonthId() == monthId)
            list.add(holiday)
        }
      }
      recyclerViewList.value=list.toMutableList()
    }catch (e:Exception){
      Log.e("Holiday",e.message.toString())
    }
  }

}
