package com.appminds.clubdeportivo.data.model

import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum

data class ClientEntity (
    var id: Int? = 0,
    val firstname: String,
    val lastname: String,
    val dni: String,
    val email: String,
    val phone: String,
    val address: String,
    val registeredAt: String,
    val isPhysicallyFit: Boolean,
    val type: ClientTypeEnum,
    val status: ClientStatusEnum,
    var dueDate: Int = 0
)