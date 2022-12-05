package com.pagarplus.app.modules.payment.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class PaymentModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtOnlinePayment: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_online_payment)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtGroupFive: String? = MyApp.getInstance().resources.getString(R.string.lbl_account)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtAmountValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtDescriptionValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtActHoldernameValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtActNumValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtCnfActnumValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtIFSCValue: String? = null,

  var etEdttxtUpiidValue: String? = null
)
