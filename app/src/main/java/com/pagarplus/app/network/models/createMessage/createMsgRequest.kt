package com.pagarplus.app.network.models.createMessage

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.network.models.createcreateemployee.ProofItem

data class CreateMsgRequest (

    @field:SerializedName("FromEmployee")
    val fromEmployee: Int? = null,

    @field:SerializedName("EmployeeList")
    val empList: ArrayList<EmpListItem>? = null,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("ImagePath")
    val imagePath: String? = null
)

data class EmpListItem(
    @field:SerializedName("EmployeeID")
    val employeeID: Int? = 0
)