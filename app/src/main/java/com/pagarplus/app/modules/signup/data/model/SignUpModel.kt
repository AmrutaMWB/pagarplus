package com.pagarplus.app.modules.signup.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItemProofsItem
import kotlin.String

data class SignUpModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtOrganizationDe: String? = MyApp.getInstance().resources.getString(R.string.msg_organization_de)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etFirmNameValue: String? = "",

  var etFirstNameValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etLastNameValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etEmailIDValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etMobileValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etAddressValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etCityValue: String? = "",

  var cityID: Int? = 0,
  /**
   * TODO Replace with dynamic value
   */
  var etStateValue: String? = "",

  var StateID: Int? = 0,
  /**
   * TODO Replace with dynamic value
   */
  var etUsernamemobilValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etPasswordValue: String? = "",
  /**
   * TODO Replace with dynamic value
   */
  var etConfirmPassworValue: String? = "",

  var oldPassword: String? = "",

  var etProfileImgURL: String? = "",

  var etDOB: String? = "",

  var etOffEndtime: String? = "",

  var etReferralcode: String? = "",

  var proofs:ArrayList<FetchEditEmployeeDetailsResponseListItemProofsItem>?= arrayListOf(),
)
