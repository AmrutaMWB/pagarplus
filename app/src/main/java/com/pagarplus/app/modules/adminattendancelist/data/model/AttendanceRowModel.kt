package com.pagarplus.app.modules.adminattendancelist.`data`.model

import androidx.lifecycle.MutableLiveData
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.expense.ImageItems
import java.io.Serializable
import java.util.ArrayList
import kotlin.String

data class AttendanceRowModel(

  var txtbranch: String? = "",
  var txtDept: String? = "",
  var txtcomment: String? = "",
  var txtempid: Int? = 0,
  var txtEmpName: String? = "",

  var txttotDuration: String = "0",
  var txtMobilenumber: String? = "",
  var organizationname: String? = "",
  var fin: String? = "",
  var fout: String? = "",
  var secin: String? = "",
  var secout: String? = "",

  var finimage: String? = "",
  var foutimage: String? = "",
  var secinimage: String? = "",
  var secoutimage: String? = "",

  val visitList: MutableLiveData<MutableList<VisitRowModel>> = MutableLiveData(mutableListOf()),
  var txtChecked: Boolean? = false,
  val ImagesList: MutableList<String>? = ArrayList(),
)
