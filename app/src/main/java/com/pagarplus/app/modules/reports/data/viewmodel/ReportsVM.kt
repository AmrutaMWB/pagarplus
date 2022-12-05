package com.pagarplus.app.modules.reports.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pagarplus.app.modules.reports.data.model.ReportsModel
import org.koin.core.KoinComponent

class ReportsVM : ViewModel(), KoinComponent {
  val reportsModel: MutableLiveData<ReportsModel> = MutableLiveData(ReportsModel())

  var navArguments: Bundle? = null

 /* val spinnerAttendanceList: MutableLiveData<MutableList<SpinnerAttendanceModel>> =
      MutableLiveData()*/
}
