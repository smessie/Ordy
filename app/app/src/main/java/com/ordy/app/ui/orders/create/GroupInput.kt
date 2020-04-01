package com.ordy.app.ui.orders.create

import com.ordy.app.api.models.Group

data class GroupInput(
    val group: Group
) {

    override fun toString(): String {
        return group.name
    }
}