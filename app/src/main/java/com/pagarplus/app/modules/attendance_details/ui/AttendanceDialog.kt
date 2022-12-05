package com.pagarplus.app.modules.attendance_details.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
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

class AttendanceDialog (val context: Context, var pos :Int, var Item:AttendanceDetailsModel)
{
    var mDialogPayment : Dialog?=null

    //private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    fun show() {
        Log.e("AttendenceModel","$Item")
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
        var tvcheckinLocation = mDialogPayment!!.findViewById<TextView>(R.id.txtcheckinloc)
        var tvcheckoutLocation = mDialogPayment!!.findViewById<TextView>(R.id.txtcheckoutloc)
        var tvvisittype = mDialogPayment!!.findViewById<TextView>(R.id.txtvisittype)
        var tvvisitduration = mDialogPayment!!.findViewById<TextView>(R.id.txtdurationtype)
        var tvadmincomment = mDialogPayment!!.findViewById<TextView>(R.id.txtadmincomment)
        var tvapprovrStatus = mDialogPayment!!.findViewById<TextView>(R.id.txtstatus)
        var BtnApprove = mDialogPayment!!.findViewById<Button>(R.id.btnApprove)
        var BtnReject = mDialogPayment!!.findViewById<Button>(R.id.btnReject)
        var EtComment = mDialogPayment!!.findViewById<EditText>(R.id.etComments)
        var linbranch = mDialogPayment!!.findViewById<LinearLayout>(R.id.lindet2)
        var lindept = mDialogPayment!!.findViewById<LinearLayout>(R.id.lindet3)
        var linName = mDialogPayment!!.findViewById<LinearLayout>(R.id.lindet1)
        //var txtcancel = mDialogPayment!!.findViewById<TextView>(R.id.txtCancel)
       // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if(!Item.txtcheckinimage.isNullOrEmpty())
        Glide.with(context).load(Item.txtcheckinimage).into(imgCheckin)
        if(!Item.txtcheckoutimage.isNullOrEmpty())
            Glide.with(context).load(Item.txtcheckoutimage).into(imgCheckout)
        BtnApprove?.isVisible=false
        BtnReject?.isVisible=false
        EtComment?.isVisible=false

        linName.isVisible = false

        tvCheckin!!.text = Item.txtCheckinDate
        tvCheckout!!.text = Item.txtCheckoutDate
        tvadmincomment!!.text = Item.txtadmincomment
        tvapprovrStatus!!.text = Item.txtStatus
        tvvisittype!!.text = Item.txtVisit
        tvvisitduration!!.text = Item.txtDuration

        if(Item.txtbranch.isNullOrEmpty()) {
            tvBranch!!.text = Item.txtbranch
                //context.viewModel.profiledetails?.organization.toString()
        }else{
            tvBranch!!.text = Item.txtbranch
        }

        if(Item.txtDept.isNullOrEmpty()) {
            lindept.isVisible = false
        }else{
            lindept.isVisible = true
            tvDept!!.text = Item.txtDept
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

 /*       if (Item.txtcheckinLocation.isNullOrEmpty()){
            if (!Item.txtcheckinlatitude.isNullOrEmpty()){
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener(context){ task->
                    var location: Location?=task.result
                    if(location!=null)
                    {
                        val mLatitude = Item.txtcheckinlatitude!!.toDouble()
                        val mLongitude = Item.txtcheckinlongitude!!.toDouble()
                        val geocoder = Geocoder(context, Locale.getDefault())
                        var addresses : List<Address>?=null
                        try {
                            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        if (addresses!!.size > 0) {
                            Log.d("max", " " + addresses[0].getMaxAddressLineIndex())
                            val address: String = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            val city: String = addresses[0].getLocality()
                            val state: String = addresses[0].getAdminArea()
                            val country: String = addresses[0].getCountryName()
                            val postalCode: String = addresses[0].getPostalCode()
                            val knownName: String = addresses[0].getFeatureName() // Only if available else return NULLaddresses[0].getAdminArea()

                            Log.e("Location","$address $city")
                            tvcheckinLocation!!.text = "$address $city"
                        }
                    }
                }
            }else{
                tvcheckinLocation!!.text = ""
            }
        }else{
            tvcheckinLocation!!.text = Item.txtcheckinLocation
        }*/
        tvcheckinLocation!!.text = Item.txtcheckinLocation?:"N/A"

   /*     if (Item.txtcheckoutLocation.isNullOrEmpty()){
            if (!Item.txtcheckoutlatitude.isNullOrEmpty()){
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener(context){ task->
                    var location: Location?=task.result
                    if(location!=null)
                    {
                        val mLatitude = Item.txtcheckoutlatitude!!.toDouble()
                        val mLongitude = Item.txtcheckoutlongitude!!.toDouble()
                        val geocoder = Geocoder(context, Locale.getDefault())
                        var addresses1 : List<Address>?=null
                        try {
                            addresses1 = geocoder.getFromLocation(mLatitude, mLongitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        if (addresses1!!.size > 0) {
                            Log.d("max", " " + addresses1[0].getMaxAddressLineIndex())
                            val address: String = addresses1[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                             // Only if available else return NULLaddresses[0].getAdminArea()

                            Log.e("Location","$address")
                            tvcheckoutLocation!!.text = "$address"
                        }
                    }
                }
            }else{
                tvcheckoutLocation!!.text = ""
            }
        }else{
            tvcheckoutLocation!!.text = Item.txtcheckoutLocation
        }*/

        tvcheckoutLocation!!.text = Item.txtcheckoutLocation?:"N/A"

        mDialogPayment!!.show()
    }
}