package com.ordy.backend.database.models

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "orders")
class Order (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
        @Column(nullable = false) var deadline: LocalDate,
        @Column(nullable = true, name = "bill_url", length = 512) var billUrl: String = "",
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var group: Group,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var courier: User,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var location: Location
)