package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
        @Column(nullable = false) var name: String,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var creator: User
)