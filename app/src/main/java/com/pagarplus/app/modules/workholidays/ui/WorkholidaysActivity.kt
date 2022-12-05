package com.pagarplus.app.modules.workholidays.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.snackbar.Snackbar
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.appcomponents.views.DatePickerFragment
import com.pagarplus.app.databinding.ActivityWorkingDaysBinding
import com.pagarplus.app.extensions.*
import com.pagarplus.app.modules.workholidays.data.viewmodel.WorkholidaysVM
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.holiday.HolidayItem
import com.pagarplus.app.network.models.holiday.SetHolidayRequest
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.HttpException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
@SuppressLint("SuspiciousIndentation")
class WorkholidaysActivity : BaseActivity<ActivityWorkingDaysBinding>(R.layout.activity_working_days) {
  private val viewModel: WorkholidaysVM by viewModels<WorkholidaysVM>()
    private val prefs: PreferenceHelper by inject()
    private var mLoginDetails=prefs.getLoginDetails<LoginResponse>()
   private var mHolidayDate: String? =SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    private var mIsHalfDay=false
    private var mHolidayTypes= arrayListOf<FeaturesTypes>()
    private var mIsWeekHalfDay=false
    private var mCurrentHolidayType=FeaturesTypes()
    private var mCurrentHolidayItem=HolidayItem()
    private var mCalender=Calendar.getInstance()
    private var mCurrentMonth=mCalender.get(Calendar.MONTH)
    private var mSelectedMonth=mCalender.get(Calendar.MONTH)
    private var mCurrentYear=mCalender.get(Calendar.YEAR)
    private lateinit var mHolidayAdapter:RecyclerHolidayAdapter

  override fun onInitialized(): Unit {
      lifecycleScope.launchWhenCreated {
          mHolidayTypes=ApiUtil(this@WorkholidaysActivity).getHolidayTypes()
          initHolidayTypeSpinner()
      }
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.workHolidayVM = viewModel
      initWeekSpinner()
      initMonthLayout()
      viewModel.getHolidaysList(mLoginDetails?.userID?:0)
      mHolidayAdapter = RecyclerHolidayAdapter(viewModel.recyclerViewList.value ?: mutableListOf(),this)
      binding.recyclerHolidayList.adapter = mHolidayAdapter
      mHolidayAdapter.setOnItemClickListener(
          object : RecyclerHolidayAdapter.OnItemClickListener {
              override fun onItemClick(view: View, position: Int, item: HolidayItem) {
                  onClickRecyclerView(view, position, item)
              }
          })
      viewModel.recyclerViewList.observe(this) {
          mHolidayAdapter.updateData(it as ArrayList<HolidayItem>)
      }

  }

    private fun initMonthLayout() {
        var list=resources.getStringArray(R.array.months)
        var month=list[mCurrentMonth]
        binding.txtMonth.text=month
       binding.nxtMonth.setOnClickListener {
           if(mSelectedMonth<11){
               mSelectedMonth += 1
               binding.txtMonth.text=list[mSelectedMonth]
               viewModel.filterHolidaysByMonth(mSelectedMonth+1)
           }
       }
        binding.prevMonth.setOnClickListener {
            if(mSelectedMonth>0)
            {
                mSelectedMonth -= 1
                binding.txtMonth.text=list[mSelectedMonth]
                viewModel.filterHolidaysByMonth(mSelectedMonth+1)
            }
        }


    }

