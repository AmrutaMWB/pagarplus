package com.pagarplus.app.modules.adminreport.ui
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.databinding.ActivityAdminReportBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.adminreport.data.model.AdminReportRowModel
import com.pagarplus.app.modules.adminreport.data.viewmodel.AdminReportVM
import com.pagarplus.app.modules.expensereport.ui.*
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel
import com.pagarplus.app.network.models.adminreport.AdminReportItem
import com.pagarplus.app.network.models.adminreport.AdminReportResponse
import com.pagarplus.app.network.models.adminreport.AdminSAReportRequest
import com.pagarplus.app.network.models.adminreport.Employee
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import com.yuvapagar.app.modules.notificationcreatemessage.ui.DetailsAdapter2
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import kotlin.collections.ArrayList

public class AdminReportActivity : BaseActivity<ActivityAdminReportBinding>(R.layout.activity_admin_report) {
    private val viewModel: AdminReportVM by viewModels<AdminReportVM>()
    private var mAdminId:Int?=0
    private var mIsSalaryWise:Boolean?=false
    private var mBranchList= arrayListOf<AdminReportItem>()
    private var mCurrentBranch:AdminReportItem=AdminReportItem()
    private var mCurrentDept:AdminReportItem= AdminReportItem()
    private var mDeptList= arrayListOf<AdminReportItem>()
    lateinit var mEmployeeListAdapter : DetailsAdapter2
    private var calendar=Calendar.getInstance()
    private var mCurrentMonthId=calendar.get(Calendar.MONTH)
    private var mCurrentYear=calendar.get(Calendar.YEAR)

