package com.pagarplus.app.modules.formaemployeeregister.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.modules.formaemployeeregister.data.viewmodel.FormAEmployeeRegisterVM
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.databinding.ActivityFormAEmployeeRegisterBinding
import com.pagarplus.app.extensions.IntentParameters
import com.pagarplus.app.extensions.NoInternetConnection
import com.pagarplus.app.extensions.isJSONObject
import com.pagarplus.app.extensions.showProgressDialog
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponse
import com.pagarplus.app.network.models.adminreport.AdminReportResponse
import com.pagarplus.app.network.models.employeeReports.EmployeeItem
import com.pagarplus.app.network.models.employeeReports.EmployeeReportRequest
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import com.yuvapagar.app.modules.notificationcreatemessage.ui.DetailsAdapter2
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.*
import com.pagarplus.app.BuildConfig


class FormAEmployeeRegisterActivity :
    BaseActivity<ActivityFormAEmployeeRegisterBinding>(R.layout.activity_form_a_employee_register) {
  private val viewModel: FormAEmployeeRegisterVM by viewModels<FormAEmployeeRegisterVM>()
  lateinit var detailsAdapter: DetailsAdapter2
  private  var mCurrentForm:String=""

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    mCurrentForm=intent.getStringExtra(IntentParameters.FormType)?:""
    Log.e("string", "path is $mCurrentForm")
    viewModel.callFetchBranchListApi()
    viewModel.callFetchDepartmentApi()
    binding.formAEmployeeRegisterVM = viewModel

    val formAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.lbl_form_year)
    )
    binding.spinnerSelectyear.adapter = formAdapter
    binding.spinnerSelectyear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(
        parentView: AdapterView<*>?,
        selectedItemView: View?,
        position: Int,
        id: Long
      ) {
        var year = parentView?.getItemAtPosition(position).toString()
        viewModel.formAEmployeeRegisterModel.value?.adminyear = year
        Log.e("adapter", (viewModel.formAEmployeeRegisterModel.value?.adminyear).toString())
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {

      }

    }
  }


  override fun setUpClicks(): Unit {
    binding.imageArrowleft.setOnClickListener {
      finish()
    }


    binding.spinnerSelectEmployee.setOnClickListener {
      showEmployeeDialog()
    }

    binding.btnGeneratePDF.setOnClickListener {
      sendForGenerationRequest()

      Toast.makeText(this, "clicked..", Toast.LENGTH_SHORT).show()
    }

  }

  // method to display forms on click of spinner item //
   fun sendForGenerationRequest(){
     var request= EmployeeReportRequest(
       AdminID =4, Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist
     )
     var requestB= EmployeeReportRequest(AdminID = 4,
                    Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist,
                    FromYear = "2022",  ToYear  = "2023",
       Months =viewModel.formAEmployeeRegisterModel.value?.MonthIdlist)
    var requestC= EmployeeReportRequest(AdminID = 4,Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist, FromYear = "2022",  ToYear  = "2023", Month = "11")
    var requestE= EmployeeReportRequest(AdminID = 4,Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist, FromYear = "2022",  ToYear  = "2023", Month = "11")
    var formlist=resources.getStringArray(R.array.lbl_form_name)

     if(mCurrentForm.equals(formlist.get(1).toString()))
       viewModel.callFetchReportApi(request,this)



     else if(mCurrentForm.equals(formlist.get(2).toString())){
       viewModel.callFetchBReportApi(requestB,this)
     }
     else if(mCurrentForm.equals(formlist.get(3).toString())){
      viewModel.callFetchCReportApi(requestC,this)
     }
     else {
       viewModel.callFetchEReportApi(requestE,this)
     }

   }


