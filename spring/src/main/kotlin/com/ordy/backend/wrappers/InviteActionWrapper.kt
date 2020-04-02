package com.ordy.backend.wrappers

import java.util.*

enum class InviteAction {
    ACCEPT, DENY
}
class InviteActionWrapper (
    val action: Optional<String>
)