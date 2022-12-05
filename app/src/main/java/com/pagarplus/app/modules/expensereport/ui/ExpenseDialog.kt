package com.pagarplus.app.modules.expensereport.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.github.demono.AutoScrollViewPager
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import com.pagarplus.app.network.models.expense.ExpenseStatusRequest
import com.pagarplus.app.network.models.expense.ImageItems
import java.util.*

class ExpenseDialog (val context: ExpenseReportActivity, var obj :ExpenseRowModel,var isAdmin:Boolean=false)
{
    var mDialogPayment : Dialog?=null
    var tvExpenseDetails: TextView?=null
    var BtnApprove: Button?=null
    var BtnReject: Button?=null
    var EtComment: EditText?=null
    fun show() {
        mDialogPayment = Dialog(context)
        mDialogPayment!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialogPayment!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = mDialogPayment!!.getWindow()
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        mDialogPayment!!.setContentView(R.layout.dialog_expense)
        mDialogPayment!!.setCancelable(true)
        var viewPager = mDialogPayment!!.findViewById<AutoScrollViewPager>(R.id.scrollViewPager)
        tvExpenseDetails = mDialogPayment!!.findViewById<View>(R.id.txtExpenseDetails) as? TextView
        BtnApprove = mDialogPayment!!.findViewById<View>(R.id.btnApprove) as? Button
        BtnReject = mDialogPayment!!.findViewById<View>(R.id.btnReject) as? Button
        EtComment = mDialogPayment!!.findViewById<View>(R.id.etComments) as? EditText

        val visibility=if(isAdmin && (obj.Status?.equals("Pending",true) == true))View.VISIBLE else View.GONE
        BtnApprove?.visibility=visibility
        BtnReject?.visibility=visibility
        EtComment?.visibility=visibility

        var bannerList = obj.billImages
        if (bannerList!!.isEmpty())
            bannerList.add(ImageItems(billImageUrl = obj.BillImageUrl))
        var autoScrollPagerAdapter: AutoScrollPagerAdapter = AutoScrollPagerAdapter(context.supportFragmentManager, bannerList)
        viewPager!!.height
        viewPager!!.adapter = autoScrollPagerAdapter
        // start auto scroll
        viewPager!!.startAutoScroll()
        // enable recycling using true
        viewPager!!.isCycle = true

        val strBuilder = StringBuilder()
        strBuilder.appendLine("Status: ${obj.Status}")
        strBuilder.appendLine("")
        if(isAdmin) {
            strBuilder.appendLine("User Name: ${obj.EmployeeName}")
        }
        strBuilder.appendLine("Date: ${obj.ExpenseDateTime}")
        strBuilder.appendLine("Type: ${obj.ExpenseTypeName}")
        if(obj.SubExpenseTypeID!=0)
            strBuilder.appendLine("Expense: ${obj.SubExpenseTypeName}")
        if(!obj.HotelName.isNullOrEmpty())
            strBuilder.appendLine("Place: ${obj.HotelName}")
        if(!obj.HotelDetails.isNullOrEmpty())
            strBuilder.appendLine("HotelDetails: ${obj.HotelDetails}")

        if(!obj.BoardFromDate.isNullOrEmpty())
            strBuilder.appendLine("From: ${obj.BoardFromDate?.extractDate()}")

        if(!obj.BoardToDate.isNullOrEmpty())
            strBuilder.appendLine("To: ${obj.BoardToDate?.extractDate()}")
        if(!obj.SourceLocation.isNullOrEmpty())
            strBuilder.appendLine("Source: ${obj.SourceLocation}")
        if(!obj.DestinationLocation.isNullOrEmpty())
            strBuilder.appendLine("Destination: ${obj.DestinationLocation}")
        if(!obj.CommentByEmployee.isNullOrEmpty())
            strBuilder.appendLine("Comment By Employee: ${obj.CommentByEmployee}")
        if(!obj.CommentByAdmin.isNullOrEmpty())
            strBuilder.appendLine("Comment By Admin: ${obj.CommentByAdmin}")
        tvExpenseDetails!!.text=strBuilder.toString()

        BtnApprove!!.setOnClickListener {
            var comment=EtComment?.text?.trim().toString()
            if(comment.isNotEmpty()) {
                val Request = ExpenseStatusRequest(
                    commentByAdmin = EtComment?.text.toString(),
                    expenseTypeID = obj.ExpenseTypeID.toString(),
                    ExpenseID = obj.ExpenseID.toString()
                )
                context.viewModel.approveExpense(Request)
                mDialogPayment?.dismiss()
            }else
                Snackbar.make(context.binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Snackbar.LENGTH_LONG).show()
        }

        BtnReject!!.setOnClickListener {
            var comment=EtComment?.text?.trim().toString()
            if(comment.isNotEmpty()) {
                val Request = ExpenseStatusRequest(
                    commentByAdmin = EtComment?.text.toString(),
                    expenseTypeID = obj.ExpenseTypeID.toString(),
                    ExpenseID = obj.ExpenseID.toString())
                context.viewModel.rejectExpense(Request)
                mDialogPayment?.dismiss()
            }else
                Snackbar.make(context.binding.root, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Snackbar.LENGTH_LONG).show()
        }
        mDialogPayment!!.show()
    }
}