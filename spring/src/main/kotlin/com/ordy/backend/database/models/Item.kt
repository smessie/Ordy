package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "Items")
class Item (
        @Id @GeneratedValue var id: Int = 0,
        @Column(nullable = false) var name: String,
        @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
        var cuisines: MutableSet<Cuisine> = mutableSetOf()
) {
    fun addCuisine(c: Cuisine) {
        cuisines.add(c)
        c.items.add(this)
    }

    fun removeCuisine(c: Cuisine) {
        cuisines.remove(c)
        c.items.remove(this)
    }
}