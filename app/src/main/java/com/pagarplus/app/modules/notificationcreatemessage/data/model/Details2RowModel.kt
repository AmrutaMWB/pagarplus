package com.pagarplus.app.modules.notificationcreatemessage.data.model

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.network.models.createcreateemployee.ProofItem
import kotlin.String

data class Details2RowModel(

    /**
   * TODO Replace with dynamic value
   */
    var txtName: String? = "",
    /**
   * TODO Replace with dynamic value
   */
    var txtEmpID: Int? = 0,

    var txtChecked: Boolean? = false,
)


