package com.ordy.backend.services

import com.google.firebase.FirebaseApp
import com.ordy.backend.TestUtils
import com.ordy.backend.database.repositories.DeviceTokenRepository
import com.ordy.backend.database.repositories.GroupMemberRepository
import com.ordy.backend.database.repositories.OrderRepository
import com.ordy.backend.services.notifications.NotificationService
import com.ordy.backend.services.notifications.NotificationType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class NotificationServiceTest {

    @InjectMocks
    private lateinit var notificationService: NotificationService

    @Mock
    private lateinit var deviceTokenRepository: DeviceTokenRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var groupMemberRepository: GroupMemberRepository

    @Mock
    private lateinit var firebaseApp: FirebaseApp

    private val testUtils = TestUtils()

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Nested
    inner class Unit {

        @Test
        fun `Should create valid NotificationContent`() {
            val title = testUtils.faker.book().title()
            val subtitle = testUtils.faker.lorem().sentence(5)
            val detail = testUtils.faker.lorem().sentence(15)
            val summary = testUtils.faker.lorem().sentence(3)

            Assertions.assertEquals(notificationService.createNotificationContent(
                    title = title,
                    subtitle = subtitle,
                    detail = detail,
                    summary = summary,
                    type = NotificationType.INVITE_NEW
            ), mapOf(
                    "type" to NotificationType.INVITE_NEW.toString(),
                    "notificationTitle" to title,
                    "notificationSubtitle" to subtitle,
                    "notificationContent" to detail,
                    "notificationSummary" to summary
            ))
        }
    }
}