    private fun onClickRecyclerView(view: View, position: Int, item: HolidayItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you Sure..?")
        builder.setMessage("Do you want delete the holiday")
        builder.setIcon(R.drawable.delete)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            this@WorkholidaysActivity.hideKeyboard()
            viewModel.deleteHolidays(item.id?:0)
        }
        builder.setNegativeButton("No") { dialogInterface, which -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun initWeekSpinner(){
   val weekArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.holidays))
   binding.spinnerWeek.adapter=weekArrayAdapter
   binding.spinnerWeek.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
     override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
         var list=resources.getStringArray(R.array.holidays)
     }

     override fun onNothingSelected(parentView: AdapterView<*>?) {
       // your code here

     }
   }
 }
    fun initHolidayTypeSpinner(){
        val HtArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,mHolidayTypes.map { it.featureName })
        binding.spinnerHolidayType.adapter=HtArrayAdapter
        binding.spinnerHolidayType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
               mCurrentHolidayType=mHolidayTypes[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }



  override fun setUpClicks(): Unit {

      binding.txtMonth.addTextChangedListener {
          if(mSelectedMonth>=mCurrentMonth)
              binding.holidayLayout.visibility=View.VISIBLE
          else
              binding.holidayLayout.visibility=View.GONE
      }
      binding.BtnAddHoliday.setOnClickListener {
          if(validateFields()) {
              var list = arrayListOf<HolidayItem>()
              mCurrentHolidayItem = HolidayItem(
                  id = null,
                  orgId = mLoginDetails?.orgID?: 0,
                  adminID = mLoginDetails?.userID ?: 0,
                  date = mHolidayDate,
                  isHalfDay = mIsHalfDay,
                  isActive = true,
                  holidayTypeID = mCurrentHolidayType.ID,
                  holidayType = mCurrentHolidayType.featureName,
                  comment = binding.etHolidayName.text.trim().toString()
              )

              list.add(mCurrentHolidayItem)
              val request =
                  SetHolidayRequest(adminID = mLoginDetails?.userID?: 0, dateList = list)
              viewModel.setHolidays(request)
          }else
              Snackbar.make(binding.root, MyApp.getInstance().resources.getString(R.string.lbl_valid_hd), Snackbar.LENGTH_LONG).show()
      }

      binding.txtHolidayDate.setOnClickListener {
      val cal=Calendar.getInstance()
          cal.set(mCurrentYear,mSelectedMonth,1)

          val destinationInstance = DatePickerFragment.getInstance(false,cal.time)
          destinationInstance.show(this.supportFragmentManager, DatePickerFragment.TAG) { selectedDate ->
              val selected = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
              mHolidayDate=selected
              binding.txtHolidayDate.text = selected
          }
      }

  }
    fun validateFields():Boolean{
        val date=binding.txtHolidayDate.text.trim().toString()
        val holidayname=binding.etHolidayName.text.trim().toString()
       return (date.isNotEmpty() && holidayname.isNotEmpty())
    }

  override fun addObservers() {
    var progressDialog : AlertDialog? = null
    viewModel.progressLiveData.observe(this@WorkholidaysActivity) {
      if(it) {
        progressDialog?.dismiss()
        progressDialog = null
        progressDialog = this@WorkholidaysActivity.showProgressDialog()
      } else {
        progressDialog?.dismiss()
      }
    }
    viewModel.setHolidayLiveData.observe(this@WorkholidaysActivity) {
      if(it is SuccessResponse) {
          if(it.data.status){
             viewModel.getHolidaysList(mLoginDetails?.userID?:0)
          }
          else
         Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
      } else if(it is ErrorResponse)  {
        onError(it.data ?:Exception())
      }
    }
      viewModel.deleteHolidayLiveData.observe(this@WorkholidaysActivity) {
          if(it is SuccessResponse) {
              if(it.data.status){
                  viewModel.getHolidaysList(mLoginDetails?.userID?:0)
              }
              else
              Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
          } else if(it is ErrorResponse)  {
              onError(it.data ?:Exception())
          }
      }

      viewModel.getHolidaysListLiveData.observe(this@WorkholidaysActivity) {
          if(it is SuccessResponse) {
              if(it.data.status){
                  viewModel.bindHolidaysList(it.data,mSelectedMonth+1)
                  binding.txtHolidayDate.text=""
                  binding.etHolidayName.text.clear()
              }else
              Snackbar.make(binding.root, it.data.message?:"", Snackbar.LENGTH_LONG).show()
          } else if(it is ErrorResponse)  {
              onError(it.data ?:Exception())
          }
      }
  }

  private fun onError(exception: Exception) {
    when(exception) {
      is NoInternetConnection -> {
        Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
      }
      is HttpException -> {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
            else JSONObject()
        val errMessage = if(!(errorObject.optString("Message").isEmpty())) {
          errorObject.optString("Message").toString()
        } else {
           exception.response()?.message()?:""
        }
        Snackbar.make(binding.root, errMessage, Snackbar.LENGTH_LONG).show()
      }
    }
  }
  companion object {
    const val TAG: String = "WORKHOLIDAYS_ACTIVITY"
      fun getIntent(context: Context, bundle: Bundle?): Intent {
          val destIntent = Intent(context, WorkholidaysActivity::class.java)
          destIntent.putExtra("bundle", bundle)
          return destIntent
      }
  }
}
