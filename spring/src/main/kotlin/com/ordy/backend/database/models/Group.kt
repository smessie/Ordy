package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "groups")
class Group (
        @Id
        @JsonView(View.Id::class)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int = 0,

        @JsonView(View.List::class)
        @Column(nullable = false)
        var name: String,

        @JsonView(View.List::class)
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
        var creator: User
)