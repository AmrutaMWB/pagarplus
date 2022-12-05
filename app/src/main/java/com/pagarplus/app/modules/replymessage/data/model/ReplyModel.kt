package com.pagarplus.app.modules.replymessage.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class ReplyModel(

  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtMessageValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var imagePathValue: String? = null,

  var toEmployeeValue: Int? = null,

  var messageIdValue: Int? = 0,

  var passedMainMessageIDValue: String? = null,

  var empname: String? = "",

  var empbranch: String? = "",

  var empdept: String? = "",

  var empimage: String? = "",

)
