package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "group_invites")
class GroupInvite(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(View.Id::class)
        val id: Int = 0,

        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
        @JsonView(View.List::class)
        var user: User,

        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
        @JsonView(View.List::class)
        var group: Group
)