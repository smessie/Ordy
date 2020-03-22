package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "Users")
class User (
        @Id @GeneratedValue var id: Int = 0,
        @Column(nullable = false) var name: String,
        @Column(nullable = false, length = 320) var email: String,
        @Column(nullable = false) var password: String
)