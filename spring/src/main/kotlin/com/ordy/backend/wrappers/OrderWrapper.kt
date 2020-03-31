package com.ordy.backend.wrappers

import java.time.LocalDateTime
import java.util.*

class OrderCreateWrapper(
        val deadline: Optional<LocalDateTime>,
        val groupId: Optional<Int>,
        val locationId: Optional<Int>,
        val customLocationName: Optional<String>
)

class OrderAddItemWrapper(
        val itemId: Optional<Int>,
        val customItemName: Optional<String>
)

class OrderUpdateItemWrapper(
        val comment: Optional<String>
)