package com.pagarplus.app.network.models.expense

import com.google.gson.annotations.SerializedName

data class ExpenseObject (
    @field:SerializedName("Status")
    val status: Boolean? = false,

    @field:SerializedName("Message")
    val message: String? = "",

    @field:SerializedName("ExpenseTypes")
    val expenseList:ArrayList<ExpenseItem>? = arrayListOf(),

    @field:SerializedName("SubExpenseTypes")
    val subExpenseList:ArrayList<SubExpenseItem>? = arrayListOf()
        )

data class ExpenseItem(

    @field:SerializedName("Value")
    val expenseId: Int? = 0,

    @field:SerializedName("Text")
    val expenseName: String? = "",

    @field:SerializedName("IsSubtypeExist")
    val isSubtypeExist: Boolean? = false)

data class SubExpenseItem(

    @field:SerializedName("HeadID")
    val expenseId: Int? = 0,

    @field:SerializedName("Value")
    val subexpenseId: Int? = 0,

    @field:SerializedName("Text")
    val subexpenseName: String? = "",
    )