package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "users")
class User (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(View.Id::class)
        var id: Int = 0,

        @Column(nullable = false)
        @JsonView(View.List::class)
        var username: String,

        @Column(nullable = false, length = 320, unique = true)
        @JsonView(View.Detail::class)
        var email: String,

        @Column(nullable = false)
        @JsonView(View.Ignore::class)
        var password: String
)