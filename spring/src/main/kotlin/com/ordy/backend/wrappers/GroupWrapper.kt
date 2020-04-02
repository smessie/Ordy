package com.ordy.backend.wrappers

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.User
import java.util.*

class GroupCreateWrapper(
        val name: Optional<String>
)

class GroupListWrapper(
        @JsonUnwrapped
        @JsonView(View.List::class)
        val group: Group,

        @JsonView(View.List::class)
        val membersCount: Int
)

class GroupWrapper(
        @JsonUnwrapped
        @JsonView(View.Detail::class)
        val group: Group,

        @JsonView(View.Detail::class)
        val members: List<User>
)