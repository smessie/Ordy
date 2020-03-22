package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "Groups")
class Group (
        @Id @GeneratedValue var id: Int = 0,
        @Column(nullable = false) var name: String,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var creator: User
)