//  fun pdfviewer (){
//
//    val file = File(Environment.getExternalStorageDirectory().absolutePath + "/example.pdf")
//    val intent = Intent(Intent.ACTION_VIEW)
//    intent.setDataAndType(Uri.fromFile(file), "application/pdf")
//    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//    val intent1 = Intent.createChooser(intent, "Open With")
//    try {
//      startActivity(intent1)
//    } catch (e: ActivityNotFoundException) {
//      // Instruct the user to install a PDF reader here, or something
//    }
//    startActivity(intent)
//  }

  fun showEmployeeDialog() {
    viewModel.callFetchEmployeeApi()
    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.getLayoutInflater()

    val dialogView = inflater.inflate(R.layout.sel_emp_search_dialog, null)
    dialogBuilder.setView(dialogView).setCancelable(false)

    val iv_close_dialog = dialogView.findViewById<AppCompatButton>(R.id.iv_cross)
    val emp_recyclerlist = dialogView.findViewById<RecyclerView>(R.id.emplistRecycler)
    val txtok = dialogView.findViewById<TextView>(R.id.txtok)
    val searchbar = dialogView.findViewById<SearchView>(R.id.searchEMp)

    val alertDialog = dialogBuilder.create()

    alertDialog.show();

    detailsAdapter = DetailsAdapter2(viewModel.detailsList.value?:mutableListOf())

    Log.e("adapter", (viewModel.detailsList.value?: mutableListOf()).toString())
    emp_recyclerlist.adapter = detailsAdapter
    detailsAdapter.setOnItemClickListener(
      object : DetailsAdapter2.OnItemClickListener {
        override fun onItemClick(view:View, position:Int, item : Details2RowModel) {
          onClickRecyclerMessage(view, position, item)
        }
      }
    )
    viewModel.detailsList.observe(this@FormAEmployeeRegisterActivity) {
      detailsAdapter.updateData(it?: arrayListOf())
    }
    binding.formAEmployeeRegisterVM = viewModel

    iv_close_dialog.setOnClickListener {
      alertDialog.dismiss()
    }

    searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(s: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(s: String?): Boolean {
        if (detailsAdapter != null) {
          if (s != null) {
            filter(s)
          }
        }
        return false
      }
    })

    txtok.setOnClickListener {
      alertDialog.dismiss()
    }
  }
  private fun filter(text: String) {
    // creating a new array list to filter our data.
    val filteredlist: ArrayList<Details2RowModel> = ArrayList()

    // running a for loop to compare elements.
    for (item in viewModel.detailsList.value!!) {
      // checking if the entered string matched with any item of our recycler view.
      if (item.txtName?.toLowerCase()?.contains(text.toLowerCase())!!) {
        // if the item is matched we are
        // adding it to our filtered list.
        filteredlist.add(item)
      }
    }
    if (filteredlist.isEmpty()) {
      // if no item is added in filtered list we are
      // displaying a toast message as no data found.
      Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
    } else {
      // at last we are passing that filtered
      // list to our adapter class.
      detailsAdapter.filterList(filteredlist)
    }
  }


  public override fun addObservers(): Unit {
    var progressDialog: AlertDialog? = null
    viewModel.progressLiveData.observe(this@FormAEmployeeRegisterActivity) {
      if (it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@FormAEmployeeRegisterActivity.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }

    /*bind Branch list*/
    viewModel.fetchBranchLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchBranchlist(it)
      } else if (it is ErrorResponse) {
        onError(it.data ?: Exception())
      }
    }

    viewModel.fetchDepartmentLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchDepartmentlist(it)
      } else if (it is ErrorResponse) {
        onError(it.data ?: Exception())
      }
    }

    viewModel.fetcheEmployeeLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        onSuccessFetchemployeelist(it)
      } else if (it is ErrorResponse) {
        onError(it.data ?: Exception())
      }
    }
    viewModel.fetcheEmployeeReportLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        var bytes = it.data.bytes()

        //if(response!=null)
     /*  if( writeResponseBodyToDisk(body = it.data))
         Log.e("FileDownload","Downloaded")
        else
         Log.e("FileDownload","Failed")
*/
        /* val logFolder = File(Environment.getExternalStorageDirectory(), "Pagarplus")
         if (!logFolder.exists())
           logFolder.mkdir()
         val logFile = File(logFolder, "EmployeeReport.pdf")
         if (!logFile.exists())
           logFile.createNewFile()
         it.data.byteStream().use { input ->
           FileOutputStream(logFile).use { output ->
             input.copyTo(output)
           }
         }

         previewPdf(logFile)*/
      }else if (it is ErrorResponse) {
        onError(it.data ?: Exception())
      }
    }

  }

  /*fun downloadBytes(){



      val destination = File(
        Environment.getExternalStorageDirectory(),
        System.currentTimeMillis().toString() + ".pdf"
      )
      var _imageName = destination.name
      var _imageExtension =
        destination.absolutePath.substring(destination.absolutePath.lastIndexOf("."))
      var fo: FileOutputStream
      try {
        destination.createNewFile()
        fo = FileOutputStream(destination)
        fo.write(getBytesFromInputStream(responseBody.byteStream()))
        fo.close()
      } catch (e: FileNotFoundException) {
        e.printStackTrace()
      }
    }*/



  @Throws(IOException::class)
  fun getBytesFromInputStream(inputStream: InputStream): ByteArray? {
    return try {
      val buffer = ByteArray(8192)
      var bytesRead: Int
      val output = ByteArrayOutputStream()
      while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        output.write(buffer, 0, bytesRead)
      }
      output.toByteArray()
    } catch (error: OutOfMemoryError) {
      null
    }
  }

  // code to generate pdf report //
  public fun previewPdf(pdfFile: File) {
   var uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+ ".provider", pdfFile);
   // var uri= pdfFile.toUri()
    val myMime = MimeTypeMap.getSingleton()
    val mimeType = myMime.getMimeTypeFromExtension("pdf")
    Log.e("fileprovider","type:..$mimeType..uri:..$uri...path:..${pdfFile.absolutePath}")
    val newIntent = Intent(Intent.ACTION_VIEW)
    newIntent.setDataAndType(uri, mimeType)
    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    newIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    try {
      startActivity(newIntent)
    } catch (e: ActivityNotFoundException) {
      Toast.makeText(this, "Please Download An App to open pdf", Toast.LENGTH_LONG).show()
    }
  }


  private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
    return try {
      Log.e("FileDownload","Inside method")

      // todo change the file location/name according to your needs
     // val futureStudioIconFile = File(getExternalFilesDir("Android").toString() + File.separator + "EmployeeReport.pdf")
      val futureStudioIconFile = File(Environment.getExternalStorageDirectory().absolutePath+"/"+"EmployeeReport.pdf")
      if(!futureStudioIconFile.exists())
        futureStudioIconFile.createNewFile()
      var inputStream: InputStream? = null
      var outputStream: OutputStream? = null
      try {
        val fileReader = ByteArray(4096)
        val fileSize = body.contentLength()
        var fileSizeDownloaded: Long = 0
        inputStream = body.byteStream()
        outputStream = FileOutputStream(futureStudioIconFile)
        inputStream.use { input ->
          FileOutputStream(futureStudioIconFile).use { output ->
            input.copyTo(output)
          }
        }
        /*while (true) {
          val read: Int = inputStream.read(fileReader)
          if (read == -1) {
            break
          }
          outputStream.write(fileReader, 0, read)
          inputStream.use { input ->
            FileOutputStream(futureStudioIconFile).use { output ->
              input.copyTo(output)
            }
          }

          fileSizeDownloaded += read.toLong()
          Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")
        }*/
        outputStream.flush()
        true


      } catch (e: IOException) {
        Log.e("FileDownload","${e.message}")
        false

      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
        if (outputStream != null) {
          outputStream.close()
        }
      }
    } catch (e: IOException) {
      Log.e("FileDownload","${e.message}")

      false
    }
  }
  fun open_file(file: File) {


    // Get URI and MIME type of file
    val uri: Uri = file.toUri()
      //FileProvider.getUriForFile(this, file)
    val mime = contentResolver.getType(uri)

    // Open file with user selected app
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.setDataAndType(uri, "application/pdf")
   // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(intent)
  }
  private fun onSuccessFetchBranchlist(response: SuccessResponse<AdminReportResponse>): Unit {
    var branchlist = response.data.dataList?: arrayListOf()
//   branchlist.add(0, GetStateListItem(value=0,text=" Branch"))
    val weekArrayAdapter= ArrayAdapter(this, R.layout.attendance_spinner_item,branchlist.map { it.text })
    binding.spinnerSelecctbranch.adapter=weekArrayAdapter
    binding.spinnerSelecctbranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {

        viewModel.formAEmployeeRegisterModel.value?.adminBranchID = branchlist.get(position).value
      }

      override fun onNothingSelected(parentView: AdapterView<*>?) {
        // your code here

      }
    }
  }


  private fun onError(exception: Exception): Unit {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
      is HttpException -> {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
        else JSONObject()
        Toast.makeText(this@FormAEmployeeRegisterActivity,
          MyApp.getInstance().getString(R.string.lbl_failure),
          Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun onSuccessFetchDepartmentlist(response: SuccessResponse<AdminReportResponse>): Unit {
    var departmentlist = response.data.dataList?: arrayListOf()
//    departmentlist.add(0, FetchGetDepartmentListResponseListItem(value=0,text=" Department"))
    val weekArrayAdapter= ArrayAdapter(this, R.layout.attendance_spinner_item,departmentlist.map { it.text })
    binding.spinnerSelectDepartment.adapter=weekArrayAdapter
    binding.spinnerSelectDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
        var list=resources.getStringArray(R.array.holidays)
        viewModel.formAEmployeeRegisterModel.value?.adminBranchID = departmentlist.get(position).value
      }

      override fun onNothingSelected(parentView: AdapterView<*>?) {
        // your code here

      }
    }
  }
  private fun onSuccessFetchemployeelist(response: SuccessResponse<GetEmpviaDeptListResponse>): Unit {
    viewModel.bindFetchGetEmpListResponse(response.data)
  }

  fun onClickRecyclerMessage(
    view: View,
    position: Int,
    item: Details2RowModel
  ): Unit {
    when(view.id) {
      R.id.chkEmplist ->  {
        var selitem = viewModel.detailsList.value?.get(position)!!.txtEmpID
        if(viewModel.formAEmployeeRegisterModel.value?.EmpIdlist?.contains(EmployeeItem(selitem)) == true) {
          viewModel.formAEmployeeRegisterModel.value?.EmpIdlist?.remove(EmployeeItem(selitem!!))
        }else{
          viewModel.formAEmployeeRegisterModel.value?.EmpIdlist?.add(EmployeeItem(selitem!!))
        }
      }
      R.id.txtDeptNamelist ->  {
        var selitem = viewModel.detailsList.value?.get(position)!!.txtEmpID
      }
    }
  }




    companion object {
    const val TAG: String = "FORM_A_EMPLOYEE_REGISTER_ACTIVITY"


    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, FormAEmployeeRegisterActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
