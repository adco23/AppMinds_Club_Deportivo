package com.appminds.clubdeportivo.data.model

import java.io.Serializable

data class ProfesorEntity(
    var id: Int? = null,
    val firstname: String,
    val lastname: String,
    val dni: String,
    val address: String,
    val phone: String,
    val isSubstitute: Boolean,
    var activity: String? = null
) : Serializable
