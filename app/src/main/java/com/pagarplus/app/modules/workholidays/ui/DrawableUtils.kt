package com.pagarplus.app.modules.workholidays.ui


import android.content.Context
import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.utils.DayColorsUtils
import com.pagarplus.app.R


/**
 * Created by Mateusz Kornakiewicz on 02.08.2018.
 */
object DrawableUtils {
    fun getCircleDrawableWithText(context: Context?, string: String?): Drawable {
        val background = ContextCompat.getDrawable(context!!, R.drawable.sample_circle)
        val text = CalendarUtils.getDrawableText(context, string, null, R.color.white, 30)
        val layers = arrayOf(background, text)
        return LayerDrawable(layers)
    }

    fun getThreeDots(context: Context?): Drawable {
        val drawable = ContextCompat.getDrawable(context!!, R.drawable.default_unselected_dot)

        //Add padding to too large icon
        return InsetDrawable(drawable, 100, 0, 100, 0)
    }

    fun getDayCircle(
        context: Context?,
        @ColorRes borderColor: Int,
        @ColorRes fillColor: Int
    ): Drawable? {
        val drawable = ContextCompat.getDrawable(context!!, R.drawable.sample_circle) as GradientDrawable?
        //drawable.setStroke(6, DayColorsUtils.parseColor(context, borderColor))
        drawable?.setStroke(6,Color.BLUE )
        drawable?.setColor(R.drawable.user_grad_back)
        return drawable
    }
}