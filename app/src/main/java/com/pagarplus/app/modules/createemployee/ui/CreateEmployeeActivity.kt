package com.pagarplus.app.modules.createemployee.ui

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.URIPathHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.appcomponents.views.ImagePickerFragmentDialog
import com.pagarplus.app.appcomponents.views.TimePickerFragment
import com.pagarplus.app.databinding.ActivityCreateEmployeeBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminemplist.ui.AdminemplistActivity
import com.pagarplus.app.modules.createbranch.ui.CreateBranchActivity
import com.pagarplus.app.modules.createemployee.data.viewmodel.CreateEmployeeVM
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.ui.BranchDeptlistDialog
import com.pagarplus.app.modules.itemlistdialog.ui.ItemlistDialog
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeResponse
import com.pagarplus.app.network.models.createcreateemployee.ProofItem
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


class CreateEmployeeActivity :
    BaseActivity<ActivityCreateEmployeeBinding>(R.layout.activity_create_employee),
  ItemlistDialog.OnCompleteListener{

  private val viewModel: CreateEmployeeVM by viewModels<CreateEmployeeVM>()
  var mBranchList= arrayListOf<GetStateListItem>()
  var mDeptList= arrayListOf<FetchGetDepartmentListResponseListItem>()
  var mIDProofList= arrayListOf<FetchGetIDProofListResponseListItem>()
  private var imageFile: File? = null
  private lateinit var mBackImageBytes:ByteArray
  private lateinit var mFrontImageBytes:ByteArray
  var mFrontPicUri_img: Uri?=null
  var mBackPicUri_img: Uri?=null
  var front_img_pathfrom_api: String? = null
  var back_img_pathfrom_api: String? = null
  var sel_proofID: String? = null
  var ProofIDnumber: String? = null
  var arrindex: Int? = 0
  private val REQUEST_READ_CONTACTS_PERMISSION = 0
  private val PICK_CONTACT = 1
  var branchDropdown: Boolean = false
  lateinit var Idalertdialog: AlertDialog

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.createEmployeeVM = viewModel

    /*call get branch list nad ID Proof list api from API Util*/
    lifecycleScope.launch {
      mBranchList = ApiUtil(this@CreateEmployeeActivity).getBranchList(viewModel.userdetails?.orgID)
      mDeptList = ApiUtil(this@CreateEmployeeActivity).getDeptList()
      mIDProofList = ApiUtil(this@CreateEmployeeActivity).getIDProofList()
      Log.e("IDProoflist", "$mIDProofList")
      viewModel.createEmployeeModel.value?.txtSelectBranch = mBranchList.get(0).text.toString()
      viewModel.createEmployeeModel.value?.txtBranchId = mBranchList.get(0).value
    }
    requestContactsPermission()
    if(viewModel.profiledetails?.planType.equals("Basic")){
      binding.outlinedEmailField.isVisible = false
      binding.outlinedDOJField.isVisible = false
      binding.outlinedDesignationField.isVisible = false
      binding.outlinedPaidleaveField.isVisible = false
      binding.outlinedSickLeaveField.isVisible = false
      binding.outlinedCheckinField.isVisible = false
      binding.outlinedCheckoutField.isVisible = false
    }else{
      binding.outlinedEmailField.isVisible = true
      binding.outlinedDOJField.isVisible = true
      binding.outlinedDesignationField.isVisible = true
      binding.outlinedPaidleaveField.isVisible = true
      binding.outlinedSickLeaveField.isVisible = true
      binding.outlinedCheckinField.isVisible = true
      binding.outlinedCheckoutField.isVisible = true
      binding.etEdtTxtCheckin.addTextChangedListener(TextFieldValidation(binding.etEdtTxtCheckin))
      binding.etEdtTxtCheckout.addTextChangedListener(TextFieldValidation(binding.etEdtTxtCheckout))
      binding.etEdtTxtdateofjoining.addTextChangedListener(TextFieldValidation(binding.etEdtTxtdateofjoining))
    }

    if(viewModel.profiledetails?.showBranch == false){
      binding.linearRowselectbranch.isVisible = false
    }else{
      binding.linearRowselectbranch.isVisible = true
    }

    if(viewModel.profiledetails?.showDepartment == false){
      binding.linearRowselectdepartme.isVisible = false
    }else{
      binding.linearRowselectdepartme.isVisible = true
    }
  }

  override fun setUpClicks(): Unit {
    binding.btnBack.setOnClickListener {
      val intent = AdminemplistActivity.getIntent(this, null)
      startActivity(intent)
      finish()
    }

    binding.imgContact.setOnClickListener{
      var intent = Intent(Intent.ACTION_PICK)
      intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
      startActivityForResult(intent, 1)
    }

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

    binding.btnSelectDept.setOnClickListener {
      SelectDeptDialog()
    }
    binding.btnSelectBranch.setOnClickListener {
      lifecycleScope.launch {
        mBranchList = ApiUtil(this@CreateEmployeeActivity).getBranchList(viewModel.userdetails?.orgID)
        SelectBranchDialog()
      }
    }
    binding.btnSelectID.setOnClickListener {
      showUploadDialog()
    }
    binding.btnAdd.setOnClickListener {
      showBalanceDialog()
    }
    binding.etEdtTxtState.setOnClickListener{
      callpopup(true)
    }

    binding.etEdtTxtCiity.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable) {
      }

      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if(viewModel.createEmployeeModel.value?.etEdtTxtStateValue.isNullOrEmpty()){
          Toast.makeText(applicationContext,"Please select state",Toast.LENGTH_LONG).show()
        }
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if(viewModel.createEmployeeModel.value?.etEdtTxtStateValue.isNullOrEmpty()) {
          binding.outlinedStateField.setError("Please select state")
          binding.etEdtTxtCiity.isEnabled = false
        }else{
          if (count == 3) {
            callpopup(false, viewModel.createEmployeeModel.value?.StateID!!, s.toString())
          }
        }
      }
    })

    binding.btnSubmit.setOnClickListener {
      if(isValidate()) {
        this@CreateEmployeeActivity.hideKeyboard()
        viewModel.callCreateCreateEmployeeApi()
      }
    }

    binding.etEdtTxtfirstname.addTextChangedListener(TextFieldValidation(binding.etEdtTxtfirstname))
    binding.etEdtTxtmobileNo.addTextChangedListener(TextFieldValidation(binding.etEdtTxtmobileNo))
    binding.etEdtTxtemail.addTextChangedListener(TextFieldValidation(binding.etEdtTxtemail))
    binding.etEdtTxtSalary.addTextChangedListener(TextFieldValidation(binding.etEdtTxtSalary))
    binding.etEdtTxtState.addTextChangedListener(TextFieldValidation(binding.etEdtTxtState))
    binding.etEdtTxtCiity.addTextChangedListener(TextFieldValidation(binding.etEdtTxtCiity))
    binding.etEdtTxtPwd.addTextChangedListener(TextFieldValidation(binding.etEdtTxtPwd))
    binding.etEdtTxtcnfPwd.addTextChangedListener(TextFieldValidation(binding.etEdtTxtcnfPwd))
  }

  /*validate eidttext fields*/
  private fun isValidate(): Boolean =
    if(viewModel.profiledetails?.planType.equals("Basic")) {
      validateRequired(binding.outlinedFirstnameField, binding.etEdtTxtfirstname) &&
              validateMobile(binding.outlinedMobileField, binding.etEdtTxtmobileNo) &&
              validateRequired(binding.outlinedStateField, binding.etEdtTxtState) &&
              validateRequired(binding.outlinedCityField, binding.etEdtTxtCiity) &&
              validateRequired(binding.outlinedSalaryField, binding.etEdtTxtSalary) &&
              validatePassword(binding.outlinedPasswordField, binding.etEdtTxtPwd) &&
              validateConfirmPassword(binding.outlinedCnfPwdField, binding.etEdtTxtcnfPwd, binding.etEdtTxtPwd)
    }else{
      validateRequired(binding.outlinedFirstnameField, binding.etEdtTxtfirstname) &&
              validateMobile(binding.outlinedMobileField, binding.etEdtTxtmobileNo) &&
              validateEmail(binding.outlinedEmailField, binding.etEdtTxtemail) &&
              validateRequired(binding.outlinedDOJField, binding.etEdtTxtdateofjoining) &&
              validateRequired(binding.outlinedStateField, binding.etEdtTxtState) &&
              validateRequired(binding.outlinedCityField, binding.etEdtTxtCiity) &&
              validateRequired(binding.outlinedSalaryField, binding.etEdtTxtSalary) &&
              validatePassword(binding.outlinedPasswordField, binding.etEdtTxtPwd) &&
              validateRequired(binding.outlinedCheckinField, binding.etEdtTxtCheckin) &&
              validateRequired(binding.outlinedCheckoutField, binding.etEdtTxtCheckout) &&
              validateConfirmPassword(binding.outlinedCnfPwdField, binding.etEdtTxtcnfPwd, binding.etEdtTxtPwd)
    }
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
        R.id.etEdtTxtCheckin -> {
          validateRequired(binding.outlinedCheckinField,binding.etEdtTxtCheckin)
        }
        R.id.etEdtTxtCheckout -> {
          validateRequired(binding.outlinedCheckoutField,binding.etEdtTxtCheckout)
        }
        R.id.etEdtTxtPwd -> {
          validatePassword(binding.outlinedPasswordField,binding.etEdtTxtPwd)
        }
        R.id.etEdtTxtcnfPwd -> {
          validateConfirmPassword(binding.outlinedCnfPwdField,binding.etEdtTxtcnfPwd,binding.etEdtTxtPwd)
        }
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String?>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_READ_CONTACTS_PERMISSION && grantResults.size > 0) {
      Log.e("Permission","Permission Granted")
    }
  }

  private fun hasContactsPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
  }

  // Request contact permission if it
  // has not been granted already
  private fun requestContactsPermission() {
    if (!hasContactsPermission()) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_CONTACTS),
        REQUEST_READ_CONTACTS_PERMISSION
      )
    }
  }

  @SuppressLint("Range")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == 1 && resultCode === Activity.RESULT_OK) {
        val contactData: Uri = data?.data ?: return
        val c: Cursor? = contentResolver.query(contactData,null,null,null,null)
        if (c?.moveToFirst()!!) {
            val cName = c!!.getString(c!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            var cNumber = c!!.getString(c!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            //cNumber = cNumber.replaceAll("\\W+","");
            cNumber = cNumber.replace(" ".toRegex(), "")
            Log.e("Contact_Name_Number ", "$cName $cNumber")
            if (cNumber.startsWith("+")) {
              cNumber = cNumber.substring(3)
              Log.e("mobile..", cNumber)
            } else if (cNumber.length > 10) {
              if (cNumber.startsWith("0")) {
                cNumber = cNumber.substring(1)
              } else if (cNumber.startsWith("91")) {
                cNumber = cNumber.substring(2)
              }
            }
            binding.etEdtTxtfirstname.setText(cName)
            binding.etEdtTxtmobileNo.setText(cNumber)
        }
      }
  }

  /*call state/city popup*/
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

    builderSingle.setPositiveButton("Add") { dialog, which ->
      branchDropdown = true
      val destIntent = CreateBranchActivity.getIntent(this, null)
      destIntent.putExtra(IntentParameters.IsNevigate,true)
      startActivity(destIntent)
    }

    builderSingle.setAdapter(arrayAdapterText) {
        dialog, which ->
      val strName = arrayAdapterText.getItem(which)
      binding.txtSelectBranch.setText(strName)
      viewModel.createEmployeeModel.value?.txtBranchId = arrayAdapterValue.getItem(which)
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
      viewModel.createEmployeeModel.value?.txtDeptId = arrayAdapterValue.getItem(which)
    }
    builderSingle.show()
  }

  /*ID Proof Upload dialog*/
  fun Activity.showUploadDialog() {
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
    val txterror = dialogView.findViewById<TextView>(R.id.txterror)

    Idalertdialog = dialogBuilder.create()

    Idalertdialog.show();

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

      IDProofNumber.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          if(sel_proofID.equals("1")) {
            val len = s.toString().length
            if (before == 0 && (len == 4 || len == 9)) IDProofNumber.append(" ")
          }
          if(sel_proofID.equals("3")) {
            val len = s.toString().length
            if (before == 0 && (len == 4)) IDProofNumber.append(" ")
          }
        }

        override fun afterTextChanged(s: Editable) {}
      })

    iv_close_dialog.setOnClickListener {
      Idalertdialog.dismiss()
    }

    txt_viewimg_front.setOnClickListener {
      EnlargeImageDialog(mFrontPicUri_img!!)
    }

    txt_viewimg_back.setOnClickListener {
      EnlargeImageDialog(mBackPicUri_img!!)
    }

    img_camera_front.setOnClickListener{
      ImagePickerFragmentDialog().show(supportFragmentManager)
      { path ->
        if(path!=null) {
          img_camera_front.setImageURI(path)
          mFrontPicUri_img=path
          setImagePath(path)
          txt_viewimg_front.isVisible = true
          mFrontImageBytes = FileUploadHelper.compressedImageFile(mFrontPicUri_img!!,this)
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
          setImagePath(path)
          txt_viewimg_back.isVisible = true
          mBackImageBytes = FileUploadHelper.compressedImageFile(mBackPicUri_img!!,this)
        }else
          Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_select_img), Snackbar.LENGTH_LONG).show()
      }
    }

    btn_next.setOnClickListener{
      if(mFrontPicUri_img != null) {
        if (viewModel.createEmployeeModel.value?.Prooflist?.contains(ProofItem(sel_proofID)) == true) {
          val dialogBuilder = AlertDialog.Builder(this@showUploadDialog)
          dialogBuilder.setTitle("Error")
          // set message of alert dialog
          dialogBuilder.setMessage(R.string.msg_duplicate_id)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
              dialog.dismiss()
            })
          // create dialog box
          val alert = dialogBuilder.create()
          // show alert dialog
          alert.show()
        } else {
          ProofIDnumber = IDProofNumber.getText().toString().replace(" ", "")
          if (sel_proofID.equals("1")) {
            if (ProofIDnumber!!.isAdhar()) {
              txterror.setText("")
              showConfirmDialog()
              img_camera_front.setImageResource(R.drawable.img_camera)
              img_camera_back.setImageResource(R.drawable.img_camera)
              IDProofNumber.setText("")
              txt_viewimg_back.isVisible = false
              txt_viewimg_front.isVisible = false
            } else {
              txterror.setText(R.string.adharerror)
            }
          } else if (sel_proofID.equals("2")) {
            if (ProofIDnumber!!.isPan()) {
              txterror.setText("")
              showConfirmDialog()
              IDProofNumber.setText("")
              img_camera_front.setImageResource(R.drawable.img_camera)
              img_camera_back.setImageResource(R.drawable.img_camera)
              txt_viewimg_back.isVisible = false
              txt_viewimg_front.isVisible = false
            } else {
              txterror.setText(R.string.panrerror)
            }
          } else {
            if (ProofIDnumber!!.isDL()) {
              txterror.setText("")
              showConfirmDialog()
              img_camera_front.setImageResource(R.drawable.img_camera)
              img_camera_back.setImageResource(R.drawable.img_camera)
              txt_viewimg_back.isVisible = false
              txt_viewimg_front.isVisible = false
              IDProofNumber.setText("")
            } else {
              txterror.setText(R.string.DLrerror)
            }
          }
        }
      } else{
        Toast.makeText(applicationContext,"Please capture ID Proof image",Toast.LENGTH_LONG).show();
      }
    }
  }

  fun showConfirmDialog(){
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setTitle("Confirmation")
    // set message of alert dialog
    dialogBuilder.setMessage(R.string.msg_idproof_confirmation)
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
        }
        dialog.dismiss()
      })
      .setNeutralButton("No", DialogInterface.OnClickListener {
          dialog, id -> dialog.cancel()
        if(mBackPicUri_img != null) {
          viewModel.BackimageUpload(ImageFolders.Idproof, mBackPicUri_img!!,mBackImageBytes)
        }

        if(mFrontPicUri_img != null) {
          viewModel.FrontimageUpload(ImageFolders.Idproof, mFrontPicUri_img!!,mFrontImageBytes)
        }
        dialog.dismiss()
        binding.txtIDProof.setText("ID Proof uploaded")
        Idalertdialog.dismiss()
      })
    // create dialog box
    val alert = dialogBuilder.create()
    // show alert dialog
    alert.show()
  }

  /*after successfull uploading ID proof save to array*/
  fun setArrayMap(){
    viewModel.createEmployeeModel.value?.Prooflist?.add(
      arrindex!!,ProofItem(
      sel_proofID, front_img_pathfrom_api, back_img_pathfrom_api,ProofIDnumber))
      arrindex!!+1
  }

  /*get image path from file*/
  fun setImagePath(picUri: Uri){
    val uriPathHelper = URIPathHelper()
    val filePath = uriPathHelper.getPath(this, picUri)
    if(filePath!=null)
      imageFile = File(filePath)
    Log.e("ImagePath", "path is $filePath")
  }

  /*view idproof image in fullview*/
  fun EnlargeImageDialog(uri: Uri) {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()

    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.imageview_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val imgview = dialogView.findViewById<ImageView>(R.id.dialog_imageview)
    val ivclose = dialogView.findViewById<AppCompatButton>(R.id.iv_close)
    val alertDialog = dialogBuilder.create()

    ivclose.setOnClickListener {
      alertDialog.dismiss()
    }

    alertDialog.setCancelable(true)
    alertDialog.show();
    imgview.setImageURI(uri)
  }

  /*Add Balance dialog*/
  fun showBalanceDialog() {
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()

    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.add_balance_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatImageView>(R.id.iv_cross)
    val EdtTxtLoanAdvance = dialogView.findViewById<EditText>(R.id.EdtTxtAdvance)
    val EdtTxtMonthlydeduction = dialogView.findViewById<EditText>(R.id.EdtTxtMonthlydeduction)
    val EdtTxtOldSalBalance = dialogView.findViewById<EditText>(R.id.EdtTxtoldSalBalance)
    val EdtTxtComment = dialogView.findViewById<EditText>(R.id.EdtTxtComment)
    val btn_save = dialogView.findViewById<AppCompatButton>(R.id.btnSave)

    val alertDialog = dialogBuilder.create()

    alertDialog.show();
    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }
    btn_save.setOnClickListener{
      viewModel.createEmployeeModel.value?.loan_advance = EdtTxtLoanAdvance.getText().toString()
      viewModel.createEmployeeModel.value?.monthly_deduction = EdtTxtMonthlydeduction.getText().toString()
      viewModel.createEmployeeModel.value?.old_salary_balance = EdtTxtOldSalBalance.getText().toString()
      viewModel.createEmployeeModel.value?.comment_on_balance = EdtTxtComment.getText().toString()
      alertDialog.dismiss()
      Log.e("balance",EdtTxtLoanAdvance.getText().toString()+" "+EdtTxtMonthlydeduction.getText().toString()
              +" "+EdtTxtOldSalBalance.getText().toString()+""+EdtTxtComment.getText().toString())
      binding.txtAddOldBalance.setText("Balance added")
    }
  }

  /*create employee api observe method */
  override fun addObservers(): Unit {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this@CreateEmployeeActivity) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@CreateEmployeeActivity.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }
    viewModel.createCreateEmployeeLiveData.observe(this@CreateEmployeeActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessCreateCreateEmployee(it)
      } else if(it is ErrorResponse) {
        onErrorMessage(it.data ?:Exception())
      }
    }
    /*get imagepath from fileUpload api on success*/
    /*upload front image and get path*/
    viewModel.FrontimageUploadLiveData.observe(this@CreateEmployeeActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        front_img_pathfrom_api = response?.imagePath
        setArrayMap()
        mFrontPicUri_img = null
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message,Toast.LENGTH_LONG).show();
      }
    }

    /*upload back image and get path*/
    viewModel.BackimageUploadLiveData.observe(this@CreateEmployeeActivity) {
      if(it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        back_img_pathfrom_api = response?.imagePath
        mBackPicUri_img = null
      } else if(it is ErrorResponse) {
        Toast.makeText(applicationContext,it.message,Toast.LENGTH_LONG).show();
      }
    }
  }

  private fun onSuccessCreateCreateEmployee(response: SuccessResponse<CreateCreateEmployeeResponse>):
      Unit {
    this@CreateEmployeeActivity.alert(MyApp.getInstance().getString(R.string.lbl_status),"${response.`data`.message}") {
      neutralButton {
        if (response.data.status == true) {
          binding.animationSparkle.isVisible = true
          val destIntent = AdminemplistActivity.getIntent(context, null)
          finish()
          startActivity(destIntent)
        }else{
          binding.animationSparkle.isVisible = false
          it.dismiss()
        }
      }
    }
    viewModel.bindCreateCreateEmployeeResponse(response.data)
  }

  private fun onErrorMessage(exception: Exception): Unit {
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
        this@CreateEmployeeActivity.alert(MyApp.getInstance().getString(R.string.lbl_status),errMessage) {
          neutralButton {
          }
        }
      }
    }
  }

  override fun <T> onComplete(`object`: T) {
    var selectedItem=`object` as Itemlistdialog1RowModel
    Log.e("State","$selectedItem")
      if (selectedItem.isState == true) {
        binding.outlinedStateField.setError("")
        binding.etEdtTxtCiity.isEnabled = true
        binding.etEdtTxtState.setText(selectedItem.txtName)
        viewModel.createEmployeeModel.value?.StateID = selectedItem.txtValue
      }else {
        binding.etEdtTxtCiity.setText(selectedItem.txtName)
        viewModel.createEmployeeModel.value?.cityID = selectedItem.txtValue
      }
  }

  companion object {
    const val TAG: String = "CREATE_EMPLOYEE_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, CreateEmployeeActivity::class.java)
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

  override fun onResume() {
    super.onResume()
    if(branchDropdown) {
      lifecycleScope.launch {
        mBranchList = ApiUtil(this@CreateEmployeeActivity).getBranchList(viewModel.userdetails?.orgID)
        binding.txtSelectBranch.setText(mBranchList.last().text)
        viewModel.createEmployeeModel.value?.txtBranchId = mBranchList.last().value
        branchDropdown = false
      }
    }else{
      lifecycleScope.launch {
        mBranchList = ApiUtil(this@CreateEmployeeActivity).getBranchList(viewModel.userdetails?.orgID)
      }
      if(!mBranchList.isEmpty()){
        binding.txtSelectBranch.setText(mBranchList.first().text)
        viewModel.createEmployeeModel.value?.txtBranchId = mBranchList.first().value
      }
    }
  }
}
