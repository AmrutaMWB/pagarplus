package com.pagarplus.app.modules.itemlistdialog.`data`.model

import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.createcreateemployee.ProofItem
import com.pagarplus.app.network.models.createsignup.GetStateListItem
import java.io.Serializable
import kotlin.String

public data class Itemlistdialog1RowModel(
  /**
   * TODO Replace with dynamic value
   */
  public var txtName: String? = "",
  public var txtValue: Int? = 0,
  public var isState: Boolean? = false,
  public var isBranch: Boolean? = false,
  public var txtChecked: Boolean? = false,
)
