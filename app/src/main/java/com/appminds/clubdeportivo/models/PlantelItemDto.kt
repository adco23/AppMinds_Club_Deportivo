package com.appminds.clubdeportivo.models

import java.io.Serializable

data class PlantelItemDto(
    val fullname: String,
    val activity: String,
    val dni: String,
    val address: String,
    val phone: String,
    val isSubstitute: Boolean
) : Serializable
