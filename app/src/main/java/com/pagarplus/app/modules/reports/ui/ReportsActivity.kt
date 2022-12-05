package com.pagarplus.app.modules.reports.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.databinding.ActivityReportsBinding
import com.pagarplus.app.extensions.IntentParameters
import com.pagarplus.app.modules.adminreport.ui.AdminReportActivity
import com.pagarplus.app.modules.formaemployeeregister.ui.FormAEmployeeRegisterActivity
import com.pagarplus.app.modules.reports.data.viewmodel.ReportsVM
import kotlin.Int
import kotlin.String
import kotlin.Unit

class ReportsActivity : BaseActivity<ActivityReportsBinding>(R.layout.activity_reports) {
  private val viewModel: ReportsVM by viewModels<ReportsVM>()

  private val REQUEST_CODE_ATTENDANCE_REPORT_DETAILS_ACTIVITY: Int = 283

  private val REQUEST_CODE_ATTENDANCE_REPORT_ACTIVITY: Int = 958

  override fun onInitialized(): Unit {

    binding.reportsVM = viewModel

    // for on click of forms spinner items //
    val formAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.lbl_form_name))
    binding.spinnerFormName.adapter=formAdapter
    binding.spinnerFormName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
        var list = parentView?.getItemAtPosition(position).toString()
        if(!list.equals(parentView?.getItemAtPosition(0).toString())) {

          val destIntent = FormAEmployeeRegisterActivity.getIntent(applicationContext, null)
          destIntent.putExtra(IntentParameters.FormType, list)
          startActivity(destIntent)
        }
      }

      override fun onNothingSelected(parentView: AdapterView<*>?) {
        // your code here

      }
    }
    }

    override fun setUpClicks(): Unit {
      binding.btnBackR.setOnClickListener {
        finish()
      }
      binding.btnSalaryReport.setOnClickListener {
        val destIntent = AdminReportActivity.getIntent(this, null)
        destIntent.putExtra(IntentParameters.IsSalaryReport,true)
        startActivity(destIntent)
      }
      binding.btnAttendanceReport.setOnClickListener {
        val destIntent = AdminReportActivity.getIntent(this, null)
        destIntent.putExtra(IntentParameters.IsSalaryReport,false)
        startActivity(destIntent)

      }
    }

    companion object {
      const val TAG: String = "REPORTS_ACTIVITY"
      fun getIntent(context: Context, bundle: Bundle?): Intent {
        val destIntent = Intent(context, ReportsActivity::class.java)
        destIntent.putExtra("bundle", bundle)
        return destIntent
      }

    }
  }
