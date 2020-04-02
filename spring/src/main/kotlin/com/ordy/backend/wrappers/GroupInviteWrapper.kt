package com.ordy.backend.wrappers

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.User

class GroupInviteListWrapper(

        @JsonView(View.Id::class)
        val id: Int,

        @JsonView(View.List::class)
        val group: GroupListWrapper,
        
        @JsonView(View.List::class)
        val user: User
)