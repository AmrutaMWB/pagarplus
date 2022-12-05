package com.pagarplus.app.modules.workholidays.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pagarplus.app.R
import org.naishadhparmar.zcustomcalendar.CustomCalendar
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener
import org.naishadhparmar.zcustomcalendar.Property
import java.util.*

class CalenderActivity : AppCompatActivity(), OnNavigationButtonClickedListener {
    // Initialize variable
    var customCalendar: CustomCalendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)
        setCurrentMonth()
    }

    fun setCurrentMonth(){
        // assign variable
        customCalendar = findViewById(R.id.custom_calendar)
        // Initialize description hashmap
        val descHashMap = HashMap<Any, Property>()

        // Initialize default property
        val defaultProperty = Property()

        // Initialize default resource
        defaultProperty.layoutResource = R.layout.default_view

        // Initialize and assign variable
        defaultProperty.dateTextViewResource = R.id.text_view

        // Put object and property
        descHashMap["default"] = defaultProperty

        // for current date
        val currentProperty = Property()
        currentProperty.layoutResource = R.layout.current_view
        currentProperty.dateTextViewResource = R.id.text_view
        descHashMap["current"] = currentProperty

        // for present date
        val presentProperty = Property()
        presentProperty.layoutResource = R.layout.present_view
        presentProperty.dateTextViewResource = R.id.text_view
        descHashMap["present"] = presentProperty

        // For absent
        val absentProperty = Property()
        absentProperty.layoutResource = R.layout.absent_view
        absentProperty.dateTextViewResource = R.id.text_view
        descHashMap["absent"] = absentProperty

        // set desc hashmap on custom calendar
        customCalendar!!.setMapDescToProp(descHashMap)

        // Initialize date hashmap
        val dateHashmap = HashMap<Int, Any>()

        // initialize calendar
        val calendar = Calendar.getInstance()

        // Put values
        dateHashmap[calendar[Calendar.DAY_OF_MONTH]] = "current"
        dateHashmap[1] = "present"
        dateHashmap[2] = "absent"
        dateHashmap[3] = "present"
        dateHashmap[4] = "absent"
        dateHashmap[20] = "present"
        dateHashmap[30] = "absent"

        // set date
        customCalendar!!.setDate(calendar, dateHashmap)
        val views= customCalendar!!.allViews

        customCalendar!!.setOnDateSelectedListener(OnDateSelectedListener { view, selectedDate, desc -> // get string date
            val sDate = (selectedDate[Calendar.DAY_OF_MONTH]
                .toString() + "/" + (selectedDate[Calendar.MONTH] + 1)
                    + "/" + selectedDate[Calendar.YEAR])

            // display date in toast
            Toast.makeText(applicationContext, sDate, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onNavigationButtonClicked(whichButton: Int, newMonth: Calendar): Array<MutableMap<Int, Any>?>? {
        val arr: Array<MutableMap<Int, Any>?> = arrayOfNulls(0)
            //arrayOfNulls<MutableMap<0, *>?>(2)
       setCurrentMonth()
        return arr
    }
}
