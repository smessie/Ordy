package com.ordy.backend.wrappers

import java.util.*

enum class Action {
    ACCEPT, DENY
}
class InviteActionWrapper (
    val action: Optional<Action>
)