package com.pagarplus.app.modules.createemployee.data.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.createcreateemployee.ProofItem
import kotlin.String

data class CreateEmployeeModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtCreateEmployee: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_create_employee)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtIDProof: String? = MyApp.getInstance().resources.getString(R.string.lbl_id_proof)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtSelectDepartme: String? =
      MyApp.getInstance().resources.getString(R.string.msg_select_departme),

  /**
   * TODO Replace with dynamic value
   */
  var txtDeptId: Int? = 0
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtSelectBranch: String? = MyApp.getInstance().resources.getString(R.string.lbl_select_branch)
  ,
  var txtBranchId: Int? = 0
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtAddOldBalance: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_add_old_balance)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtFirstnameValue: String? = null,
  /**
  * TODO Replace with dynamic value
  */
  var etEdtTxtLastnameValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtmobileNoValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtemailValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtSalaryValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtPaidleaveValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtSickleaveValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtPwdValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtdesignation: String? = null,

  var etEdtTxtdatofjoining: String? = null,

  var etEdtTxtcnfPwdValue: String? = null,

  var etEdtTxtCityValue: String? = null,

  var cityID: Int? = 0,

  var etEdtTxtStateValue: String? = null,

  var StateID: Int? = 0,

  var etEdtTxtAddressValue: String? = null,

  var txtSpnHoliday: String? = null,

  var loan_advance: String? = null,

  var monthly_deduction: String? = null,

  var old_salary_balance: String? = null,

  var comment_on_balance: String? = null,

  var etEdtTxtWeekoff: String? = "",

  var etEdtTxtBloodgroup: String? = "",

  var etEdtTxtEmergencynum: String? = "",

  var etEdtTxtcheckintime: String? = "",

  var etEdtTxtcheckouttime: String? = "",

  val Prooflist: ArrayList<ProofItem> = ArrayList()
)
