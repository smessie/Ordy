package com.ordy.backend.services.notifications

enum class NotificationType {
    ORDER_CREATE {
        override fun toString() = "ORDER_CREATE"
    },
    ORDER_DEADLINE {
        override fun toString() = "ORDER_DEADLINE"
    },
    ORDER_BILL {
        override fun toString() = "ORDER_BILL"
    },
    PAYMENT_DEBT {
        override fun toString() = "PAYMENT_DEBT"
    },
    INVITE_NEW {
        override fun toString() = "INVITE_NEW"
    }
}