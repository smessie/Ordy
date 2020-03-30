package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import javax.persistence.*

@Entity
@Table(name = "order_items")
class OrderItem (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.Id::class)
    var id: Int = 0,

    @Column(nullable = false)
    @JsonView(View.List::class)
    var comment: String = "",

    @Column(nullable = false)
    @JsonView(View.List::class)
    var paid: Boolean = false,

    @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
    @JsonView(View.List::class)
    var order: Order,

    @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
    @JsonView(View.List::class)
    var item: Item,

    @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
    @JsonView(View.List::class)
    var user: User
)