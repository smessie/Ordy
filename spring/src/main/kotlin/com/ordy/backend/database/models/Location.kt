package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "Locations")
class Location (
        @Id @GeneratedValue var id: Int = 0,
        @Column(nullable = false) var name: String,
        @Column(nullable = true) var latitude: Int,
        @Column(nullable = true) var longitude: Int,
        @Column(nullable = true) var address: String,
        @Column(nullable = true) var private: Boolean,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var cuisine: Cuisine
)