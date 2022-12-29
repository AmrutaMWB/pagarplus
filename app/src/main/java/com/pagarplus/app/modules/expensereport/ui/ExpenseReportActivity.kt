package com.pagarplus.app.modules.expensereport.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityExpensesreportBinding
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


public class ExpenseReportActivity : BaseActivity<ActivityExpensesreportBinding>(R.layout.activity_expensesreport) {
    val viewModel: ExpenseReportVM by viewModels<ExpenseReportVM>()
    private var mEmployeeId:String?=""
    private var mAdminId:String?=""
    private var mIsAdmin:Boolean?=false

    public override fun onInitialized(): Unit {
        binding.expenseReportVM = viewModel
        mEmployeeId=intent.getStringExtra(IntentParameters.UserId)?:""
        mAdminId=intent.getStringExtra(IntentParameters.AdminId)?:""
        mIsAdmin=intent.getBooleanExtra(IntentParameters.isAdmin,false)
        val fromdate=intent.getStringExtra(IntentParameters.FromDate)
        val todate=intent.getStringExtra(IntentParameters.ToDate)

        if(fromdate!=null && todate!=null){
            viewModel.expenseReportModel.value?.txtFromDate=fromdate
            viewModel.expenseReportModel.value?.txtToDate=todate
        }

        val recyclerView1Adapter = ExpenseReportAdapter(viewModel.recyclerView1List.value?: mutableListOf(), this)
        binding.recyclerExpenseListView.adapter = recyclerView1Adapter
        recyclerView1Adapter.setOnItemClickListener(
            object : ExpenseReportAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int, item: ExpenseRowModel) {
                    onClickRecyclerView1(view, position, item)
                }
            }
        )
        viewModel.recyclerView1List.observe(this) {
            recyclerView1Adapter.updateData(it as ArrayList<ExpenseRowModel>)
        }

        val recyclerRatingAdapter = ExpenseRatingAdapter(viewModel.recyclerExpenseRatingList.value?: mutableListOf(), this)
        binding.recyclerRatingView.adapter = recyclerRatingAdapter

        recyclerRatingAdapter.setOnItemClickListener(
            object : ExpenseRatingAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int, item: ExpenseRatingItem) {
                   viewModel.filterExpenseList(item.ExpenseId)
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
           // finish()
           // takeScreenshot()
            getBitMap()
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

    override fun onResume() {
        super.onResume()
        sendGetExpenseListRequest()
    }


    fun getBitMap(){
        var recyclerView=binding.recyclerExpenseListView
        recyclerView.measure(
            View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val bm = Bitmap.createBitmap(
            recyclerView.getWidth(),
            recyclerView.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888)
        recyclerView.draw(Canvas(bm))

      //  saveImage(bm)
        val im = ImageView(this)
        im.setImageBitmap(bm)
        AlertDialog.Builder(this).setView(im).show()
    }

    public fun onClickRecyclerView1(view: View, position: Int, item: ExpenseRowModel): Unit {
     // ExpenseDialog(this,item,mIsAdmin?:false).show()
        var intent=ExpenseDialogActivity.getIntent(this,null)
        intent.putExtra(IntentParameters.ExpenseObject,item)
        intent.putExtra(IntentParameters.isAdmin,mIsAdmin)
        startActivity(intent)
    }

    public override fun addObservers(): Unit {
        var progressDialog: AlertDialog? = null
        viewModel.progressLiveData.observe(this@ExpenseReportActivity) {
            if (it) {
                progressDialog?.dismiss()
                progressDialog = null
                progressDialog = this@ExpenseReportActivity.showProgressDialog()
            } else {
                progressDialog?.dismiss()
            }
        }
        viewModel.expenseReportListLiveData.observe(this@ExpenseReportActivity) {
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
                viewModel.bindExpensesReportResponse(arrayListOf())
            }
        } else{
            Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
           binding.txtTotalExpense.text=it.data.message
            viewModel.bindExpensesReportResponse(arrayListOf())
        }
    }
    public companion object {
    public const val TAG: String = "ExpensesReport_Activity"
    public fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, ExpenseReportActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
