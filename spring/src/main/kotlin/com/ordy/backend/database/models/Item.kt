package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "items")
class Item (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(View.List::class)
        var id: Int = 0,

        @Column(nullable = false)
        @JsonView(View.List::class)
        var name: String
)