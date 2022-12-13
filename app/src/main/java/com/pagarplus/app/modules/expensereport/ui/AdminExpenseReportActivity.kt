package com.pagarplus.app.modules.expensereport.ui
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityAdminExpenseReportBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.expensereport.data.model.ExpenseRatingItem
import com.pagarplus.app.modules.expensereport.data.viewmodel.ExpenseReportVM
import com.pagarplus.app.network.models.expense.ExpenseReportRequest
import com.pagarplus.app.network.models.expense.ExpenseReportResponse
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
public class AdminExpenseReportActivity : BaseActivity<ActivityAdminExpenseReportBinding>(R.layout.activity_admin_expense_report) {
    private val viewModel: ExpenseReportVM by viewModels<ExpenseReportVM>()
    private var mEmployeeId:String?=""
    private var mAdminId:String?=""

    public override fun onInitialized(): Unit {
        binding.expenseReportVM = viewModel
        mAdminId=intent.getStringExtra(IntentParameters.AdminId)?:""
        sendGetExpenseListRequest()
        val recyclerView1Adapter = ExpenseReportAdapter(viewModel.userwiseExpenseList.value?: mutableListOf(), this,1)
        binding.recyclerUserListView.adapter = recyclerView1Adapter
        recyclerView1Adapter.setOnItemClickListener(
            object : ExpenseReportAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int, item: ExpenseRowModel) {
                    onClickRecyclerView1(view, position, item)
                }
            }
        )
        viewModel.userwiseExpenseList.observe(this) {
            recyclerView1Adapter.updateData(it as ArrayList<ExpenseRowModel>)
        }

        val recyclerRatingAdapter = ExpenseRatingAdapter(viewModel.recyclerExpenseRatingList.value?: mutableListOf(), this)
        binding.recyclerRatingView.adapter = recyclerRatingAdapter

        recyclerRatingAdapter.setOnItemClickListener(
            object : ExpenseRatingAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int, item: ExpenseRatingItem) {
                    val destIntent=ExpenseTypeWiseActivity.getIntent(this@AdminExpenseReportActivity,null)
                    destIntent.putExtra(IntentParameters.AdminId,mAdminId)
                    destIntent.putExtra(IntentParameters.ExpenseName,item.ExpenseLabel)
                    destIntent.putExtra(IntentParameters.ExpenseId,item.ExpenseId)
                    destIntent.putExtra(IntentParameters.FromDate,binding.fromDate.text.toString())
                    destIntent.putExtra(IntentParameters.ToDate,binding.toDate.text.toString())
                    startActivity(destIntent)
                }
            }
        )
        viewModel.recyclerExpenseRatingList.observe(this) {
            recyclerRatingAdapter.updateData(it?: mutableListOf())

        }


    }


    fun sendGetExpenseListRequest(){
        val expenseReportRequest=ExpenseReportRequest("",mEmployeeId.toString(),mAdminId.toString(),
            viewModel.expenseReportModel.value?.txtFromDate,
            viewModel.expenseReportModel.value?.txtToDate)
        viewModel.getExpenseList(expenseReportRequest)
    }

    public override fun setUpClicks(): Unit {
        var model=viewModel.expenseReportModel.value

        binding.fromDate.setOnClickListener {
            val destinationInstance = DatePickerFragment.getInstance(false,model?.txtFromDate?.getDate("dd-MM-yyyy"))

            destinationInstance.show(this.supportFragmentManager, DatePickerFragment.TAG) { selectedDate ->
                if(selectedDate<=viewModel.expenseReportModel.value?.txtToDate?.getDate())
                {
                    binding.fromDate.text=SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
                    sendGetExpenseListRequest()
                }else
                    Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()

            }

        }
        binding.toDate.setOnClickListener {
            Log.e("Dates","FromDate:${model?.txtFromDate}..ToDate:${model?.txtToDate}")

            val destinationInstance = DatePickerFragment.getInstance(false,viewModel.expenseReportModel.value?.txtToDate?.getDate())
            destinationInstance.show(this.supportFragmentManager, DatePickerFragment.TAG) { selectedDate ->

                if(selectedDate>=viewModel.expenseReportModel.value?.txtFromDate?.getDate("dd-MM-yyyy")) {
                    binding.toDate.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
                    sendGetExpenseListRequest()
                }
                else
                    Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_date), Snackbar.LENGTH_LONG).show()
            }

        }
        binding.btnBack.setOnClickListener {
            finish()
        }

    }
    public fun onClickRecyclerView1(view: View, position: Int, item: ExpenseRowModel): Unit {
        val destIntent=ExpenseReportActivity.getIntent(this,null)
        destIntent.putExtra(IntentParameters.AdminId,mAdminId)
        destIntent.putExtra(IntentParameters.UserId,item.EmployeeID.toString())
        destIntent.putExtra(IntentParameters.isAdmin,true)
        destIntent.putExtra(IntentParameters.FromDate,binding.fromDate.text.toString())
        destIntent.putExtra(IntentParameters.ToDate,binding.toDate.text.toString())
        startActivity(destIntent)

    }

    public override fun addObservers(): Unit {
        var progressDialog: AlertDialog? = null
        viewModel.progressLiveData.observe(this@AdminExpenseReportActivity) {

            if (it) {
                progressDialog?.dismiss()
                progressDialog = null
                progressDialog = this@AdminExpenseReportActivity.showProgressDialog()

            } else {

                progressDialog?.dismiss()

            }

        }
        viewModel.expenseReportListLiveData.observe(this@AdminExpenseReportActivity) {

            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                onSuccessExpensesreport(it)

            } else if (it is ErrorResponse) {
                onError(it.data ?: Exception())
            }

        }

    }

    private fun onError(exception: Exception) {
        when (exception) {
            is
            NoInternetConnection -> {
                Snackbar.make(binding.root, exception.message ?: "", Snackbar.LENGTH_LONG).show()
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()?.string()
                val errorObject = if (errorBody != null && errorBody.isJSONObject())
                    JSONObject(errorBody)
                else JSONObject()

                Snackbar.make(binding.root, MyApp.getInstance().getString(R.string.lbl_error), Snackbar.LENGTH_LONG).show()
            }
        }

    }


    private fun onSuccessExpensesreport(it: SuccessResponse<ExpenseReportResponse>) {
        if (it.data.status==true) {
            if(!it.data.expenseList.isNullOrEmpty())
                viewModel.bindExpensesReportResponse(it.data.expenseList?: arrayListOf())
            else{
                Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_no_expense), Snackbar.LENGTH_LONG).show()
                binding.txtTotalExpense.text=MyApp.getInstance().resources.getString(R.string.lbl_no_expense)
            }
        } else{
            Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
            binding.txtTotalExpense.text=it.data.message
        }


    }
    public companion object {
        public const val TAG: String = "AdminExpensesReport_Activity"
        public fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, AdminExpenseReportActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}
