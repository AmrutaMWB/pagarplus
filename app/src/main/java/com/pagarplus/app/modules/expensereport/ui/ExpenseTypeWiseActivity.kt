package com.pagarplus.app.modules.expensereport.ui
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityExpensetypeWiseReportBinding
import com.pagarplus.app.extensions.*
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


public class ExpenseTypeWiseActivity : BaseActivity<ActivityExpensetypeWiseReportBinding>(R.layout.activity_expensetype_wise_report) {
    val viewModel: ExpenseReportVM by viewModels<ExpenseReportVM>()
    private var mAdminId:String?=""
    private var mExpenseId:Int?=0
    private var mExpenseName:String?=""
    private lateinit var recyclerView1Adapter:ExpenseReportAdapter
    private  var destIntent:Intent?=null


    public override fun onInitialized(): Unit {
        binding.expenseReportVM = viewModel
        mExpenseId=intent.getIntExtra(IntentParameters.ExpenseId,0)
        mAdminId=intent.getStringExtra(IntentParameters.AdminId)?:""
        mExpenseName=intent.getStringExtra(IntentParameters.ExpenseName)?:""
        val fromdate=intent.getStringExtra(IntentParameters.FromDate)
        val todate=intent.getStringExtra(IntentParameters.ToDate)

        if(fromdate!=null && todate!=null){
            viewModel.expenseReportModel.value?.txtFromDate=fromdate
            viewModel.expenseReportModel.value?.txtToDate=todate
        }
        //sendGetExpenseListRequest()


         recyclerView1Adapter = ExpenseReportAdapter(viewModel.recyclerView1List.value?: mutableListOf(), this,2)
        binding.recyclerExpenseListView.adapter = recyclerView1Adapter
        recyclerView1Adapter.setOnItemClickListener(
            object : ExpenseReportAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int, item: ExpenseRowModel) {
                    onClickRecyclerView1(view, position, item)
                }
            }
        )

        setUpSearchViewListener()
        viewModel.recyclerView1List.observe(this) {
            recyclerView1Adapter.updateData(it as ArrayList<ExpenseRowModel>)
        }

        
    }

    override fun onResume() {
        super.onResume()
        sendGetExpenseListRequest()
    }


    fun sendGetExpenseListRequest(){
        val expenseReportRequest=ExpenseReportRequest(mExpenseId.toString(),"",mAdminId.toString(),
            viewModel.expenseReportModel.value?.txtFromDate,
            viewModel.expenseReportModel.value?.txtToDate)
        viewModel.getExpenseList(expenseReportRequest)
    }

    private fun setUpSearchViewListener(): Unit {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0 : String) : Boolean {
                recyclerView1Adapter.filter.filter(p0)
                return false
            }
            override fun onQueryTextChange(p0 : String) : Boolean {
                recyclerView1Adapter.filter.filter(p0)
                return false
            }
        })
    }

    public override fun setUpClicks(): Unit {
        var model=viewModel.expenseReportModel.value

        binding.fromDate.setOnClickListener {
            Log.e("Dates","FromDate:${model?.txtFromDate}..ToDate:${model?.txtToDate}")
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

        binding.btnOptionMenu.setOnClickListener {
            var PopUpmenu = PopupMenu(this, it)

            PopUpmenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.status_all -> {
                        viewModel.filterExpenseListByStatus("")
                        true
                    }
                    R.id.status_approved -> {
                        viewModel.filterExpenseListByStatus(item.title.toString())

                        true
                    }
                    R.id.status_pending -> {
                        viewModel.filterExpenseListByStatus(item.title.toString())

                        true
                    }
                    R.id.status_rejected -> {
                        viewModel.filterExpenseListByStatus(item.title.toString())

                        true
                    }

                    else -> false
                }

            }
            PopUpmenu.inflate(R.menu.status_menu)
            PopUpmenu.show()
        }
    }



    public fun onClickRecyclerView1(view: View, position: Int, item: ExpenseRowModel): Unit {
       destIntent=ExpenseDialogActivity.getIntent(this,null)
        destIntent!!.putExtra(IntentParameters.ExpenseObject,item)
        destIntent!!.putExtra(IntentParameters.isAdmin,true)
        startActivity(destIntent)
    }

    public override fun addObservers(): Unit {
        var progressDialog: AlertDialog? = null
        viewModel.progressLiveData.observe(this@ExpenseTypeWiseActivity) {

            if (it) {
                progressDialog?.dismiss()
                progressDialog = null
                progressDialog = this@ExpenseTypeWiseActivity.showProgressDialog()

            } else {

                progressDialog?.dismiss()

            }

        }
        viewModel.expenseReportListLiveData.observe(this@ExpenseTypeWiseActivity) {

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
                viewModel.bindExpensesBasedOnType(it.data.expenseList?: arrayListOf(),mExpenseName!!)
            else{
                Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_no_expense), Snackbar.LENGTH_LONG).show()
                binding.txtTotalExpense.text=MyApp.getInstance().resources.getString(R.string.lbl_no_expense)
                viewModel.bindExpensesBasedOnType(arrayListOf(),mExpenseName!!)
            }
        } else{
            Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
            binding.txtTotalExpense.text=it.data.message
            viewModel.bindExpensesBasedOnType(arrayListOf(),mExpenseName!!)
        }
    }
    public companion object {
        public const val TAG: String = "ExpensesTypeWise_Activity"
        public fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, ExpenseTypeWiseActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}
