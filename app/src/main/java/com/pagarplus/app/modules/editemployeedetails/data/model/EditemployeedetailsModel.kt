package com.pagarplus.app.modules.editemployeedetails.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItemProofsItem
import kotlin.String

data class EditemployeedetailsModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtEditEmployee: String? =
      MyApp.getInstance().resources.getString(R.string.lbl_edit_employee)
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
    MyApp.getInstance().resources.getString(R.string.msg_select_departme)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var txtDeptId: Int? = null
  ,
  var txtEmpId: Int? = 0
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
  var etEdtTxtFirstnameValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtLastnameValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtmobileNoValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtemailValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtSalaryValue: String? = "0",
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtPaidleaveValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtSickleaveValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtPwdValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */

  var etEdtTxtcnfPwdValue: String? = "",

  var txtSpnHoliday: String? = "",

  var loan_advance: String? = "",

  var monthly_deduction: String? = "",

  var old_salary_balance: String? = "",

  var comment_on_balance: String? = "",

  var empID: Int? = 0,

  var etEdtTxtdesignation: String? = "",

  var etEdtTxtdatofjoining: String? = "",

  var etEdtTxtCity: String? = "",

  var txtCityID: Int? = 0,

  var etEdtTxtState: String? = "",

  var txtStateID: Int? = 0,

  var etEdtTxtaddress: String? = "",

  var etEdtTxtWeekoff: String? = "",

  var etEdtTxtBloodgroup: String? = "",

  var etEdtTxtEmergencynum: String? = "",

  var etEdtTxtcheckintime: String? = "",

  var etEdtTxtcheckouttime: String? = "",

  var txtOldPassword: String? = "",

  val Prooflist: ArrayList<FetchEditEmployeeDetailsResponseListItemProofsItem>? = ArrayList()

  //val updatedProoflist: ArrayList<UpdatedListItemProofsItem>? = ArrayList()
)
