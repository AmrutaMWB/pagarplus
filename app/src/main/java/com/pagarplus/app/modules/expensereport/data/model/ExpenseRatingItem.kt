package com.pagarplus.app.modules.expensereport.data.model

import android.util.Pair

data class ExpenseRatingItem (
    var ExpenseLabel:String ="",
    var ExpensePercentageVal:Int= 0,
    var ExpensePercentage:String= "$ExpensePercentageVal %",
    var ExpenseImage:String="",
    var ExpenseAmount:String="",
    var ExpenseId:Int=0
)

