package com.pagarplus.app.network.models.expense

import com.google.gson.annotations.SerializedName

data class ExpenseStatusRequest(
@field:SerializedName("CommentByAdmin")
var commentByAdmin: String? = null,

@field:SerializedName("ExpenseTypeID")
var expenseTypeID: String? = null,

@field:SerializedName("ExpenseID")
var ExpenseID: String? = null)
