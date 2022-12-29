package com.pagarplus.app.modules.reports.`data`.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.reports.data.model.ReportsModel
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import org.koin.core.KoinComponent
import org.koin.core.inject

class ReportsVM : ViewModel(), KoinComponent {
  val reportsModel: MutableLiveData<ReportsModel> = MutableLiveData(ReportsModel())

  var navArguments: Bundle? = null
}
