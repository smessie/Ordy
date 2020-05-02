package com.ordy.backend.wrappers

import java.util.*

class OrderCreateWrapper(
        val deadline: Optional<Date>,
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

class LastNotifyUpdateWrapper(
        val lastTime: Date
)