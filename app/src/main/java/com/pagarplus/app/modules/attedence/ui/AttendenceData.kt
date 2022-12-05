package com.pagarplus.app.modules.attedence.ui

import java.io.Serializable
import java.util.*

data class AttendanceData (
     var isCheckedIn:Boolean?=null,
     var isCheckedOut:Boolean?=null,
     var VisitType:Int?=null,
     var SessionType:Int?=null,
     var LogDate: Date?=null):Serializable