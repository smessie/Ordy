package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "images")
class Image(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(View.Id::class)
        val id: Int = 0,

        @Lob
        @Column(nullable = false)
        @JsonIgnore
        val image: Array<Byte> = emptyArray(),

        @JsonIgnore
        @OneToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = true)
        val order: Order? = null
)