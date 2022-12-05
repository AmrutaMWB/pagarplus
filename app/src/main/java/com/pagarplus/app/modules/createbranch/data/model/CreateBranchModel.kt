package com.pagarplus.app.modules.createbranch.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import kotlin.String

data class CreateBranchModel(
  /**
   * TODO Replace with dynamic value
   */
  var txtCreateBranch: String? = MyApp.getInstance().resources.getString(R.string.lbl_create_branch)
  ,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtBranchnameValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtBranchadrValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtStateValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtCityValue: String? = null,
  /**
   * TODO Replace with dynamic value
   */
  var etEdtTxtlocmapValue: String? = null,

  var LatitudeValue: String? = null,

  var LongitudeValue: String? = null,

  var StateID: Int? = 0,

  var CityID: Int? = 0,
)
