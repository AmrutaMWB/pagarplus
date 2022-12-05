package com.pagarplus.app.modules.adminattendancelist.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.modules.attendance_details.data.model.AttendanceDetailsModel
import com.pagarplus.app.network.models.attendance.AttedanceApproveRejectRequest
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import com.pagarplus.app.network.models.expense.ExpenseStatusRequest
import java.io.IOException
import java.util.*

class AttendanceListDialog (val context: AdminAttendancelistActivity, var pos :Int)
{
    var mDialogPayment : Dialog?=null
    var tvExpenseDetails: TextView?=null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    fun  dismiss(){
        mDialogPayment?.dismiss()
    }
    fun show() {
        mDialogPayment = Dialog(context)
        mDialogPayment!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialogPayment!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = mDialogPayment!!.getWindow()
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        mDialogPayment!!.setContentView(R.layout.details_dialog)
        mDialogPayment!!.setCancelable(true)
        var imgCheckin = mDialogPayment!!.findViewById<ImageView>(R.id.imgCheckin)
        var imgCheckout = mDialogPayment!!.findViewById<ImageView>(R.id.imgCheckout)
        var tvEmpname = mDialogPayment!!.findViewById<TextView>(R.id.txtEmpname)
        var tvBranch = mDialogPayment!!.findViewById<TextView>(R.id.txtbranchname)
        var tvDept = mDialogPayment!!.findViewById<TextView>(R.id.txtDepartmentName)
        var tvCheckin = mDialogPayment!!.findViewById<TextView>(R.id.txtcheckintime)
        var tvCheckout = mDialogPayment!!.findViewById<TextView>(R.id.txtcheckouttime)
        var tvadmincomment = mDialogPayment!!.findViewById<TextView>(R.id.txtadmincomment)
        var tvcheckinLocation = mDialogPayment!!.findViewById<TextView>(R.id.txtcheckinloc)
        var tvcheckoutLocation = mDialogPayment!!.findViewById<TextView>(R.id.txtcheckoutloc)
        var tvvisittype = mDialogPayment!!.findViewById<TextView>(R.id.txtvisittype)
        var tvvisitduration = mDialogPayment!!.findViewById<TextView>(R.id.txtdurationtype)
        var linbranch = mDialogPayment!!.findViewById<LinearLayout>(R.id.lindet2)
        var lindept = mDialogPayment!!.findViewById<LinearLayout>(R.id.lindet3)
        var tvstatus = mDialogPayment!!.findViewById<TextView>(R.id.txtstatus)
        var tvleavestatus = mDialogPayment!!.findViewById<TextView>(R.id.txtleavestatus)
        var tvisleave = mDialogPayment!!.findViewById<TextView>(R.id.txtisleave)
        var tvreasonleave = mDialogPayment!!.findViewById<TextView>(R.id.txtreasonleave)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        var BtnApprove = mDialogPayment!!.findViewById<Button>(R.id.btnApprove)
        var BtnReject = mDialogPayment!!.findViewById<Button>(R.id.btnReject)
        var EtComment = mDialogPayment!!.findViewById<EditText>(R.id.etComments)
        var BtnEdit = mDialogPayment!!.findViewById<Button>(R.id.btnEdit)

        /*val visibility=if((context.viewModel.attendList.value?.get(pos)?.txtStatus?.isNullOrEmpty() == true) ||
            (context.viewModel.attendList.value?.get(pos)?.txtStatus?.equals("Pending",true) == true))View.VISIBLE else View.GONE
        BtnApprove?.visibility=visibility
        BtnReject?.visibility=visibility
        EtComment?.visibility=visibility*/

        if(context.viewModel.attendList.value?.get(pos)?.txtStatus.equals("Approved")){
            BtnEdit.isVisible = true
            BtnApprove.isVisible = false
            BtnReject.isVisible = false
            EtComment.isVisible = false

        }else if(context.viewModel.attendList.value?.get(pos)?.txtStatus.equals("Rejected")){
            BtnApprove.isVisible = false
            BtnReject.isVisible = false
            EtComment.isVisible = false
            BtnEdit.isVisible = true
        }else if(context.viewModel.attendList.value?.get(pos)?.txtType.equals("Absent")){
            BtnApprove.isVisible = false
            BtnReject.isVisible = false
            EtComment.isVisible = false
            BtnEdit.isVisible = false
        }else{
            BtnApprove.isVisible = true
            BtnReject.isVisible = true
            BtnEdit.isVisible = false
            EtComment.setText("")
        }

        Glide.with(context).load(context.viewModel.attendList.value?.get(pos)?.txtcheckinimage).into(imgCheckin)
        Glide.with(context).load(context.viewModel.attendList.value?.get(pos)?.txtcheckoutimage).into(imgCheckout)
        tvEmpname!!.text = context.viewModel.attendList.value?.get(pos)?.txtEmpName
        tvCheckin!!.text = context.viewModel.attendList.value?.get(pos)?.txtCheckinDate
        tvCheckout!!.text = context.viewModel.attendList.value?.get(pos)?.txtCheckoutDate
        tvvisittype!!.text = context.viewModel.attendList.value?.get(pos)?.txtVisit
        tvvisitduration!!.text = context.viewModel.attendList.value?.get(pos)?.txtDuration
        tvstatus!!.text = context.viewModel.attendList.value?.get(pos)?.txtStatus

        if(context.viewModel.attendList.value?.get(pos)?.txtadmincomment.isNullOrEmpty()){
            tvadmincomment.isVisible = false
        }else {
            tvadmincomment!!.text = context.viewModel.attendList.value?.get(pos)?.txtadmincomment
        }

        if(context.viewModel.attendList.value?.get(pos)?.txtisLeaveExist.equals("true")){
            tvisleave!!.text = "On Leave : Yes"
            tvleavestatus!!.text = "Leave Status : "+context.viewModel.attendList.value?.get(pos)?.txtLeaveStatus
            tvreasonleave!!.text = "Reason for Leave : "+context.viewModel.attendList.value?.get(pos)?.txtrerasonLeave
        }else{
            tvisleave!!.text = "On Leave : No"
            tvleavestatus.isVisible = false
            tvreasonleave.isVisible = false
        }

        if(context.viewModel.attendList.value?.get(pos)?.txtbranch.isNullOrEmpty()) {
            tvBranch!!.text = context.viewModel.profiledetails?.organization.toString()
        }else{
            tvBranch!!.text = context.viewModel.attendList.value?.get(pos)?.txtbranch
        }

        if(context.viewModel.attendList.value?.get(pos)?.txtDept.isNullOrEmpty()) {
            lindept.isVisible = false
        }else{
            lindept.isVisible = true
            tvDept!!.text = context.viewModel.attendList.value?.get(pos)?.txtDept
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (context.viewModel.attendList.value?.get(pos)?.txtcheckinLocation.isNullOrEmpty() ||
            context.viewModel.attendList.value?.get(pos)?.txtcheckinLocation.equals(" ")){
            tvcheckinLocation.text = ""
        }else{
            tvcheckinLocation!!.text = context.viewModel.attendList.value?.get(pos)?.txtcheckinLocation
        }

        if (context.viewModel.attendList.value?.get(pos)?.txtcheckoutLocation.isNullOrEmpty() ||
            context.viewModel.attendList.value?.get(pos)?.txtcheckoutLocation.equals(" ")){
            tvcheckoutLocation.text = ""
        }else{
            tvcheckoutLocation!!.text = context.viewModel.attendList.value?.get(pos)?.txtcheckoutLocation
        }

        BtnEdit.setOnClickListener{
            BtnApprove.isVisible = true
            BtnEdit.isVisible = false
            BtnReject.isVisible = true
            EtComment.isVisible = true
        }

        BtnApprove!!.setOnClickListener {
            var comment=EtComment?.text?.trim().toString()
            if(comment.isNotEmpty()) {
                val Request = AttedanceApproveRejectRequest(
                    comment = EtComment?.text.toString(),
                    attendanceID = context.viewModel.attendList.value?.get(pos)?.txtAttendanceID
                )
                context.viewModel.callApproveAttendanceApi(Request)
                mDialogPayment?.dismiss()
            }else
                Toast.makeText(context, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Toast.LENGTH_LONG).show()
        }

        BtnReject!!.setOnClickListener {
            var comment=EtComment?.text?.trim().toString()
            if(comment.isNotEmpty()) {
                val Request = AttedanceApproveRejectRequest(
                    comment = EtComment?.text.toString(),
                    attendanceID = context.viewModel.attendList.value?.get(pos)?.txtAttendanceID
                )
                context.viewModel.callRejectAttendanceApi(Request)
                mDialogPayment?.dismiss()
            }else
                Toast.makeText(context, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Toast.LENGTH_LONG).show()
        }
        mDialogPayment!!.show()
    }
}