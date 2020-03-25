package com.ordy.backend.database.models

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Cuisines")
class Cuisine (
        @Id @GeneratedValue var id: Int = 0,
        @Column(nullable = false) var name: String,
        @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
        var items: MutableSet<Item> = mutableSetOf()
) {
    fun addItem(i: Item) {
        items.add(i)
        i.cuisines.add(this)
    }

    fun removeItem(i: Item) {
        items.remove(i)
        i.cuisines.remove(this)
    }
}