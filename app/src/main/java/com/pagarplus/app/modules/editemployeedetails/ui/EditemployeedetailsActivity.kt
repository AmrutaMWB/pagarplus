package com.pagarplus.app.modules.editemployeedetails.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.appcomponents.views.TimePickerFragment
import com.pagarplus.app.databinding.ActivityEditemployeedetailsBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminemplist.ui.AdminemplistActivity
import com.pagarplus.app.modules.createbranch.ui.CreateBranchActivity
import com.pagarplus.app.modules.editemployeedetails.`data`.viewmodel.EditemployeedetailsVM
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.modules.itemlistdialog.ui.ItemlistDialog
import com.pagarplus.app.modules.payment.ui.PaymentActivity
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.network.models.adminEditEmpdata.AdminEditEmployeeResponse
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItem
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItemProofsItem
import com.pagarplus.app.network.models.adminEditEmpdata.UpdateEmployeeResponse
import com.pagarplus.app.network.models.createsignup.GetStateListItem
import com.pagarplus.app.network.models.fetchgetbranchlist.FetchGetBranchListResponseListItem
import com.pagarplus.app.network.models.fetchgetdepartmentlist.FetchGetDepartmentListResponseListItem
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponseListItem
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String
import kotlin.Unit

class EditemployeedetailsActivity :
    BaseActivity<ActivityEditemployeedetailsBinding>(R.layout.activity_editemployeedetails),
  ItemlistDialog.OnCompleteListener, BranchDeptlistDialog.OnCompleteListener {
  private val viewModel: EditemployeedetailsVM by viewModels<EditemployeedetailsVM>()
  private lateinit var mBackImageBytes:ByteArray
  private lateinit var mFrontImageBytes:ByteArray
  private var imageFile: File? = null
  var mFrontPicUri_img: Uri?=null
  var mBackPicUri_img: Uri?=null
  var front_img_from_camera: String? = null
  var back_img_from_camera: String? = null
  var front_img_pathfrom_api: String? = null
  var back_img_pathfrom_api: String? = null
  var sel_proofID: String? = null
  var proofID: Int? = 0
  var proofListsize: Int? = 0
  var ProofIDnumber: String? = null
  var arrindex: Int? = -1
  var mBranchList= arrayListOf<GetStateListItem>()
  var mDeptList= arrayListOf<FetchGetDepartmentListResponseListItem>()
  var mIDProofList= arrayListOf<FetchGetIDProofListResponseListItem>()
  lateinit var serverDateFormat: SimpleDateFormat
  lateinit var userDateFormat: SimpleDateFormat
  lateinit var Idalertdialog: AlertDialog

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    val pattern = "dd/MM/yyyy HH:mm:ss"
    serverDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    val userPattern = "dd-MM-yyyy"
    userDateFormat = SimpleDateFormat(userPattern, Locale.getDefault())

    viewModel.editemployeedetailsModel.value?.empID = viewModel.navArguments?.getString(IntentParameters.EmpID)?.toInt()
    viewModel.callFetchGetEmpDataApi()

    binding.editemployeedetailsVM = viewModel
    lifecycleScope.launch {
      mBranchList = ApiUtil(this@EditemployeedetailsActivity).getBranchList(viewModel.userdetails?.orgID)
      mDeptList = ApiUtil(this@EditemployeedetailsActivity).getDeptList()
      mIDProofList = ApiUtil(this@EditemployeedetailsActivity).getIDProofList()
      Log.e("IDProoflist", "$mIDProofList")
    }

    this.hideKeyboard()
  }

  override fun setUpClicks(): Unit {
    binding.btnSubmit.setOnClickListener {
      if(isValidate()){
        this.hideKeyboard()
        viewModel.callUpdateEmployeeApi()
      }
    }

    binding.etEdtTxtfirstname.addTextChangedListener(TextFieldValidation(binding.etEdtTxtfirstname))
    binding.etEdtTxtmobileNo.addTextChangedListener(TextFieldValidation(binding.etEdtTxtmobileNo))
    binding.etEdtTxtemail.addTextChangedListener(TextFieldValidation(binding.etEdtTxtemail))
    binding.etEdtTxtSalary.addTextChangedListener(TextFieldValidation(binding.etEdtTxtSalary))
    binding.etEdtTxtdateofjoining.addTextChangedListener(TextFieldValidation(binding.etEdtTxtdateofjoining))
    binding.etEdtTxtState.addTextChangedListener(TextFieldValidation(binding.etEdtTxtState))
    binding.etEdtTxtCiity.addTextChangedListener(TextFieldValidation(binding.etEdtTxtCiity))
    binding.etEdtTxtPwd.addTextChangedListener(TextFieldValidation(binding.etEdtTxtPwd))
    binding.etEdtTxtcnfPwd.addTextChangedListener(TextFieldValidation(binding.etEdtTxtcnfPwd))

    binding.etEdtTxtdateofjoining.setOnClickListener {
      val destinationInstance = DatePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        DatePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
        binding.etEdtTxtdateofjoining.setText(selected)
      }
    }

    binding.etEdtTxtCheckin.setOnClickListener {
      val destinationInstance = TimePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        TimePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedDate)
        binding.etEdtTxtCheckin.setText(selected)
      }
    }

    binding.etEdtTxtCheckout.setOnClickListener {
      val destinationInstance = TimePickerFragment.getInstance()
      destinationInstance.show(
        this.supportFragmentManager,
        TimePickerFragment.TAG
      ) { selectedDate ->
        val selected = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedDate)
        binding.etEdtTxtCheckout.setText(selected)
      }
    }

    binding.btnBack.setOnClickListener {
      val intent = AdminemplistActivity.getIntent(this, null)
      startActivity(intent)
    }

    binding.btnSelectDept.setOnClickListener {
      SelectDeptDialog()
    }
    binding.btnSelectBranch.setOnClickListener {
      SelectBranchDialog()
    }

    binding.btnSelectID1.setOnClickListener {
      if(proofListsize!! > 0){
        arrindex = 0
        showEditIDDialog(arrindex!!)
      }else{
        showUploadDialog()
      }
    }
    binding.btnSelectID2.setOnClickListener {
      if(proofListsize!! > 1){
        arrindex = 1
        showEditIDDialog(arrindex!!)
      }else{
        showUploadDialog()
      }
    }

    binding.etEdtTxtState.setOnClickListener{
      /*execute state list api*/
      callpopup(true)
    }

    binding.etEdtTxtCiity.addTextChangedListener(object : TextWatcher {

      override fun afterTextChanged(s: Editable) {
      }

      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count == 2) {
          if (viewModel.editemployeedetailsModel.value?.etEdtTxtState.isNullOrEmpty()) {
            binding.outlinedStateField.setError("Please select state")
            binding.etEdtTxtCiity.isEnabled = false
          } else {
            if (count == 3) {
              callpopup(false, viewModel.editemployeedetailsModel.value?.txtStateID!!, s.toString())
            }
          }
        }
      }
    })
  }

  /*validate eidttext fields*/
  private fun isValidate(): Boolean =
    validateRequired(binding.outlinedFirstnameField,binding.etEdtTxtfirstname) &&
            validateMobile(binding.outlinedMobileField,binding.etEdtTxtmobileNo) &&
            validateEmail(binding.outlinedEmailField,binding.etEdtTxtemail) &&
            validateRequired(binding.outlinedDOJField,binding.etEdtTxtdateofjoining) &&
            validateRequired(binding.outlinedStateField,binding.etEdtTxtState) &&
            validateRequired(binding.outlinedCityField,binding.etEdtTxtCiity) &&
            validateRequired(binding.outlinedSalaryField,binding.etEdtTxtSalary)

  /**
   * applying text watcher on each text field
   */
  inner class TextFieldValidation(private val view: View) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      // checking ids of each text field and applying functions accordingly.
      when (view.id) {
        R.id.etEdtTxtfirstname -> {
          validateRequired(binding.outlinedFirstnameField,binding.etEdtTxtfirstname)
        }
        R.id.etEdtTxtmobileNo -> {
          validateMobile(binding.outlinedMobileField,binding.etEdtTxtmobileNo)
        }
        R.id.etEdtTxtemail -> {
          validateEmail(binding.outlinedEmailField,binding.etEdtTxtemail)
        }
        R.id.etEdtTxtdateofjoining -> {
          validateRequired(binding.outlinedDOJField,binding.etEdtTxtdateofjoining)
        }
        R.id.etEdtTxtState -> {
          validateRequired(binding.outlinedStateField,binding.etEdtTxtState)
        }
        R.id.etEdtTxtCiity -> {
          validateRequired(binding.outlinedCityField,binding.etEdtTxtCiity)
        }
        R.id.etEdtTxtSalary -> {
          validateRequired(binding.outlinedSalaryField,binding.etEdtTxtSalary)
        }
      }
    }
  }

  fun callpopup(isState:Boolean,stateId:Int=0,cityKeyword: String=""){
    var bundle = Bundle()
    bundle.putBoolean(IntentParameters.IsStateOrCity, isState)
    if(!isState)
      bundle.putInt(IntentParameters.StateId, stateId)
    bundle.putString(IntentParameters.CityKeyword, cityKeyword)

    var itemlistDialog = ItemlistDialog.getInstance(bundle, this)
    itemlistDialog.show(supportFragmentManager, null)
  }

  /*display alert dialog to select Branch*/
  fun SelectBranchDialog(){
    val arrayAdapterText = ArrayAdapter(this, R.layout.spinner_item, R.id.txtTitle,mBranchList.map { it.text })
    val arrayAdapterValue = ArrayAdapter(this, R.layout.spinner_item, R.id.txtTitle,mBranchList.map { it.value })
    val builderSingle = AlertDialog.Builder(this)
    builderSingle.setTitle("Select Branch")

    builderSingle.setNegativeButton(
      "cancel"
    ) { dialog, which -> dialog.dismiss() }

    builderSingle.setAdapter(arrayAdapterText) {
        dialog, which ->
      val strName = arrayAdapterText.getItem(which)
      binding.txtSelectBranch.setText(strName)
      viewModel.editemployeedetailsModel.value?.txtBranchId = arrayAdapterValue.getItem(which)
    }

    builderSingle.show()
  }

  /*display alert dialog to select Department*/
  fun SelectDeptDialog(){
    val arrayAdapterText = ArrayAdapter(this, R.layout.spinner_item, R.id.txtTitle,mDeptList.map { it.text })
    val arrayAdapterValue = ArrayAdapter(this, R.layout.spinner_item, R.id.txtTitle,mDeptList.map { it.value })
    val builderSingle = AlertDialog.Builder(this)
    builderSingle.setTitle("Select Department")

    builderSingle.setNegativeButton(
      "cancel"
    ) { dialog, which -> dialog.dismiss() }

    builderSingle.setAdapter(arrayAdapterText) {
        dialog, which ->
      val strName = arrayAdapterText.getItem(which)
      binding.txtSelectDepartme.setText(strName.toString())
      viewModel.editemployeedetailsModel.value?.txtDeptId = arrayAdapterValue.getItem(which)
    }
    builderSingle.show()
  }

  /*ID Proof Upload dialog if not uploaded any*/
  fun showEditIDDialog(arrayIndex: Int) {
    Log.e("idproofsize", viewModel.editemployeedetailsModel.value?.Prooflist?.size.toString())
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()
    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.id_upload_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatButton>(R.id.iv_close)
    val img_camera_front = dialogView.findViewById<ImageView>(R.id.imageCameraFront)
    val img_camera_back = dialogView.findViewById<ImageView>(R.id.imageCameraBack)
    val txt_viewimg_front = dialogView.findViewById<TextView>(R.id.txtViewImagefront)
    val txt_viewimg_back = dialogView.findViewById<TextView>(R.id.txtViewImageBack)
    val btn_next = dialogView.findViewById<AppCompatButton>(R.id.btnNext)
    val txt_label = dialogView.findViewById<AppCompatTextView>(R.id.tv_label)
    val spnIDProof = dialogView.findViewById<Spinner>(R.id.SpnIDProof)
    val IDProofNumber = dialogView.findViewById<EditText>(R.id.EdtTxtIDNumber)

    Idalertdialog = dialogBuilder.create()
    Idalertdialog.show();

    btn_next.setText("Update")

    if(viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)?.proofTypeName.equals(null)) {
      txt_label.setText(R.string.lbl_upload)
    }else{
      txt_label.setText(viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)?.proofTypeName)
    }

    val arrAdaptertext = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mIDProofList.map { it.text })
    val arrAdaptervalue = ArrayAdapter(this, R.layout.spinner_item, R.id.txtTitle, mIDProofList.map { it.value })
    arrAdaptertext.notifyDataSetChanged()
    spnIDProof.adapter = arrAdaptertext
    spnIDProof.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        sel_proofID = arrAdaptervalue.getItem(position).toString()

        if(arrayIndex == 0) {
          binding.txtIDProof1.setText(arrAdaptertext.getItem(position).toString())
        }else{
          binding.txtIDProof2.setText(arrAdaptertext.getItem(position).toString())
        }

        if(viewModel.editemployeedetailsModel.value?.Prooflist?.contains(
            FetchEditEmployeeDetailsResponseListItemProofsItem(sel_proofID)) == true){
          val dialogBuilder = AlertDialog.Builder(this@EditemployeedetailsActivity)
          dialogBuilder.setTitle("Error")
          // set message of alert dialog
          dialogBuilder.setMessage(R.string.msg_duplicate_id)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(R.string.btn_prooceed, DialogInterface.OnClickListener {
                dialog, id ->
              dialog.dismiss()
            })
          // create dialog box
          val alert = dialogBuilder.create()
          // show alert dialog
          alert.show()
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {
        // Another interface callback
      }
    }

    if(viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)!!.proofTypeID.equals(null)){
      spnIDProof.isVisible = true
    }else {
      spnIDProof.isVisible = false
      sel_proofID = viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)!!.proofTypeID
    }
    IDProofNumber.setText(viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)!!.proofNumber)
    proofID = viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)!!.proofID

    IDProofNumber.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val len = s.toString().length
        if (before == 0 && (len == 4 || len == 9 || len == 14)) IDProofNumber.append(" ")
      }
      override fun afterTextChanged(s: Editable) {}
    })

    if(!viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)!!.frontImageUrl.isNullOrEmpty()) {
      front_img_pathfrom_api = viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)!!.frontImageUrl
      back_img_pathfrom_api = viewModel.editemployeedetailsModel.value?.Prooflist?.get(arrayIndex)!!.backImageUrl
      Glide.with(this).load(front_img_pathfrom_api).into(img_camera_front);
      Glide.with(this).load(back_img_pathfrom_api).into(img_camera_back);
      txt_viewimg_back.isVisible = true
      txt_viewimg_front.isVisible = true
    }

    iv_close_dialog.setOnClickListener {
      Idalertdialog.dismiss()
    }

    txt_viewimg_front.setOnClickListener {
      if(front_img_from_camera.isNullOrEmpty()) {
        EnlargeImageDialog(mFrontPicUri_img,front_img_pathfrom_api!!)
      }else{
        EnlargeImageDialog(mFrontPicUri_img!!,"")
      }
    }

    txt_viewimg_back.setOnClickListener {
      if(back_img_from_camera.isNullOrEmpty()) {
        EnlargeImageDialog(mBackPicUri_img,back_img_pathfrom_api!!)
      }else{
        EnlargeImageDialog(mBackPicUri_img!!,"")
      }
    }

    img_camera_front.setOnClickListener{
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          img_camera_front.setImageURI(path)
          mFrontPicUri_img=path
          setImagePath(path,"front")
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
      //txt_viewimg_front.isVisible = true
    }

    img_camera_back.setOnClickListener{
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          img_camera_back.setImageURI(path)
          mBackPicUri_img=path
          setImagePath(path,"back")
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }

    btn_next.setOnClickListener{
      if(viewModel.editemployeedetailsModel.value?.Prooflist?.contains(
          FetchEditEmployeeDetailsResponseListItemProofsItem(sel_proofID)) == true){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Error")
        // set message of alert dialog
        dialogBuilder.setMessage(R.string.msg_duplicate_id)
          // if the dialog is cancelable
          .setCancelable(false)
          // positive button text and action
          .setPositiveButton(R.string.btn_prooceed, DialogInterface.OnClickListener {
              dialog, id ->
            dialog.dismiss()
          })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
      }else {
        ProofIDnumber = IDProofNumber.getText().toString()
        if (sel_proofID.equals("1")) {
          if (ProofIDnumber!!.isAdhar()) {
            showConfirmationdialog(arrayIndex)
          } else {
            Toast.makeText(
              applicationContext,
              "Please enter valid Aadhar number",
              Toast.LENGTH_LONG
            ).show();
          }
        } else if (sel_proofID.equals("2")) {
          if (ProofIDnumber!!.isPan()) {
            showConfirmationdialog(arrayIndex)
          } else {
            Toast.makeText(applicationContext, "Please enter valid Pan number", Toast.LENGTH_LONG)
              .show();
          }
        } else {
          showConfirmationdialog(arrayIndex)
        }
      }
    }
  }

  /*ID Proof Upload dialog if not uploaded any*/
  fun Activity.showUploadDialog() {
    Log.e("idproofsize", viewModel.editemployeedetailsModel.value?.Prooflist?.size.toString())

    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()
    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.id_upload_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatButton>(R.id.iv_close)
    val img_camera_front = dialogView.findViewById<ImageView>(R.id.imageCameraFront)
    val img_camera_back = dialogView.findViewById<ImageView>(R.id.imageCameraBack)
    val txt_viewimg_front = dialogView.findViewById<TextView>(R.id.txtViewImagefront)
    val txt_viewimg_back = dialogView.findViewById<TextView>(R.id.txtViewImageBack)
    val btn_next = dialogView.findViewById<AppCompatButton>(R.id.btnNext)
    val spnIDProof = dialogView.findViewById<Spinner>(R.id.SpnIDProof)
    val IDProofNumber = dialogView.findViewById<EditText>(R.id.EdtTxtIDNumber)
    val txt_label = dialogView.findViewById<AppCompatTextView>(R.id.tv_label)

    txt_label.setText(R.string.lbl_upload)

    val alertDialog = dialogBuilder.create()
    alertDialog.show();

    val arrAdaptertext = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mIDProofList.map { it.text })
    val arrAdaptervalue = ArrayAdapter(this, R.layout.spinner_item, R.id.txtTitle, mIDProofList.map { it.value })
    arrAdaptertext.notifyDataSetChanged()
    spnIDProof.adapter = arrAdaptertext
    spnIDProof.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        sel_proofID = arrAdaptervalue.getItem(position).toString()
      }
      override fun onNothingSelected(parent: AdapterView<*>?) {
        // Another interface callback
      }
    }

    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }

    txt_viewimg_front.setOnClickListener {
      EnlargeImageDialog(mFrontPicUri_img!!,"")
    }

    txt_viewimg_back.setOnClickListener {
      EnlargeImageDialog(mFrontPicUri_img!!,"")
    }

    img_camera_front.setOnClickListener{
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          img_camera_front.setImageURI(path)
          mFrontPicUri_img=path
          setImagePath(path,"front")
          txt_viewimg_front.isVisible = true
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
      //txt_viewimg_front.isVisible = true
    }

    img_camera_back.setOnClickListener{
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          img_camera_back.setImageURI(path)
          mBackPicUri_img=path
          setImagePath(path,"back")
          txt_viewimg_back.isVisible = true
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
      //txt_viewimg_back.isVisible = true
    }

    btn_next.setOnClickListener{
      if(viewModel.editemployeedetailsModel.value?.Prooflist?.contains(
          FetchEditEmployeeDetailsResponseListItemProofsItem(sel_proofID)) == true){
        val dialogBuilder = AlertDialog.Builder(this@showUploadDialog)
        dialogBuilder.setTitle("Error")
        // set message of alert dialog
        dialogBuilder.setMessage(R.string.msg_duplicate_id)
          // if the dialog is cancelable
          .setCancelable(false)
          // positive button text and action
          .setPositiveButton(R.string.btn_prooceed, DialogInterface.OnClickListener {
              dialog, id ->
            dialog.dismiss()
          })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
      }else {
        if (sel_proofID.equals("1")) {
          ProofIDnumber = IDProofNumber.getText().toString()
          if (ProofIDnumber!!.isAdhar()) {
            showConfirmationdialog(-1)
          } else {
            Toast.makeText(
              applicationContext,
              "Please enter valid Aadhar number",
              Toast.LENGTH_LONG
            ).show();
          }
        } else if (sel_proofID.equals("2")) {
          ProofIDnumber = IDProofNumber.getText().toString()
          if (ProofIDnumber!!.isPan()) {
            showConfirmationdialog(-1)
          } else {
            Toast.makeText(applicationContext, "Please enter valid Pan Number", Toast.LENGTH_LONG)
              .show();
          }
        } else {
          ProofIDnumber = IDProofNumber.getText().toString()
          showConfirmationdialog(-1)
        }
      }
    }
  }

  /*idupload dialog confrimation on next/ update click*/
  @SuppressLint("NewApi")
  fun showConfirmationdialog(arindex: Int){
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setTitle("Confirmation")
    // set message of alert dialog
    dialogBuilder.setMessage(R.string.msg_idproof_update)
      // if the dialog is cancelable
      .setCancelable(false)
      // positive button text and action
      .setPositiveButton("Yes", DialogInterface.OnClickListener {
          dialog, id ->

        if(mBackPicUri_img != null) {
          viewModel.BackimageUpload(ImageFolders.Idproof, mBackPicUri_img!!,mBackImageBytes)
        }

        if(mFrontPicUri_img != null) {
          viewModel.FrontimageUpload(ImageFolders.Idproof, mFrontPicUri_img!!,mFrontImageBytes)

        }else{
          viewModel.editemployeedetailsModel.value?.Prooflist?.map {
            FetchEditEmployeeDetailsResponseListItemProofsItem(
              ProofIDnumber.toString(), front_img_pathfrom_api,
              back_img_pathfrom_api, sel_proofID, proofID
            )
            Log.e("Prroflist", viewModel.editemployeedetailsModel.value?.Prooflist.toString())
            dialog.dismiss()
          }
        }
      })
      .setNegativeButton("No", DialogInterface.OnClickListener {
          dialog, id -> dialog.cancel()
      })
    // create dialog box
    val alert = dialogBuilder.create()
    // show alert dialog
    alert.show()
  }

  fun setArrayMap(arindex: Int){
    if(arindex != -1){
      viewModel.editemployeedetailsModel.value?.Prooflist?.set(arindex,
        FetchEditEmployeeDetailsResponseListItemProofsItem(
          ProofIDnumber.toString(), front_img_from_camera,
          back_img_from_camera, sel_proofID, proofID
        )
      )
      /*set array index to negative and set all value to null to find next array index to update idproof*/
      arrindex = -1
      sel_proofID = null
      proofID = 0
    }else{
      viewModel.editemployeedetailsModel.value?.Prooflist?.add(
        FetchEditEmployeeDetailsResponseListItemProofsItem(
          ProofIDnumber.toString(), front_img_from_camera,
          back_img_from_camera, sel_proofID, proofID
        )
      )
      Idalertdialog.dismiss()
      /*set all value to null to find next array index to update idproof*/
      sel_proofID = null
      proofID = 0
    }
  }

  /*get image path from file*/
  fun setImagePath(picUri: Uri,selView: String){
    val uriPathHelper = URIPathHelper()
    val filePath = uriPathHelper.getPath(this, picUri)
    if(filePath!=null)
      imageFile = File(filePath)
    if(selView.equals("front")) {
      mFrontImageBytes = FileUploadHelper.compressedImageFile(picUri, this)
    }else{
      mBackImageBytes = FileUploadHelper.compressedImageFile(picUri, this)
    }
    Log.e("ImagePath", "path is $filePath")
  }

  /*view idproof image in fullview*/
  fun EnlargeImageDialog(uri: Uri?,urlpath: String) {
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
    if(uri != null) {
      imgview.setImageURI(uri)
    }else{
      Glide.with(this).load(urlpath).into(imgview)
    }
  }

  /*create employee api observe method */
  override fun addObservers(): Unit {
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
    viewModel.fetchGetEmpLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessGetEmployee(it)
      } else if(it is ErrorResponse) {
        onErrorCreateCreateEmployee(it.data ?:Exception())
      }
    }
    viewModel.updateEmpLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessUpdateEmployee(it)
      } else if(it is ErrorResponse) {
        onErrorCreateCreateEmployee(it.data ?:Exception())
      }
    }
    /*get imagepath from fileUpload api on success*/
    /*upload front image and get path*/
    viewModel.FrontimageUploadLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        front_img_from_camera = response?.imagePath
        setArrayMap(arrindex!!)
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message, Toast.LENGTH_LONG).show();
      }
    }

    /*upload back image and get path*/
    viewModel.BackimageUploadLiveData.observe(this) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        back_img_from_camera = response?.imagePath
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message, Toast.LENGTH_LONG).show();
      }
    }
  }

  private fun onSuccessGetEmployee(response: SuccessResponse<AdminEditEmployeeResponse>): Unit {
    viewModel.bindEditEmployeeResponse(response.data.list?.get(0) ?: FetchEditEmployeeDetailsResponseListItem())
    proofListsize = viewModel.editemployeedetailsModel.value?.Prooflist?.size
    formatDate(viewModel.editemployeedetailsModel.value?.etEdtTxtdatofjoining!!)
    /*check branch if null show text else show selected branch name*/
    if(viewModel.editemployeedetailsModel.value?.txtSelectBranch.isNullOrEmpty()){
      viewModel.editemployeedetailsModel.value?.txtSelectBranch = "Select Branch"
    }
    if(viewModel.editemployeedetailsModel.value?.txtSelectDepartme.isNullOrEmpty()){
      viewModel.editemployeedetailsModel.value?.txtSelectDepartme = "Select Department"
    }

    /*change date of joining date format*/
    val inDate = serverDateFormat.parse(viewModel.editemployeedetailsModel.value?.etEdtTxtdatofjoining)
    viewModel.editemployeedetailsModel.value?.etEdtTxtdatofjoining = userDateFormat.format(inDate)

    Toast.makeText(this,proofListsize.toString(),Toast.LENGTH_SHORT).show()
    if(proofListsize!! > 0) {
      if (viewModel.editemployeedetailsModel.value?.Prooflist?.get(0)?.proofTypeName.isNullOrEmpty()) {
        binding.txtIDProof1.setText(R.string.lbl_id_proof)
        binding.btnSelectID1.text = "Select"
      } else {
        binding.txtIDProof1.setText(viewModel.editemployeedetailsModel.value?.Prooflist?.get(0)?.proofTypeName)
        binding.btnSelectID1.text = "View"
      }

      if(proofListsize!! > 2) {
        if (viewModel.editemployeedetailsModel.value?.Prooflist?.get(1)?.proofTypeName.isNullOrEmpty()) {
          binding.txtIDProof2.setText(R.string.lbl_id_proof)
          binding.btnSelectID2.text = "Select"
        } else {
          binding.txtIDProof2.setText(viewModel.editemployeedetailsModel.value?.Prooflist?.get(1)?.proofTypeName)
          binding.btnSelectID2.text = "View"
        }
      }
    }
  }

  private fun onSuccessUpdateEmployee(response: SuccessResponse<UpdateEmployeeResponse>): Unit {
    this.alert(MyApp.getInstance().getString(R.string.lbl_status),"${response.`data`.message}") {
      neutralButton {
        if (response.data.status == true) {
          val destIntent = AdminemplistActivity.getIntent(context, null)
          finish()
          startActivity(destIntent)
        }else{
          it.dismiss()
        }
      }
    }
    viewModel.bindUpdateEmployeeResponse(response.data)
  }

  private fun onErrorCreateCreateEmployee(exception: Exception): Unit {
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
        this.alert(MyApp.getInstance().getString(R.string.lbl_status),errMessage) {
          neutralButton {
          }
        }
      }
    }
  }

  fun formatDate(selDate: String):String {
    // Read the value until the minutes only
    val pattern = "dd/MM/yyyy HH:mm:ss"
    val serverDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    // Format the date output, as you wish to see, after we read the Date() value
    val userPattern = "dd-MM-yyyy"
    val userDateFormat = SimpleDateFormat(userPattern, Locale.getDefault())
    val contime = serverDateFormat.parse(selDate)
    return userDateFormat.format(contime)
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("State","$selectedItem")
    if(selectedItem.isState==true){
      binding.outlinedStateField.setError("")
      binding.outlinedStateField.isErrorEnabled = false
      binding.etEdtTxtCiity.isEnabled = true
      binding.etEdtTxtState.setText(selectedItem.txtName)
      viewModel.editemployeedetailsModel.value?.txtStateID = selectedItem.txtValue
    }else{
      binding.etEdtTxtCiity.setText(selectedItem.txtName)
      viewModel.editemployeedetailsModel.value?.txtCityID = selectedItem.txtValue
    }
  }

  companion object {
    const val TAG: String = "EDITEMPLOYEEDETAILS_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, EditemployeedetailsActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }

  override fun onBackPressed() {
    val builder = AlertDialog.Builder(this)
    //set message for alert dialog
    builder.setMessage("Do you want to leave this page?")
    //performing positive action
    builder.setPositiveButton("Yes") { dialogInterface, which ->
      val destIntent = AdminemplistActivity.getIntent(this, null)
      startActivity(destIntent)
      finish()
    }
    //performing negative action
    builder.setNegativeButton("No") { dialogInterface, which ->
      dialogInterface.dismiss()
    }
    // Create the AlertDialog
    val dialog: AlertDialog = builder.create()
    // Set other dialog properties
    dialog.setCancelable(false)
    dialog.show()
  }
}
