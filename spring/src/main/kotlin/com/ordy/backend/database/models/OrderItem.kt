package com.ordy.backend.database.models

import javax.persistence.*

@Entity
@Table(name = "orderItems")
class OrderItem (
    @Id @GeneratedValue var id: Int = 0,
    @Column(nullable = false) var comment: String = "",
    @Column(nullable = false) var paid: Boolean = false,
    @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var order: Order,
    @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var item: Item,
    @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var user: User
)