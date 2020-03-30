package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "users")
class User (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
        @Column(nullable = false) var username: String,
        @Column(nullable = false, length = 320, unique = true) var email: String,
        @Column(nullable = false) var password: String
)