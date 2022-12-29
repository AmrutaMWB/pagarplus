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
import androidx.recyclerview.widget.RecyclerView
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
import com.pagarplus.app.modules.adminattendancelist.data.model.AttendanceRowModel
import com.pagarplus.app.modules.adminattendancelist.data.model.VisitRowModel
import com.pagarplus.app.modules.attendance_details.data.model.AttendanceDetailsModel
import com.pagarplus.app.network.models.attendance.AttedanceApproveRejectRequest
import com.pagarplus.app.network.models.attendance.EmpIdItem
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import com.pagarplus.app.network.models.expense.ExpenseStatusRequest
import java.io.IOException
import java.util.*

class AttendanceListDialogActivity (val context: AdminAttendancelistActivity, var pos :Int)
{
    var mDialogPayment : Dialog?=null
    var tvExpenseDetails: TextView?=null
    lateinit var EtComment: EditText
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
        mDialogPayment!!.setContentView(R.layout.activity_attendancelistdialog)
        mDialogPayment!!.setCancelable(true)
        var tvEmpname = mDialogPayment!!.findViewById<TextView>(R.id.txtEmpname)
        var tvBranch = mDialogPayment!!.findViewById<TextView>(R.id.txtbranchname)
        var tvDept = mDialogPayment!!.findViewById<TextView>(R.id.txtDepartmentName)
        var lindept = mDialogPayment!!.findViewById<LinearLayout>(R.id.lindept)
        var recylerVisit = mDialogPayment!!.findViewById<RecyclerView>(R.id.recyclerVisitList)
        var scrolPager = mDialogPayment!!.findViewById<ImageSlider>(R.id.scrollViewPager)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        EtComment = mDialogPayment!!.findViewById<EditText>(R.id.etComments)

        /*load images in scroll pager*/
        val imageList = ArrayList<SlideModel>()
        context.viewModel.attendList.value?.get(pos)?.ImagesList?.forEach {
            imageList.add(SlideModel("${it}"))
        }
        scrolPager.setImageList(imageList)

        var visitListAdapter = VisitListAdapter(context.viewModel.attendList.value?.get(pos)?.visitList?.value?:mutableListOf())
        recylerVisit.adapter = visitListAdapter
        visitListAdapter.setOnItemClickListener(
            object : VisitListAdapter.OnItemClickListener {
                override fun onItemClick(view:View, position:Int, item : VisitRowModel) {
                    onClickRecyclerAttendance(view, position, item)
                }
            }
        )
        context.viewModel.visitList.observe(context) {
            visitListAdapter.updateData(it)
        }

        tvEmpname!!.text = context.viewModel.attendList.value?.get(pos)?.txtEmpName

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

        mDialogPayment!!.show()
    }

    fun onClickRecyclerAttendance(
        view: View,
        position: Int,
        item: VisitRowModel
    ): Unit {
        when(view.id) {
            R.id.btnApprove ->  {
                var comment=EtComment.text?.trim().toString()
                if(comment.isNotEmpty()) {
                    var attendanceID = context.viewModel.visitList.value?.get(pos)?.txtAttendanceID
                    var status = "Approved"
                    context.viewModel.callApproveAttendanceApi(attendanceID!!,status,comment)
                    mDialogPayment?.dismiss()
                }else
                    Toast.makeText(context, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Toast.LENGTH_LONG).show()
            }
            R.id.btnReject ->  {
                var comment=EtComment.text?.trim().toString()
                if(comment.isNotEmpty()) {
                    var attendanceID = context.viewModel.visitList.value?.get(pos)?.txtAttendanceID
                    var status = "Rejected"
                    context.viewModel.callApproveAttendanceApi(attendanceID!!,status,comment)
                    mDialogPayment?.dismiss()
                }else
                    Toast.makeText(context, MyApp.getInstance().resources.getString(R.string.lbl_ent_comm), Toast.LENGTH_LONG).show()
            }
        }
    }
}