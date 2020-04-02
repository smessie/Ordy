package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "cuisines")
class Cuisine (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(View.Id::class)
        var id: Int = 0,

        @Column(nullable = false)
        @JsonView(View.Ignore::class)
        var name: String,

        @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
        @JsonView(View.Detail::class)
        var items: MutableSet<Item> = mutableSetOf()
)