    public override fun onInitialized(): Unit {
        binding.adminReportVM = viewModel
        mAdminId=intent.getIntExtra(IntentParameters.AdminId,0)
        mIsSalaryWise=intent.getBooleanExtra(IntentParameters.IsSalaryReport,false)
        viewModel.getBranchAndDepartmentList(mAdminId?:0)

        binding.txtOfficialWorkingDays.visibility=if(mIsSalaryWise?:false)View.GONE else View.VISIBLE
        initMonthsSpinner()
        val recyclerView1Adapter = AdminReportAdapter(viewModel.recyclerEmployeeSAList.value?: mutableListOf(), this,mIsSalaryWise?:false)
        binding.recyclerSAListView.adapter = recyclerView1Adapter
        recyclerView1Adapter.setOnItemClickListener(
            object : AdminReportAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int, item: AdminReportRowModel) {
                    onClickRecyclerView1(view, position, item)
                }
            }
        )

        viewModel.recyclerEmployeeSAList.observe(this){
            recyclerView1Adapter.updateData(it)
        }

        if(viewModel.profiledetails?.showBranch == false){
            binding.btnSelectBranch.isEnabled = false
        }else{
            binding.btnSelectBranch.isEnabled = true
        }

        if(viewModel.profiledetails?.showDepartment == false){
            binding.btnSelectDept.isEnabled = false
        }else{
            binding.btnSelectDept.isEnabled = true
        }
    }

    fun showEmployeeDialog() {
        viewModel.callFetchGetEmpListApi(mAdminId?:0,mCurrentDept.value?:0)
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getLayoutInflater()
        @SuppressLint("InflateParams")
        val dialogView = inflater.inflate(R.layout.sel_emp_search_dialog, null)
        dialogBuilder.setView(dialogView).setCancelable(false)
        val iv_close_dialog = dialogView.findViewById<AppCompatButton>(R.id.iv_cross)
        val emp_recyclerlist = dialogView.findViewById<RecyclerView>(R.id.emplistRecycler)
        val txtok = dialogView.findViewById<TextView>(R.id.txtok)
        val searchbar = dialogView.findViewById<SearchView>(R.id.searchEMp)
        val selectAll = dialogView.findViewById<CheckBox>(R.id.chkSelectAll)
        val alertDialog = dialogBuilder.create()
        alertDialog.show();
        mEmployeeListAdapter = DetailsAdapter2(viewModel.detailsList.value?:mutableListOf())
        emp_recyclerlist.adapter = mEmployeeListAdapter
        mEmployeeListAdapter.setOnItemClickListener(
            object : DetailsAdapter2.OnItemClickListener {
                override fun onItemClick(view:View, position:Int, item : Details2RowModel) {
                        if(viewModel.adminReportModel.value?.EmpIdlist?.contains(
                                Employee(item.txtEmpID)) == true) {
                            viewModel.adminReportModel.value?.EmpIdlist?.remove(
                                Employee(item.txtEmpID)
                            )
                        }else{
                            viewModel.adminReportModel.value?.EmpIdlist?.add(
                                Employee(item.txtEmpID)
                            )
                        }
                    }
            }
        )
        viewModel.detailsList.observe(this@AdminReportActivity) {
            mEmployeeListAdapter.updateData(it)
        }
        selectAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.adminReportModel.value?.EmpIdlist?.clear()
                viewModel.detailsList.value?.forEach {
                    viewModel.adminReportModel.value?.EmpIdlist?.add(Employee(it.txtEmpID))
                    it.txtChecked=true
                }
            }else{
                viewModel.adminReportModel.value?.EmpIdlist?.clear()
                viewModel.detailsList.value?.forEach {
                    it.txtChecked=false
                }
            }
            mEmployeeListAdapter.notifyDataSetChanged()
        }

        iv_close_dialog.setOnClickListener {
            alertDialog.dismiss()
        }

        searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                if (mEmployeeListAdapter != null) {
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
            mEmployeeListAdapter.filterList(filteredlist)
        }
    }

    fun sendASReportRequest(){
        var Emplist=viewModel.adminReportModel.value?.EmpIdlist
        val employee=Employee(16)
        if( Emplist?.contains(employee)==false)
            Emplist.add(employee)

      var request=AdminSAReportRequest(
          adminID = mAdminId?:0,
          month = mCurrentMonthId,
          year =mCurrentYear,
          employee = Emplist )
        viewModel.getAdminSAList(request)
    }

    public override fun setUpClicks(): Unit {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnSelectEmp.setOnClickListener {
            showEmployeeDialog()
        }

        binding.btnSearch.setOnClickListener {
            sendASReportRequest()

        }
    }

    public fun onClickRecyclerView1(view: View, position: Int, item: AdminReportRowModel): Unit {
        val destIntent= ExpenseReportActivity.getIntent(this, null)
        destIntent.putExtra(IntentParameters.AdminId,mAdminId)
        destIntent.putExtra(IntentParameters.UserId,item.EmployeeID.toString())
        destIntent.putExtra(IntentParameters.isAdmin,true)
        startActivity(destIntent)
    }

    fun initMonthsSpinner(){
        var monthid=Calendar.getInstance().get(Calendar.MONTH)
        val monthAdapter = ArrayAdapter(this, R.layout.attendance_spinner_item,resources.getTextArray(R.array.months))
        binding.btnSelectMonth.adapter=monthAdapter
        binding.btnSelectMonth.setSelection(monthid)
        binding.btnSelectMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
               mCurrentMonthId=position+1
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    public override fun addObservers(): Unit {
        var progressDialog: AlertDialog? = null
        viewModel.progressLiveData.observe(this@AdminReportActivity) {

            if (it) {
                progressDialog?.dismiss()
                progressDialog = null
                progressDialog = this@AdminReportActivity.showProgressDialog()

            } else {
                progressDialog?.dismiss()
            }
        }

        viewModel.adminWiseBranchLiveData.observe(this@AdminReportActivity) {
            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
               if( !response?.dataList.isNullOrEmpty()){
                   mBranchList=response!!.dataList as ArrayList<AdminReportItem>
                   if(mBranchList.size>1)
                   mBranchList.add(0,AdminReportItem(0,"All Branch"))
                   val branchAdapter = ArrayAdapter(this,R.layout.attendance_spinner_item,mBranchList.map { it.text })
                   binding.btnSelectBranch.adapter=branchAdapter
                   binding.btnSelectBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                       override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                          mCurrentBranch=mBranchList[position]
                       }

                       override fun onNothingSelected(parentView: AdapterView<*>?) {
                           // your code here

                       }
                   }
               }
            } else if (it is ErrorResponse) {
                onError(it.data ?: Exception())
            }
        }

        viewModel.adminWiseDepartmentLiveData.observe(this@AdminReportActivity) {
            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                if( !response?.dataList.isNullOrEmpty()){
                    mDeptList=response!!.dataList as ArrayList<AdminReportItem>
                    if(mDeptList.size>1)
                    mDeptList.add(0,AdminReportItem(0,"All Department"))
                    val deptAdapter = ArrayAdapter(this, R.layout.attendance_spinner_item,mDeptList.map { it.text })
                    binding.btnSelectDept.adapter=deptAdapter
                    binding.btnSelectDept.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                            mCurrentDept=mDeptList[position]
                        }
                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                            // your code here
                        }
                    }
                }
            } else if (it is ErrorResponse) {
                onError(it.data ?: Exception())
            }
        }

        viewModel.fetchGetEmpLiveData.observe(this) {
            if(it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                viewModel.bindFetchGetEmpListResponse(it.data)
            } else if(it is ErrorResponse) {
                onError(it.data ?:Exception())
            }
        }

        viewModel.adminSAReportLiveData.observe(this@AdminReportActivity) {
            if (it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                onSuccessAdminSaReport(it)

            } else if (it is ErrorResponse) {
                onError(it.data ?: Exception())
            }
        }
    }

    private fun onError(exception: Exception) {
        when (exception) {
            is
            NoInternetConnection -> {
                Snackbar.make(binding.root, exception.message ?: "", Snackbar.LENGTH_LONG).show()
            }

            is HttpException -> {
                val errorBody = exception.response()?.errorBody()?.string()
                val errorObject = if (errorBody != null && errorBody.isJSONObject())
                    JSONObject(errorBody)
                else JSONObject()

                Snackbar.make(binding.root, MyApp.getInstance().getString(R.string.lbl_error), Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun onSuccessAdminSaReport(it: SuccessResponse<AdminReportResponse>) {
            if(!it.data.AdminSAList.isNullOrEmpty())
                viewModel.binAdminSAList(it.data,resources.getTextArray(R.array.months)[mCurrentMonthId-1].toString())
            else{
                Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_no_data), Snackbar.LENGTH_LONG).show()
            }
    }
    public companion object {
        public const val TAG: String = "AdminReport_Activity"
        public fun getIntent(context: Context, bundle: Bundle?): Intent {
            val destIntent = Intent(context, AdminReportActivity::class.java)
            destIntent.putExtra("bundle", bundle)
            return destIntent
        }
    }
}
