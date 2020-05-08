package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "locations")
class Location(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(View.Id::class)
        var id: Int = 0,

        @Column(nullable = false)
        @JsonView(View.List::class)
        var name: String,

        @Column(nullable = true)
        @JsonView(View.Detail::class)
        var latitude: Double?,

        @Column(nullable = true)
        @JsonView(View.Detail::class)
        var longitude: Double?,

        @Column(nullable = true)
        @JsonView(View.List::class)
        var address: String = "",

        @Column(nullable = true)
        @JsonView(View.Ignore::class)
        var private: Boolean = true,

        @JsonView(View.Ignore::class)
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = true)
        var cuisine: Cuisine?
)
