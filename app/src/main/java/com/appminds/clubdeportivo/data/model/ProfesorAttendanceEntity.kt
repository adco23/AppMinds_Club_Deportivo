package com.appminds.clubdeportivo.data.model

import com.appminds.clubdeportivo.models.enums.AttendanceStatus
import java.io.Serializable

data class ProfesorAttendanceEntity(
    var id: Int? = null,
    val idProfesor: Int,
    val date: String,
    val status: AttendanceStatus
) : Serializable
