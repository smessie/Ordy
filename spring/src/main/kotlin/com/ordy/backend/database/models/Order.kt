package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "orders")
class Order (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(View.Id::class)
        var id: Int = 0,

        @Column(nullable = false)
        @JsonView(View.List::class)
        var deadline: LocalDateTime,

        @Column(nullable = true, name = "bill_url", length = 512)
        @JsonView(View.Detail::class)
        var billUrl: String = "",

        @JsonView(View.List::class)
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
        var group: Group,

        @JsonView(View.List::class)
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
        var courier: User,

        @JsonView(View.List::class)
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
        var location: Location,

        @JsonView(View.Detail::class)
        @OneToMany(mappedBy="order")
        var orderItems: Set<OrderItem> = emptySet()
)