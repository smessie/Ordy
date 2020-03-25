package com.ordy.app.api.models.actions

import java.time.LocalTime

data class OrderCreate(

    val groupId: Int,

    val locationId: Int?,

    val customLocationName: String?,

    val deadline: LocalTime
)