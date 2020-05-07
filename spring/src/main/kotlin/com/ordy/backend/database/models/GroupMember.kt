package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "group_members")
class GroupMember(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int = 0,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var user: User,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var group: Group
)