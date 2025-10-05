package com.appminds.clubdeportivo.models

import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum

data class ClientDto(
    val firstname: String,
    val lastname: String,
    val dni: String,
    val address: String,
    val phone: String,
    val email: String,
    val submittedDocument: Boolean,
    val type: ClientTypeEnum,
    val status: ClientStatusEnum
)
