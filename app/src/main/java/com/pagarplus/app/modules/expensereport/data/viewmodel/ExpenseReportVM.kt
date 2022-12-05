package com.pagarplus.app.modules.expensereport.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.modules.expensereport.data.model.ExpenseRatingItem
import com.pagarplus.app.modules.expensereport.data.model.ExpenseReportModel
import com.pagarplus.app.network.models.expense.ExpenseReportRequest
import com.pagarplus.app.network.models.expense.ExpenseReportResponse
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import com.pagarplus.app.network.models.expense.ExpenseStatusRequest
import com.pagarplus.app.network.models.others.ApiResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response

import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.math.BigDecimal
import java.text.DecimalFormat


public class ExpenseReportVM : ViewModel(), KoinComponent {
  public val expenseReportModel: MutableLiveData<ExpenseReportModel> = MutableLiveData(
      ExpenseReportModel()
  )
  public val recyclerView1List: MutableLiveData<MutableList<ExpenseRowModel>> = MutableLiveData(mutableListOf())
  public val mainRecyclerList: MutableLiveData<MutableList<ExpenseRowModel>> = MutableLiveData(mutableListOf())
  public val userwiseExpenseList: MutableLiveData<MutableList<ExpenseRowModel>> = MutableLiveData(mutableListOf())
  public val recyclerExpenseRatingList: MutableLiveData<MutableList<ExpenseRatingItem>> = MutableLiveData(mutableListOf())
    public val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
     val networkRepository: NetworkRepository by inject()
    public val expenseReportListLiveData: MutableLiveData<Response<ExpenseReportResponse>> = MutableLiveData<Response<ExpenseReportResponse>>()
    public val approveExpenseLiveData: MutableLiveData<Response<ApiResponse>> = MutableLiveData<Response<ApiResponse>>()
    public val rejectExpenseLiveData: MutableLiveData<Response<ApiResponse>> = MutableLiveData<Response<ApiResponse>>()

    fun getExpenseList(request: ExpenseReportRequest){
        viewModelScope.launch {
            progressLiveData.postValue(true)
            expenseReportListLiveData.postValue(networkRepository.getExpenseReportList( request))
            progressLiveData.postValue(false)
        }
    }
    fun approveExpense(request: ExpenseStatusRequest){
        viewModelScope.launch {
            progressLiveData.postValue(true)
            approveExpenseLiveData.postValue(networkRepository.approveExpense( request))
            progressLiveData.postValue(false)
        }
    }

    fun rejectExpense(request: ExpenseStatusRequest){
        viewModelScope.launch {
            progressLiveData.postValue(true)
            rejectExpenseLiveData.postValue(networkRepository.rejectExpense( request))
            progressLiveData.postValue(false)
        }
    }

    public fun bindExpensesReportResponse(responseData: ArrayList<ExpenseRowModel>): Unit {
        val expenseReportModelValue = expenseReportModel.value ?: ExpenseReportModel()
        val recyclerView = responseData.sortedByDescending { it.ExpenseDateTime?:"" }.toMutableList()

        var totalExpense=recyclerView.sumOf { it.Amount?:0 }.toDouble()
        val Format = DecimalFormat("##.00")
        recyclerView.forEach {
            it.ExpenseDateTime=it.ExpenseDateTime?.extractDate()
            it.AmountValue="₹ ${it.Amount}"
            if(it.SubExpenseTypeID==0)
                it.SubExpenseTypeName=""
        }
        val secondarylist=recyclerView
        mainRecyclerList.value=recyclerView
        recyclerView1List.value = secondarylist.toMutableList()

        val userWiseExpense=responseData.groupBy { it.EmployeeID?:0 }.map{
           ExpenseRowModel(
               EmployeeID = it.value[0].EmployeeID,
               EmployeeName = it.value[0].EmployeeName,
               AmountValue = "₹${it.value.sumOf { it.Amount?:0 }}",
               ProfileImgUrl = it.value[0].ProfileImgUrl)
        }.toMutableList()

        userwiseExpenseList.value=userWiseExpense
        val expenseRatingList=responseData.groupBy { it.ExpenseTypeID }.map {
            val amount=it.value.sumOf { it.Amount?:0 }
            val percentage=if(amount>0)(amount.div(totalExpense)*100) else 0.0
            ExpenseRatingItem(
                ExpenseLabel = it.value[0].ExpenseTypeName?:"",
                ExpenseAmount = "₹$amount",
                ExpensePercentageVal =percentage.toInt(),
                ExpensePercentage = "${Format.format(BigDecimal(percentage))}%",
                ExpenseImage = it.value[0].Icon?:"",
                ExpenseId = it.value[0].ExpenseTypeID?:0)
        }.toMutableList()
        expenseReportModelValue.TotalExpense="Total Expense : ₹$totalExpense"


        recyclerExpenseRatingList.value=expenseRatingList
        expenseReportModel.value = expenseReportModelValue
    }

    fun bindExpensesBasedOnType(responseData: ArrayList<ExpenseRowModel>,ExpenseTypeName:String){
        val expenseReportModelValue = expenseReportModel.value ?: ExpenseReportModel()
          var recyclerView= responseData.sortedBy { it.EmployeeName?:"" }.toMutableList()

        var totalExpense=recyclerView.sumOf { it.Amount?:0 }.toDouble()
        val Format = DecimalFormat("##.00")
        recyclerView.forEach {
            it.ExpenseDateTime=it.ExpenseDateTime?.extractDate()
            it.AmountValue="₹ ${it.Amount}"
            if(it.SubExpenseTypeID==0)
                it.SubExpenseTypeName=""
        }
        val secondarylist=recyclerView
        mainRecyclerList.value=recyclerView
        recyclerView1List.value = secondarylist.toMutableList()
        expenseReportModelValue.TotalExpense="Total $ExpenseTypeName : ₹$totalExpense"
        expenseReportModel.value = expenseReportModelValue
    }


    fun filterExpenseList(ExpenseId:Int?){
       val recycler_list= arrayListOf<ExpenseRowModel>()
        mainRecyclerList.value?.forEach {
            if(it.ExpenseTypeID==ExpenseId)
                recycler_list.add(it)
        }
        recyclerView1List.value=recycler_list.toMutableList()


    }
    fun filterExpenseListByStatus(ExpenseStatus:String){
        var recycler_list= arrayListOf<ExpenseRowModel>()
        if(ExpenseStatus.isNotEmpty()){
            recycler_list=mainRecyclerList.value?.filter { it.Status==ExpenseStatus } as ArrayList<ExpenseRowModel>
        }else
            recycler_list=mainRecyclerList.value as ArrayList<ExpenseRowModel>

        recyclerView1List.value=recycler_list.toMutableList()


    }


}
