package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "Cuisines")
class Cuisine (
        @Id @GeneratedValue var id: Int = 0,
        @Column(nullable = false) var name: String,
        @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY) var items: List<Item> = mutableListOf()
)