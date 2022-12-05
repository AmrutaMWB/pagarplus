package com.pagarplus.app.modules.itemlistdialog.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.createsignup.GetStateListItem
import kotlin.String

public data class ItemlistModel(
  /**
   * TODO Replace with dynamic value
   */
  public var txtItemList: String? = ""
  ,
  /**
   * TODO Replace with dynamic value
   */
  public var txtTvItemName: String? = "",

  public var stateID: Int? = 0,

  public var branchSize: Int? = 0,

  val BranchList: ArrayList<GetStateListItem> = ArrayList()

)
