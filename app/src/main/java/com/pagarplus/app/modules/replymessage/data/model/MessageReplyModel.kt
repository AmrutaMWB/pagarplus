package com.pagarplus.app.modules.replymessage.`data`.model

import kotlin.String

data class MessageReplyModel(
  /**
   * TODO Replace with dynamic value
   */
  public var txtEmpName: String? = "",
  public var txtMessage: String? = "",
  public var txtDatetime: String? = "",
  public var txtCommonDate: String? = "",
  public val imgPath: String? = "",
  var adminname: String? = "",
)
