package com.ordy.backend.wrappers

import java.time.LocalDate
import java.util.*

class OrderCreateWrapper(
        val deadline: Optional<LocalDate>,
        val groupId: Optional<Int>,
        val locationId: Optional<Int>,
        val customLocationName: Optional<String>
)

class OrderAddItemWrapper(
        val itemId: Optional<Int>,
        val customItemName: Optional<String>
)