package com.pagarplus.app.modules.advertise.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityAdvertiselistBinding
import com.pagarplus.app.databinding.AprRejPenLevlonlistBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.admindashboard.ui.AdmindashboardActivity
import com.pagarplus.app.modules.adminemplist.ui.AdminemplistActivity
import com.pagarplus.app.modules.advertise.data.model.AdvertiseRowModel
import com.pagarplus.app.modules.advertise.data.viewmodel.AdvertiseVM
import com.pagarplus.app.modules.aprrejloanleavelist.data.model.MessageListModel
import com.pagarplus.app.modules.aprrejloanleavelist.data.viewmodel.AprRejloanleaveVM
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.modules.replymessage.ui.ReplyActivity
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetleaveListResponse
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.FetchGetloanListResponse
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class AdvertiseListActivity :
    BaseActivity<ActivityAdvertiselistBinding>(R.layout.activity_advertiselist), BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: AdvertiseVM by viewModels<AdvertiseVM>()

  lateinit var listAdapter : AdvertiseAdapter

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")

    val sdf = SimpleDateFormat("dd-MM-yyyy")
    val currentDate = sdf.format(Date())
    //viewModel.advertiseModel.value?.seldate = currentDate

    viewModel.callFetchAllAdvertiseListApi()
    listAdapter = AdvertiseAdapter(viewModel.advertiseList.value?:mutableListOf())
    binding.recyclerAdvertiseList.adapter = listAdapter
    listAdapter.setOnItemClickListener(
      object : AdvertiseAdapter.OnItemClickListener {
        override fun onItemClick(view:View, position:Int, item : AdvertiseRowModel) {
          onClickRecyclerMessage(view, position, item)
        }
      }
    )
    viewModel.advertiseList.observe(this) {
      listAdapter.updateData(it)
    }
    binding.advertiseVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.btnBack.setOnClickListener{
      val intent = AdvertiseActivity.getIntent(this, null)
      finish()
      startActivity(intent)
    }

    binding.imageCalendarAdv.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        viewModel.advertiseModel.value?.seldate = selected
        viewModel.callFetchAllAdvertiseListApi()
      }
    }
    binding.txtAllBranch.setOnClickListener {
      callpopupBranchDept(true)
    }
    binding.txtAllDepartment.setOnClickListener {
      callpopupBranchDept(false)
    }
  }

  /*call branch/Dept popup*/
  fun callpopupBranchDept(isBranch:Boolean){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsBranchDept, isBranch)
    var itemlistDialog = BranchDeptlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
  }

  public override fun addObservers(): Unit {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }

    /*fetch advertise lists*/
    viewModel.getAllBannerLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessGetAllBanner(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    /*active created advertise*/
    viewModel.activeBannerLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessActiveDeactiveBanner(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }

    /*deactive created advertise*/
    viewModel.deactiveBannerLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessActiveDeactiveBanner(it)
      } else if(it is ErrorResponse) {
        onErrorFetchMsg(it.data ?:Exception())
      }
    }
  }

  private fun onSuccessGetAllBanner(response: SuccessResponse<CreateCreateBannerResponse>): Unit {
    viewModel.bindFetchAllAdvertiseListResponse(response.data)
  }

  private fun onSuccessActiveDeactiveBanner(response: SuccessResponse<CreateCreateBannerResponse>): Unit {
    viewModel.bindActiveDeactiveBannerResponse(response.data)
  }

  private fun onErrorFetchMsg(exception: Exception): Unit {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
      is HttpException -> {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
        else JSONObject()
        val errMessage = if(!errorObject.optString("Errormsg").isNullOrEmpty()) {
          errorObject.optString("Errormsg").toString()
        } else {
          exception.response()?.message()?:""
        }
        Snackbar.make(binding.root, errMessage, Snackbar.LENGTH_LONG).show()
      }
    }
  }

  /*view idproof image in fullview*/
  fun EnlargeImageDialog(uri: Uri) {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()

    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.imageview_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val imgview = dialogView.findViewById<ImageView>(R.id.dialog_imageview)
    val alertDialog = dialogBuilder.create()

    alertDialog.setCancelable(true)
    alertDialog.show();
    imgview.setImageURI(uri)
  }

  fun onClickRecyclerMessage(
    view: View,
    position: Int,
    item: AdvertiseRowModel
  ): Unit {
    when(view.id) {
      R.id.imgEdit ->  {
        val destBundle = Bundle()
        destBundle.putString(IntentParameters.IsEditAdvertise, "true")
        if(listAdapter.list.size > 0) {
          destBundle.putInt(IntentParameters.BannerID, listAdapter.list.get(position)?.txtBannerID!!)
          destBundle.putString(IntentParameters.imagePath, listAdapter.list.get(position)?.txtImgpath!!)
          destBundle.putString(IntentParameters.TxtMessage, listAdapter.list.get(position)?.txtMessage)
          destBundle.putString(IntentParameters.TxtTitle, listAdapter.list.get(position)?.txtTitle)
          destBundle.putString(IntentParameters.fromDate, listAdapter.list.get(position)?.txtFromDate!!)
          destBundle.putString(IntentParameters.toDate, listAdapter.list.get(position)?.txtToDate!!)
          destBundle.putInt(IntentParameters.BranchID, listAdapter.list.get(position)?.txtBranchId!!)
          destBundle.putInt(IntentParameters.DeptID, listAdapter.list.get(position)?.txtDeptID!!)
          destBundle.putString(IntentParameters.Branch, listAdapter.list.get(position)?.txtBranch!!)
          destBundle.putString(IntentParameters.Dept, listAdapter.list.get(position)?.txtDept!!)
        }else{
          destBundle.putInt(IntentParameters.BannerID, viewModel.advertiseList.value?.get(position)?.txtBannerID!!)
          destBundle.putString(IntentParameters.imagePath, viewModel.advertiseList.value?.get(position)?.txtImgpath!!)
          destBundle.putString(IntentParameters.TxtMessage, viewModel.advertiseList.value?.get(position)?.txtMessage)
          destBundle.putString(IntentParameters.TxtTitle, viewModel.advertiseList.value?.get(position)?.txtTitle)
          destBundle.putString(IntentParameters.fromDate, viewModel.advertiseList.value?.get(position)?.txtFromDate!!)
          destBundle.putString(IntentParameters.toDate, viewModel.advertiseList.value?.get(position)?.txtToDate!!)
          destBundle.putInt(IntentParameters.BranchID, viewModel.advertiseList.value?.get(position)?.txtBranchId!!)
          destBundle.putInt(IntentParameters.DeptID, viewModel.advertiseList.value?.get(position)?.txtDeptID!!)
          destBundle.putString(IntentParameters.Branch, viewModel.advertiseList.value?.get(position)?.txtBranch!!)
          destBundle.putString(IntentParameters.Dept, viewModel.advertiseList.value?.get(position)?.txtDept!!)
        }
        val destIntent = AdvertiseActivity.getIntent(this, destBundle)
        startActivity(destIntent)
      }
      R.id.txtActive -> {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Confirmation")
        // set message of alert dialog
        dialogBuilder.setMessage("Are you sure you want deactivate this advertise image?")
          // if the dialog is cancelable
          .setCancelable(false)
          // positive button text and action
          .setPositiveButton("Yes", DialogInterface.OnClickListener {
              dialog, id ->
            if(listAdapter.list.size > 0) {
              viewModel.callActiveAdvertiseApi(listAdapter.list.get(position)!!.txtBannerID!!)
            }else{
              viewModel.callActiveAdvertiseApi(viewModel.advertiseList.value?.get(position)!!.txtBannerID!!)
            }
            finish()
            startActivity(AdvertiseListActivity.getIntent(this,null))
          })
          // negative button text and action
          .setNegativeButton("No", DialogInterface.OnClickListener {
              dialog, id -> dialog.cancel()
          })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
      }
      R.id.imgDeactive -> {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Confirmation")
        // set message of alert dialog
        dialogBuilder.setMessage("Are you sure you want deactivate this advertise image?")
          // if the dialog is cancelable
          .setCancelable(false)
          // positive button text and action
          .setPositiveButton("Yes", DialogInterface.OnClickListener {
              dialog, id ->
            if(listAdapter.list.size > 0) {
              viewModel.callDeactiveAdvertiseApi(listAdapter.list.get(position)!!.txtBannerID!!)
            }else{
              viewModel.callDeactiveAdvertiseApi(viewModel.advertiseList.value?.get(position)!!.txtBannerID!!)
            }
            finish()
            startActivity(AdvertiseListActivity.getIntent(this,null))
          })
          // negative button text and action
          .setNegativeButton("No", DialogInterface.OnClickListener {
              dialog, id -> dialog.cancel()
          })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
      }
      R.id.imageNoti ->  {
        if(listAdapter.list.size > 0) {
          EnlargeImageDialog(listAdapter.list.get(position)?.txtImgpath!!.toUri())
        }else{
          EnlargeImageDialog(viewModel.advertiseList.value?.get(position)?.txtImgpath!!.toUri())
        }
      }
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("Branch","$selectedItem")
    if(selectedItem.isBranch==true){
      binding.txtAllBranch.setText(selectedItem.txtName)
      viewModel.advertiseModel.value?.txtBranchId = selectedItem.txtValue
      viewModel.callFetchAllAdvertiseListApi()
    }else{
      binding.txtAllDepartment.setText(selectedItem.txtName)
      viewModel.advertiseModel.value?.txtDeptId = selectedItem.txtValue
      viewModel.callFetchAllAdvertiseListApi()
    }
  }

  companion object {
    const val TAG: String = "ADVERTISELIST_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, AdvertiseListActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
