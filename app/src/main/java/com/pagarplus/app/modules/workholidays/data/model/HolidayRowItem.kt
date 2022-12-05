package com.pagarplus.app.modules.workholidays.data.model
data class HolidayRowItem(
    var Id: Int? = null,

    var OrgId: Int? = null,

    var AdminID: Int? = null,

    var Date: String? = null,

    var IsHalfDay: Boolean? = false,

    var IsActive: Boolean? = null,

    var HolidayTypeID: Int? = null,

    var HolidayType: String? = null,

    var Comment: String? = null,

    var HolidayHead: String? = null,

    var HolidayWeek: String? = null,
)