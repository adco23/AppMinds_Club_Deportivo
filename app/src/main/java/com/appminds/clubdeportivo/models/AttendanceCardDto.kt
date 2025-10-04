package com.appminds.clubdeportivo.models

import com.appminds.clubdeportivo.models.enums.AttendanceStatus

data class AttendanceCardDto (
    val fullname: String,
    var status: AttendanceStatus,
    val activity: String
)