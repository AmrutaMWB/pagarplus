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
import com.pagarplus.app.network.models.adminreport.AdminReportItem
import com.pagarplus.app.network.models.employeeReports.MonthItem
import java.util.Calendar


class FormAEmployeeRegisterActivity :
    BaseActivity<ActivityFormAEmployeeRegisterBinding>(R.layout.activity_form_a_employee_register) {
  private val viewModel: FormAEmployeeRegisterVM by viewModels<FormAEmployeeRegisterVM>()
  lateinit var detailsAdapter: DetailsAdapter2
  private  var mCurrentForm:String=""
  val mCalender=Calendar.getInstance()
  var mYear=mCalender.get(Calendar.YEAR)
  var mMonth=mCalender.get(Calendar.MONTH)+1
  private var mAdminId=0
 private lateinit var mFormTypes:Array<String>
 private  var mMonthsArray= arrayListOf<String>()
 private lateinit var mYearsArray:Array<String>
  private var mSelectedMonth:MonthItem?=null
 private var mBranchId=0
 private var mDeptId=0
  override fun onInitialized(): Unit {
     mFormTypes=resources.getStringArray(R.array.lbl_form_name)
    var montharray=resources.getStringArray(R.array.months)
    montharray.forEach {
      mMonthsArray.add(it)
    }
    mMonthsArray.add(0,"All")
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    mAdminId=intent.getIntExtra(IntentParameters.AdminId,0)
    mCurrentForm=intent.getStringExtra(IntentParameters.FormType)?:""
    binding.reportTypeName.text=mCurrentForm
    viewModel.callFetchBranchListApi(mAdminId)
    viewModel.callFetchDepartmentApi(mAdminId)
    binding.formAEmployeeRegisterVM = viewModel
    if(mCurrentForm==mFormTypes.get(1))
      binding.spinnerSelectMonth.visibility=View.GONE
    else
      binding.spinnerSelectMonth.visibility=View.VISIBLE

    mYearsArray=resources.getStringArray(R.array.lbl_form_year)
    val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,mYearsArray)
    binding.spinnerSelectyear.adapter = yearAdapter
    var position=mYearsArray.indexOf(mYearsArray.find { it==mYear.toString() })
    binding.spinnerSelectyear.setSelection(position)
    binding.spinnerSelectyear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
        mYear = parentView?.getItemAtPosition(position).toString().toInt()

      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
      }
    }

    val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mMonthsArray)
    binding.spinnerSelectMonth.adapter = monthAdapter
    binding.spinnerSelectMonth.setSelection(mMonth)
    binding.spinnerSelectMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
      mSelectedMonth= MonthItem(parentView?.getItemAtPosition(position).toString(),position)
        mMonth=position
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
/*var url="http://117.205.68.9//PagarwebApi/Content/Reports/Amruta9122022131028674EmployeeYearlyLoan_Report.pdf"
//var url="http://117.205.68.9//PagarwebApi/Content/Reports/PagarEmployeesList.xlsx"
      val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      startActivity(browserIntent)*/
    }

  }

  // method to display forms on click of spinner item //
   fun sendForGenerationRequest(){

     if(mCurrentForm.equals(mFormTypes.get(1).toString())){
       var request= EmployeeReportRequest(
         AdminID =mAdminId, Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist, Adminyear = mYear)
       viewModel.callFetchReportApi(request,this)
     }
     else if(mCurrentForm.equals(mFormTypes.get(2).toString())){
       var monthlist= arrayListOf<MonthItem>()
      if(mMonth!=0)
         monthlist.add(mSelectedMonth!!)
       var requestB= EmployeeReportRequest(AdminID = mAdminId,
         Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist,
         FromYear = mYear,  ToYear  = mYear+1, Months =viewModel.formAEmployeeRegisterModel.value?.MonthIdlist)
       viewModel.callFetchBReportApi(requestB,this)
     }
     else if(mCurrentForm.equals(mFormTypes.get(3).toString())){
       var requestC= EmployeeReportRequest(AdminID = mAdminId,Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist, FromYear = mYear,  ToYear  = mYear+1, Month = mMonth)
       viewModel.callFetchCReportApi(requestC,this)
     }
     else {
       var requestE= EmployeeReportRequest(AdminID = mAdminId,Employee =viewModel.formAEmployeeRegisterModel.value?.EmpIdlist, FromYear = mYear,  ToYear  = mYear+1, Month = mMonth)

       viewModel.callFetchEReportApi(requestE,this)
     }

   }




  fun showEmployeeDialog() {
    viewModel.callFetchEmployeeApi(mAdminId,mBranchId,mDeptId,mYear)
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
    viewModel.detailsList.observe(this){
      detailsAdapter.updateData(it)
    }
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

    viewModel.fetchUrlLiveData.observe(this) {
      if (it is SuccessResponse) {
        val response = it.getContentIfNotHandled()
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.data.string()))
       startActivity(browserIntent)
      } else if (it is ErrorResponse) {
        onError(it.data ?: Exception())
      }
    }

  }


  private fun onSuccessFetchBranchlist(response: SuccessResponse<AdminReportResponse>): Unit {
    var branchlist = response.data.dataList as ArrayList<AdminReportItem>
    if(branchlist.size>1)
      branchlist.add(0, AdminReportItem(0,"All Branch"))
    val weekArrayAdapter= ArrayAdapter(this, R.layout.attendance_spinner_item,branchlist.map { it.text })
    binding.spinnerSelecctbranch.adapter=weekArrayAdapter
    binding.spinnerSelecctbranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
        mBranchId=branchlist.get(position).value?:0
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
    var departmentlist = response.data.dataList as ArrayList<AdminReportItem>
    if(departmentlist.size>1)
      departmentlist.add(0,AdminReportItem(0,"All Department"))
    val deptArrayAdapter= ArrayAdapter(this, R.layout.attendance_spinner_item,departmentlist.map { it.text })
    binding.spinnerSelectDepartment.adapter=deptArrayAdapter
    binding.spinnerSelectDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
      mDeptId = departmentlist.get(position).value?:0
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
