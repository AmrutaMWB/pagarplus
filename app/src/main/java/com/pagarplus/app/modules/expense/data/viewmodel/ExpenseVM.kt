package com.pagarplus.app.modules.expense.`data`.viewmodel

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.modules.expense.`data`.model.ExpenseModel
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.expense.AddExpenseRequest
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlin.Boolean
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class ExpenseVM : ViewModel(), KoinComponent {
  val expenseModel: MutableLiveData<ExpenseModel> = MutableLiveData(ExpenseModel())
  var navArguments: Bundle? = null
  val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
  private val networkRepository: NetworkRepository by inject()
  val createAddExpenseLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()
  val imageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()

  fun imageUpload(folder:String,uri:Uri,imgsize: ByteArray) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      imageUploadLiveData.postValue(
        networkRepository.uploadFile(folder,uri,imgsize)
      )
      progressLiveData.postValue(false)
    }
  }

  fun addExpenseDetails(request: AddExpenseRequest) {
    viewModelScope.launch {
      progressLiveData.postValue(true)
      createAddExpenseLiveData.postValue(
      networkRepository.addExpenseDetails(request)
      )
      progressLiveData.postValue(false)
    }
  }


}
