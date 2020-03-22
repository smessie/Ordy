package com.ordy.backend.database.models

import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "Orders")
class Order (
        @Id @GeneratedValue var id: Int = 0,
        @Column(nullable = false) var deadline: LocalTime,
        @Column(nullable = true, name = "bill_url", length = 512) var billUrl: String,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var group: Group,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var creator: User,
        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false) var location: Location
)