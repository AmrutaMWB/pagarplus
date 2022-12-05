package com.pagarplus.app.modules.workholidays.ui


import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.pagarplus.app.R
import com.pagarplus.app.extensions.getDate
import java.util.*

class CalendarWithCustomRowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_with_custom_row_activity)
        val events: MutableList<EventDay> = ArrayList()
        val calendar1 = Calendar.getInstance()
        calendar1.time= Date()
        events.add(EventDay(calendar1, R.drawable.ic_arrow_down))
        val calendar2 = Calendar.getInstance()
        calendar2.time="23/11/2022".getDate()
        events.add(EventDay(calendar2, R.drawable.ic_arrow_down, Color.parseColor("#228B22")))

        val calendar6 = Calendar.getInstance()
        calendar6.time="22/11/2022".getDate()
        events.add(EventDay(calendar6,DrawableUtils.getDayCircle(this,R.color.blue_400,R.color.red_600)))
        val calendar3 = Calendar.getInstance()
        calendar3.add(Calendar.DAY_OF_MONTH, 7)
        events.add(EventDay(calendar3, DrawableUtils.getCircleDrawableWithText(this,"Leave")))
        val calendar4 = Calendar.getInstance()
        calendar4.add(Calendar.DAY_OF_MONTH, 13)
        events.add(EventDay(calendar4, DrawableUtils.getThreeDots(this)))
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setSwipeEnabled(false)
        calendarView.setEvents(events)
    }
}