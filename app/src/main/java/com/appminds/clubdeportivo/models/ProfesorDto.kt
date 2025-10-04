package com.appminds.clubdeportivo.models

import java.io.Serializable

data class ProfesorDto(
    val firstname: String,
    val lastname: String,
    val activity: String,
    val dni: String,
    val address: String,
    val phone: String,
    val isSubstitute: Boolean
) : Serializable
