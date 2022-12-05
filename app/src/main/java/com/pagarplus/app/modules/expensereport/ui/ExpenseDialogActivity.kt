package com.pagarplus.app.modules.expensereport.ui
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.viewModelScope
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.databinding.ActivityExpenseDialogBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.expensereport.data.viewmodel.ExpenseReportVM
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import com.pagarplus.app.network.models.expense.ExpenseStatusRequest
import com.pagarplus.app.network.models.expense.ImageItems
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import kotlinx.coroutines.launch
import org.json.JSONObject

import org.koin.core.KoinComponent
import retrofit2.HttpException
import kotlin.String
import kotlin.Unit

public class ExpenseDialogActivity: BaseActivity<ActivityExpenseDialogBinding>(R.layout.activity_expense_dialog),KoinComponent {

    private var mObject : ExpenseRowModel?=null
    private var mIsAdmin :Boolean=false
    private val viewModel: ExpenseReportVM by viewModels<ExpenseReportVM>()


    public override fun setUpClicks(): Unit {

        binding.btnApprove.setOnClickListener {
            var comment=binding.etComments?.text?.trim().toString()
            if(comment.isNotEmpty()) {
                val Request = ExpenseStatusRequest(
                    commentByAdmin = binding.etComments.text.toString(),
                    expenseTypeID = mObject?.ExpenseTypeID.toString(),
                    ExpenseID = mObject?.ExpenseID.toString())
                viewModel.approveExpense(Request)


            }else
                Snackbar.make(this.binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Snackbar.LENGTH_LONG).show()
        }

        binding.btnReject.setOnClickListener {
            var comment=binding.etComments.text?.trim().toString()
            if(comment.isNotEmpty()) {
                val Request = ExpenseStatusRequest(
                    commentByAdmin = binding.etComments.text.toString(),
                    expenseTypeID = mObject?.ExpenseTypeID.toString(),
                    ExpenseID = mObject?.ExpenseID.toString())
                viewModel.rejectExpense(Request)

            }else
                Snackbar.make(this.binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Snackbar.LENGTH_LONG).show()
        }
    }

    public override fun onInitialized(): Unit {
        mObject=intent.getSerializableExtra(IntentParameters.ExpenseObject) as ExpenseRowModel
        mIsAdmin=intent.getBooleanExtra(IntentParameters.isAdmin,false)

        var bannerList = mObject?.billImages
        if (bannerList!!.isEmpty())
            bannerList.add(ImageItems(billImageUrl = mObject?.BillImageUrl))
        val imageList = ArrayList<SlideModel>()
        bannerList.forEach {
            imageList.add(SlideModel("${it?.billImageUrl}"))
        }

        binding.scrollViewPager.setImageList(imageList)
        initValues()
    }

    private fun initValues() {
        val visibility=if(mIsAdmin) View.VISIBLE else View.GONE
        //val visibility=if(mIsAdmin && (mObject?.Status?.equals("Pending",true) == true)) View.VISIBLE else View.GONE
        binding.btnApprove.visibility=visibility
        binding.btnReject.visibility=visibility
        binding.etComments.visibility=visibility

        val strBuilder = StringBuilder()
        strBuilder.appendLine("Status: ${mObject?.Status}")
        strBuilder.appendLine("")
        if(mIsAdmin) {
            strBuilder.appendLine("User Name: ${mObject?.EmployeeName}")
        }
        strBuilder.appendLine("Date: ${mObject?.ExpenseDateTime}")
        strBuilder.appendLine("Type: ${mObject?.ExpenseTypeName}")
        if(mObject?.SubExpenseTypeID!=0)
            strBuilder.appendLine("Expense: ${mObject?.SubExpenseTypeName}")
        if(!mObject?.HotelName.isNullOrEmpty())
            strBuilder.appendLine("Place: ${mObject?.HotelName}")
        if(!mObject?.HotelDetails.isNullOrEmpty())
            strBuilder.appendLine("HotelDetails: ${mObject?.HotelDetails}")

        if(!mObject?.BoardFromDate.isNullOrEmpty())
            strBuilder.appendLine("From: ${mObject?.BoardFromDate?.extractDate()}")

        if(!mObject?.BoardToDate.isNullOrEmpty())
            strBuilder.appendLine("To: ${mObject?.BoardToDate?.extractDate()}")
        if(!mObject?.SourceLocation.isNullOrEmpty())
            strBuilder.appendLine("Source: ${mObject?.SourceLocation}")
        if(!mObject?.DestinationLocation.isNullOrEmpty())
            strBuilder.appendLine("Destination: ${mObject?.DestinationLocation}")
        if(!mObject?.CommentByEmployee.isNullOrEmpty())
            strBuilder.appendLine("Comment By Employee: ${mObject?.CommentByEmployee}")
        if(!mObject?.CommentByAdmin.isNullOrEmpty())
            strBuilder.appendLine("Comment By Admin: ${mObject?.CommentByAdmin}")
        binding.txtExpenseDetails!!.text=strBuilder.toString()
    }
    public override fun addObservers(): Unit {
        var progressDialog: AlertDialog? = null
        viewModel.progressLiveData.observe(this@ExpenseDialogActivity) {

            if (it) {
                progressDialog?.dismiss()
                progressDialog = null
                progressDialog = this@ExpenseDialogActivity.showProgressDialog()

            } else {

                progressDialog?.dismiss()

            }

        }

        viewModel.approveExpenseLiveData.observe(this@ExpenseDialogActivity) {

            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                if(it.data.status){
                  finish()
                }
                Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
            } else if (it is ErrorResponse) {
                onError(it.data ?: Exception())
            }

        }
        viewModel.rejectExpenseLiveData.observe(this@ExpenseDialogActivity) {

            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                if(it.data.status){
                    finish()
                }
                Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
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

    public companion object {
        public const val TAG: String = "Expense_Dialog_ACTIVITY"

        public fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, ExpenseDialogActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}
