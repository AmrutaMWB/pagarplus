package com.pagarplus.app.modules.editprofile.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItemProofsItem
import kotlin.String

data class EditProfileModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtEditProfile: String? = MyApp.getInstance().resources.getString(R.string.lbl_edit_profile)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etFirstname: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etLastName: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var imgProfileUrl: String? = null,
  var etEmail: String? = null,
  var etAddress: String? = null,
  var etUIDNum: String? = null,
  var etPanNum: String? = null,
  var etDLNum: String? = null,
  var etPassword: String? = "",
  var etConfirmPassword: String? = "",
  var EmpId: Int? = 0,
  var proofs:ArrayList<FetchEditEmployeeDetailsResponseListItemProofsItem>?= arrayListOf(),
  var etFathername: String? = null,
  var etEducation: String? = null,
  var etDOB: String? = null,
  var etAge: String? = "0",
  var etGender: String? = null,

)
