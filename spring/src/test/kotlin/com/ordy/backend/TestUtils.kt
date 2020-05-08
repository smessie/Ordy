package com.ordy.backend

import com.github.javafaker.Faker
import com.ordy.backend.database.models.*
import java.util.*

class TestUtils {

    val faker = Faker()

    fun getOrder(
            deadline: Date = Date(Date().time + 1000 * 60 * 10), // NOW + 10 Min
            billUrl: String = faker.internet().url(),
            notified: Boolean = false,
            group: Group = getGroup(),
            courier: User = getUser(),
            location: Location = getLocation(),
            orderItems: Set<OrderItem> = emptySet()
    ): Order {
        return Order(
                deadline = deadline,
                billUrl = billUrl,
                notified = notified,
                group = group,
                courier = courier,
                location = location,
                orderItems = orderItems
        )
    }

    fun getGroup(
            name: String = faker.name().firstName(),
            creator: User = getUser()
    ): Group {
        return Group(
                name = name,
                creator = creator
        )
    }

    fun getUser(
            username: String = faker.name().firstName(),
            email: String = faker.internet().safeEmailAddress(),
            password: String = faker.internet().password(8, 64, true, true, true)
    ): User {
        return User(
                username = username,
                email = email,
                password = password
        )
    }

    fun getLocation(
            name: String = faker.company().name(),
            address: String = faker.address().fullAddress(),
            private: Boolean = false,
            latitude: Double? = faker.number().randomDouble(5, -100, 100),
            longitude: Double? = faker.number().randomDouble(5, -100, 100),
            cuisine: Cuisine? = getCuisine()
    ): Location {
        return Location(
                name = name,
                address = address,
                private = private,
                latitude = latitude,
                longitude = longitude,
                cuisine = cuisine
        )
    }

    fun getCuisine(
            name: String = faker.name().firstName(),
            items: MutableSet<Item> = mutableSetOf()
    ): Cuisine {
        return Cuisine(
                name = name,
                items = items
        )
    }

    fun getGroupMember(
            user: User = getUser(),
            group: Group = getGroup()
    ): GroupMember {
        return GroupMember(
                user = user,
                group = group
        )
    }

    fun getDeviceToken(
            user: User = getUser(),
            token: String = faker.internet().password(64, 128, true, true, true)
    ) :DeviceToken {
        return DeviceToken(user = user, token = token)
    }
}