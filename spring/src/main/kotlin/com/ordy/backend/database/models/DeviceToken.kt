package com.ordy.backend.database.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "device_tokens")
class DeviceToken(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonIgnore
        var id: Int = 0,

        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
        @JsonIgnore
        var user: User,

        @Column(unique = true, nullable = false, length = 255)
        @JsonIgnore
        var token: String
)