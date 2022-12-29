package com.pagarplus.app.modules.advertise.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.databinding.ActivityAdvertiseBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.advertise.data.viewmodel.AdvertiseVM
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AdvertiseActivity : BaseActivity<ActivityAdvertiseBinding>(R.layout.activity_advertise), BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: AdvertiseVM by viewModels<AdvertiseVM>()
  private var imageFile: File? = null
  private lateinit var mImageBytes:ByteArray
  lateinit var mPicUri_img: Uri
  var img_pathfrom_api: String? = null
  var isEditAdvertise: String? = "false"
  private lateinit var file:File
  private lateinit var camIntent:Intent
  private lateinit var galIntent:Intent
  private lateinit var cropIntent:Intent
  var RequestPermissionCode = 100

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    isEditAdvertise = viewModel.navArguments?.getString(IntentParameters.IsEditAdvertise)

    if(isEditAdvertise.equals("true")){
      viewModel.advertiseModel.value?.bannerId = viewModel.navArguments?.getInt(IntentParameters.BannerID)
      viewModel.advertiseModel.value?.txtfromdate = viewModel.navArguments?.getString(IntentParameters.fromDate)
      viewModel.advertiseModel.value?.txttodate = viewModel.navArguments?.getString(IntentParameters.toDate)
      viewModel.advertiseModel.value?.txtDescription = viewModel.navArguments?.getString(IntentParameters.TxtMessage)
      viewModel.advertiseModel.value?.txtTitle = viewModel.navArguments?.getString(IntentParameters.TxtTitle)
      viewModel.advertiseModel.value?.txtImagePath = viewModel.navArguments?.getString(IntentParameters.imagePath)
      viewModel.advertiseModel.value?.txtBranchId = viewModel.navArguments?.getInt(IntentParameters.BranchID)
      viewModel.advertiseModel.value?.txtDeptId = viewModel.navArguments?.getInt(IntentParameters.DeptID)
      binding.btnPost.isVisible = false
      binding.btnUpdate.isVisible = true

      binding.txtAllBranch.setText(viewModel.navArguments?.getString(IntentParameters.Branch))
      binding.txtAllDepartment.setText(viewModel.navArguments?.getString(IntentParameters.Dept))
    }else{
      binding.btnPost.isVisible = true
      binding.btnUpdate.isVisible = false
    }

    /*check image is empty or not*/
    if(viewModel.advertiseModel.value?.txtImagePath.isNullOrEmpty()){
      binding.imageloadImg.isVisible = false
      binding.txtClickImage.setText("Upload Image")
    }else{
      binding.imageloadImg.isVisible = true
      binding.txtClickImage.setText("Change Image")
    }
    binding.advertiseVM = viewModel

    if(viewModel.userdetails?.planType.equals("Basic")){
      binding.txtAllBranch.isVisible = false
      binding.txtAllDepartment.isVisible = false
    }else{
      binding.txtAllBranch.isVisible = true
      binding.txtAllDepartment.isVisible = true
    }
  }

  override fun setUpClicks(): Unit {
    binding.imageCamera.setOnClickListener {
      //openDialog()
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          binding.imageCamera.isVisible = true
          binding.imageCamera.setImageURI(path)
          mPicUri_img=path
          setImagePath(path)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }

    binding.btnPost.setOnClickListener {
      this@AdvertiseActivity.hideKeyboard()
      if(mPicUri_img != null){
        if(binding.edtfromdate.getText().toString().isNullOrEmpty() ||
          binding.edttodate.getText().toString().isNullOrEmpty()){
          Toast.makeText(applicationContext,"please select date", Toast.LENGTH_LONG).show();
        }else {
          viewModel.ImageUpload(ImageFolders.AdvertiseImages, mPicUri_img!!,mImageBytes)
        }
      }else {
        if(binding.txtDescription.getText().toString().isNullOrEmpty()){
          Toast.makeText(applicationContext,"please type message", Toast.LENGTH_LONG).show();
        }else{

        }
      }
    }

    binding.btnUpdate.setOnClickListener {
      this@AdvertiseActivity.hideKeyboard()
      if(mPicUri_img != null){
        if(binding.edtfromdate.getText().toString().isNullOrEmpty() ||
          binding.edttodate.getText().toString().isNullOrEmpty()){
          Toast.makeText(applicationContext,"please select date", Toast.LENGTH_LONG).show();
        }else {
          viewModel.ImageUpload(ImageFolders.AdvertiseImages, mPicUri_img!!,mImageBytes)
        }
      }else {
        viewModel.callUpdateBannerApi()
      }
    }

    binding.btnBack.setOnClickListener {
      finish()
    }

    binding.edtfromdate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.edtfromdate.setText(selected)
      }
    }

    binding.edttodate.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.edttodate.setText(selected)
      }
    }

    binding.txtAllBranch.setOnClickListener {
      callpopupBranchDept(true)
    }
    binding.txtAllDepartment.setOnClickListener {
      callpopupBranchDept(false)
    }
  }

  private fun enableRuntimePermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
        this,Manifest.permission.CAMERA
      )) {

    }
    else{
      ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.CAMERA),RequestPermissionCode)
    }
  }

  private fun cropImages(){
    /**set crop image*/
    try {
      cropIntent = Intent("com.android.camera.action.CROP")
      cropIntent.setDataAndType(mPicUri_img,"image/*")
      cropIntent.putExtra("crop",true)
      cropIntent.putExtra("outputX",180)
      cropIntent.putExtra("outputY",180)
      cropIntent.putExtra("aspectX",3)
      cropIntent.putExtra("aspectY",4)
      cropIntent.putExtra("scaleUpIfNeeded",true)
      cropIntent.putExtra("return-data",true)
      startActivityForResult(cropIntent,1)
      setImagePath(mPicUri_img)
    }catch (e: ActivityNotFoundException){
      e.printStackTrace()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 0 && resultCode == RESULT_OK){
      cropImages()
    } else if (requestCode == 2){
      if (data != null){
        mPicUri_img = data.data!!
        cropImages()
      }
    }
    else if (requestCode == 1){
      if (data != null){
        val bundle = data.extras
        val bitmap = bundle!!.getParcelable<Bitmap>("data")
        binding.imageCamera.setImageBitmap(bitmap)
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      RequestPermissionCode-> if (grantResults.size>0
        && grantResults[0]== PackageManager.PERMISSION_GRANTED){
        Toast.makeText(this,
          R.string.msg_permission_grant,
          Toast.LENGTH_SHORT).show()
      }
      else{
        Toast.makeText(this,
          R.string.msg_permission_deny,
          Toast.LENGTH_SHORT).show()
      }
    }
  }

  /*call branch/Dept popup*/
  fun callpopupBranchDept(isBranch:Boolean){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsBranchDept, isBranch)
    var itemlistDialog = BranchDeptlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
  }

  /*get image path from file*/
  fun setImagePath(picUri: Uri){
    val uriPathHelper = URIPathHelper()
    val filePath = uriPathHelper.getPath(this, picUri)
    if(filePath!=null)
      imageFile = File(filePath)
    mImageBytes = FileUploadHelper.compressedImageFile(picUri,this)
    Log.e("ImagePath", "path is $filePath")
  }

  override fun addObservers() {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this@AdvertiseActivity) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@AdvertiseActivity.showProgressDialog()
      } else  {
        progressDialog?.dismiss()
      }
    }
    viewModel.createCreateBannerLiveData.observe(this@AdvertiseActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateCreateBanner(it)
      } else if(it is ErrorResponse)  {
        onErrorCreateCreateBanner(it.data ?:Exception())
      }
    }

    viewModel.updateBannerLiveData.observe(this@AdvertiseActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateCreateBanner(it)
      } else if(it is ErrorResponse)  {
        onErrorCreateCreateBanner(it.data ?:Exception())
      }
    }

    /*get imagepath from fileUpload api on success*/
    /*upload image and get path*/
    viewModel.imageUploadLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        img_pathfrom_api = it.data.imagePath
        viewModel.advertiseModel.value?.txtImagePath = img_pathfrom_api
        if(isEditAdvertise.equals("true")){
          viewModel.callUpdateBannerApi()
        }else {
          viewModel.callCreateCreateBannerApi()
        }
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message, Toast.LENGTH_LONG).show();
      }
    }
  }

  private fun onSuccessCreateCreateBanner(response: SuccessResponse<CreateCreateBannerResponse>) {
    if (response.data.status == true) {
      binding.animationSparkle.isVisible = true
      this@AdvertiseActivity.alert("", getString(R.string.msg_advertisement)) {
        neutralButton {
          val destIntent = AdvertiseListActivity.getIntent(context, null)
          finish()
          startActivity(destIntent)
        }
      }
    }else {
      binding.animationSparkle.isVisible = false
      this@AdvertiseActivity.alert("", "${response.`data`.message}") {
        neutralButton {
          it.dismiss()
        }
      }
    }
    viewModel.bindCreateCreateBannerResponse(response.data)
  }

  private fun onErrorCreateCreateBanner(exception: Exception) {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
      is HttpException -> {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
            else JSONObject()
        val errMessage = if(!errorObject.optString("Message").isNullOrEmpty()) {
          errorObject.optString("Message").toString()
        } else {
           exception.response()?.message()?:""
        }
        this@AdvertiseActivity.alert(MyApp.getInstance().getString(R.string.lbl_status),errMessage) {
          neutralButton {
          }
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
    const val TAG: String = "ADVERTISE_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, AdvertiseActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun onBackPressed() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(R.string.msg_closeapp)
    builder.setPositiveButton("Yes") { dialogInterface, which ->
      finish()
    }
    //performing negative action
    builder.setNegativeButton("No") { dialogInterface, which ->
      dialogInterface.dismiss()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()
  }
}
