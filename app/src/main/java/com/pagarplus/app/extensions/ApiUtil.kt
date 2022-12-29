package com.pagarplus.app.extensions

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponse
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponseListItem
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponse
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponseListItem
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.createsignup.GetStateListItem
import com.pagarplus.app.network.models.createsignup.StateListResponse
import com.pagarplus.app.network.models.expense.ExpenseItem
import com.pagarplus.app.network.models.expense.SubExpenseItem
import com.pagarplus.app.network.models.fetchgetbranchlist.FetchGetBranchListResponseListItem
import com.pagarplus.app.network.models.fetchgetdepartmentlist.FetchGetDepartmentListResponseListItem
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponseListItem
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.Response
import com.pagarplus.app.network.resources.SuccessResponse
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ApiUtil(var context: Context):KoinComponent
{
    private val prefs: PreferenceHelper by inject()
    private val networkRepository: NetworkRepository by inject()

  suspend fun getExpenseTypes():ArrayList<ExpenseItem> {

          val list = arrayListOf<ExpenseItem>()

          val response = networkRepository.fetchExpenseTypes("bearer ${prefs.getAccess_token()}")
          if (response is SuccessResponse) {
              response.getContentIfNotHandled()
              response.data.expenseList?.let { list.addAll(it) }
          } else if (response is ErrorResponse) {
              Log.e("ApiUtil", response.message)
          }
          return list
  }

    suspend fun getSubExpenseTypes():ArrayList<SubExpenseItem> {

        val list = arrayListOf<SubExpenseItem>()

        val  response = networkRepository.fetchSubExpenseTypes("bearer ${prefs.getAccess_token()}")
        if (response is SuccessResponse) {
            response.getContentIfNotHandled()
            response.data.subExpenseList?.let { list.addAll(it) }
        } else if (response is ErrorResponse) {
            Log.e("ApiUtil", response.message)
        }
        return list
    }

 suspend fun getFeatureTypes(FeatureName:String):ArrayList<FeaturesTypes> {
        val list = arrayListOf<FeaturesTypes>()
        val  response = networkRepository.fetchFeatureTypes(FeatureName)
        if (response is SuccessResponse) {
            response.getContentIfNotHandled()
            response.data.list?.let { list.addAll(it) }
        } else if (response is ErrorResponse) {
            Log.e("ApiUtil", response.message)
        }
        return list
    }

    suspend fun getVisitTypes(UserId:Int):ArrayList<FeaturesTypes> {
        val list = arrayListOf<FeaturesTypes>()
        val  response = networkRepository.fetchVisitTypes(UserId)
        if (response is SuccessResponse) {
            response.getContentIfNotHandled()
            response.data.list?.let { list.addAll(it) }
        } else if (response is ErrorResponse) {
            Log.e("ApiUtil", response.message)
        }
        return list
    }

    suspend fun getHolidayTypes():ArrayList<FeaturesTypes> {
        val list = arrayListOf<FeaturesTypes>()
        val  response = networkRepository.getHolidayTypes()
        if (response is SuccessResponse) {
            response.getContentIfNotHandled()
            response.data.let { list.addAll(it) }
        } else if (response is ErrorResponse) {
            Log.e("ApiUtil", response.message)
        }
        return list
    }

    /*committed by amruta*/
    /*api method to get branch list and store in array*/
    suspend fun getBranchList(orgId: Int?):ArrayList<GetStateListItem> {

        val list = arrayListOf<GetStateListItem>()

        val response = networkRepository.fetchGetBranchList(orgId)
        if (response is SuccessResponse) {
            response.getContentIfNotHandled()
            response.data.stateList?.let { list.addAll(it) }
        } else if (response is ErrorResponse) {
            Log.e("ApiUtil", response.message)
        }
        return list
    }

    /*api method to get dept list and store in array*/
    suspend fun getDeptList():ArrayList<FetchGetDepartmentListResponseListItem> {

        val list = arrayListOf<FetchGetDepartmentListResponseListItem>()

        val response = networkRepository.fetchGetDepartmentList()
        if (response is SuccessResponse) {
            response.getContentIfNotHandled()
            response.data.deptList?.let { list.addAll(it) }
        } else if (response is ErrorResponse) {
            Log.e("ApiUtil", response.message)
        }
        return list
    }

    /*api method to get proof list and store in array*/
    suspend fun getIDProofList():ArrayList<FetchGetIDProofListResponseListItem> {

        val list = arrayListOf<FetchGetIDProofListResponseListItem>()

        val response = networkRepository.fetchGetIDProofList()
        if (response is SuccessResponse) {
            response.getContentIfNotHandled()
            response.data.proofList?.let { list.addAll(it) }
        } else if (response is ErrorResponse) {
            Log.e("ApiUtil", response.message)
        }
        return list
    }

    suspend fun setProfileDetails(Id:Int):CreateCreateEmployeeRequest {
        var profile=CreateCreateEmployeeRequest()
        try {
            val response = networkRepository.getEmployeeProfile(Id)
            if (response is SuccessResponse) {
                response.getContentIfNotHandled()
                if (response.data.status==true) {
                    profile= response.data.list?.get(0) ?:CreateCreateEmployeeRequest()
                    prefs.setProfileDetails(profile)
                }else
                    prefs.setProfileDetails(profile)

            } else if (response is ErrorResponse) {
                Log.e("ApiUtil", response.message)
            }
        }catch (e:Exception){
            Log.e("ApiUtil", e.message?:"")
        }
      return  profile
    }
}