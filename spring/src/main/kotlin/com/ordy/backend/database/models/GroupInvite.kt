package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "GroupInvites")
class GroupInvite (
        @Id @GeneratedValue val id: Int = 0,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var user: User,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var group: Group
)