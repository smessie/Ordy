package com.ordy.backend.services.notifications

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.DeviceTokenRepository
import com.ordy.backend.database.repositories.GroupMemberRepository
import com.ordy.backend.database.repositories.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Service
class NotificationService(
        val deviceTokenRepository: DeviceTokenRepository,
        val orderRepository: OrderRepository,
        val groupMemberRepository: GroupMemberRepository,
        val firebaseApp: FirebaseApp
) {

    /**
     * Async method for sending a list of user the same notification.
     * This is a simple wrapper around the sendNotification method.
     */
    @Async
    fun sendNotificationAsync(users: List<User>, content: Map<String, String>) {
        sendNotification(users, content)
    }

    /**
     * Send each user of a list a notification.
     */
    private fun sendNotification(users: List<User>, content: Map<String, String>) {
        users.forEach { sendNotification(it, content) }
    }


    /**
     * Async method for sending a single user a notification.
     * This is a simple wrapper around the sendNotification method.
     */
    @Async
    fun sendNotificationAsync(user: User, content: Map<String, String>) {
        sendNotification(user, content)
    }

    /**
     * send a notification for each token of a user.
     */
    private fun sendNotification(user: User, content: Map<String, String>) {
        deviceTokenRepository.getAllByUser(user).forEach { sendNotification(it.token, content) }
    }

    /**
     * send notification for a specific token.
     */
    private fun sendNotification(target: String, content: Map<String, String>) {
        val message = Message.builder()
                .setToken(target)
                .putAllData(content)
                .build()

        try {
            FirebaseMessaging.getInstance(firebaseApp).send(message)
        } catch (e: FirebaseMessagingException) {
            LoggerFactory.getLogger(this::class.java).error(e.toString())
        }

    }

    /**
     * Create the message content given the start parameters.
     */
    fun createNotificationContent(
            title: String = "Title",
            subtitle: String = "Subtitle",
            detail: String = "Detail",
            summary: String = "Summary",
            type: NotificationType = NotificationType.INVITE_NEW,
            extra: Map<String, String> = emptyMap()): Map<String, String> {
        return mutableMapOf(
                "type" to type.toString(),
                "notificationTitle" to title,
                "notificationSubtitle" to subtitle,
                "notificationContent" to detail,
                "notificationSummary" to summary
        ).also {
            for (i in extra) {
                it[i.key] = i.value
            }
        }
    }

    /**
     * Check deadlines every minute, send notification when close to deadline.
     */
    @Scheduled(fixedRate = 30000)
    fun deadlineNotification() {
        orderRepository.findAllByNotifiedIsFalse().forEach {
            // > 10 minutes
            val difference = it.deadline.time - Date().time
            if (difference in 0..600000) {
                orderRepository.saveAndFlush(it.also { order -> order.notified = true })
                sendNotificationAsync(
                        users = groupMemberRepository.findGroupMembersByGroup(it.group)
                                .map { groupMember -> groupMember.user }, // notify all users in group
                        content = createNotificationContent(
                                title = "${(difference / (60 * 1000.0)).roundToInt()} minutes left",
                                subtitle = "The order for ${it.location.name} in group ${it.group.name} is about to close",
                                detail = "<b>Group: </b>${it.group.name}\n<b>Location: </b>${it.location.name}\n<b>Courier: </b>${it.courier.username}",
                                summary = "Hurry",
                                type = NotificationType.ORDER_DEADLINE,
                                extra = mapOf(
                                        "orderId" to it.id.toString(),
                                        "notificationDeadline" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(it.deadline)
                                )
                        )
                )
            }
        }
    }
}