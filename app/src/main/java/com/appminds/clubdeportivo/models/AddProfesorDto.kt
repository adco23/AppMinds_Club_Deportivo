package com.appminds.clubdeportivo.models

import java.io.Serializable

data class AddProfesorDto (
    val firstname: String,
    val lastname: String,
    val dni: String,
    val address: String,
    val phone: String,
    val isSubstitute: Boolean
) : Serializable