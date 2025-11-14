package com.appminds.clubdeportivo.models

import com.appminds.clubdeportivo.models.enums.AttendanceStatus

data class AttendanceCardDto (
    var id: Int?,
    val fullname: String,
    var status: AttendanceStatus,
    val activity: String
)