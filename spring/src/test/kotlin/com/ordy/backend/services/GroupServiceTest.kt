package com.ordy.backend.services

import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.GroupInvite
import com.ordy.backend.database.models.GroupMember
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.*
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.services.notifications.NotificationService
import com.ordy.backend.wrappers.GroupCreateWrapper
import com.ordy.backend.wrappers.InviteActionWrapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class GroupServiceTest {

    @InjectMocks
    private lateinit var groupService: GroupService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var groupMemberRepository: GroupMemberRepository

    @Mock
    private lateinit var groupInviteRepository: GroupInviteRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var orderItemRepository: OrderItemRepository

    @Mock
    private lateinit var notificationService: NotificationService

    private var faker = Faker()

    private lateinit var testUser: User
    private lateinit var testUserTwee: User
    private lateinit var testGroup: Group
    private lateinit var testGroupMember: GroupMember
    private lateinit var testGroupMembers: List<GroupMember>

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)

        testUser = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        testUserTwee = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        testGroup = Group(name = giveValidGroupName(), creator = testUser)
        testGroupMember = GroupMember(id = faker.number().numberBetween(1, 1000000), user = testUser, group = testGroup)
        testGroupMembers = listOf(
                GroupMember(id = faker.number().numberBetween(1, 1000000), user = testUser, group = testGroup),
                GroupMember(id = faker.number().numberBetween(1, 1000000), user = testUserTwee, group = testGroup)
        )
    }

    private fun giveValidGroupName(): String {
        val groupNameRegex = Regex("^[A-z0-9 ]+$")

        var name = faker.name().name()
        while (!groupNameRegex.matches(name)) {
            name = faker.name().name()
        }

        return name
    }

    @Test
    fun `User should be able to create a group`() {
        val groupCreateWrapper = GroupCreateWrapper(Optional.of(giveValidGroupName()))

        val savedGroup = Group(
                name = groupCreateWrapper.name.get(),
                creator = testUser
        )

        val savedGroupMember = GroupMember(
                group = savedGroup,
                user = testUser
        )

        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupRepository.save<Group>(any())).thenReturn(savedGroup)
        whenever(groupMemberRepository.save<GroupMember>(any())).thenReturn(savedGroupMember)

        val newGroup = groupService.createGroup(testUser.id, groupCreateWrapper)

        check(newGroup.name == savedGroup.name && newGroup.creator == savedGroup.creator)
        verify(groupRepository).save<Group>(any())
        verify(groupMemberRepository).save<GroupMember>(any())
    }

    @Test
    fun `User should be able to invite someone`() {

        val userToInvite = User(
                faker.number().numberBetween(1, 1000000),
                faker.name().name(),
                faker.internet().emailAddress(),
                faker.internet().password()
        )

        whenever(userRepository.findById(userToInvite.id)).thenReturn(Optional.of(userToInvite))
        whenever(userRepository.findById(testUser.id)).thenReturn(Optional.of(testUser))
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(testGroupMember))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(userToInvite, testGroup)).thenReturn(Optional.empty())
        whenever(groupInviteRepository.findGroupInviteByUserAndGroup(userToInvite, testGroup)).thenReturn(Optional.empty())
        whenever(groupInviteRepository.save<GroupInvite>(any())).thenReturn(GroupInvite(user = userToInvite, group = testGroup))

        groupService.createInvite(testGroup.id, userToInvite.id, testUser.id)

        verify(groupInviteRepository).save<GroupInvite>(any())
        verify(notificationService).sendNotificationAsync(eq(userToInvite), any())
    }

    @Test
    fun `Invite should be deleted`() {
        val groupInvite = GroupInvite(
                user = testUser,
                group = testGroup
        )

        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(groupInviteRepository.findGroupInviteByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(groupInvite))

        groupService.deleteInvite(testGroup.id, testUser.id)

        verify(groupInviteRepository).delete(groupInvite)
    }

    @Test
    fun `Should return all the invites from the user`() {
        val groupInvites = listOf(
                GroupInvite(
                        user = testUser,
                        group = testGroup
                )
        )

        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupInviteRepository.findGroupInvitesByUser(testUser)).thenReturn(groupInvites)

        groupService.getInvites(testUser.id)

        verify(groupInviteRepository).findGroupInvitesByUser(any())
    }

    @Test
    fun `Should accept an invite`() {
        val inviteAction = InviteActionWrapper(
                action = Optional.of("ACCEPT")
        )

        val groupInvite = GroupInvite(
                user = testUser,
                group = testGroup
        )

        val groupMember = GroupMember(user = groupInvite.user, group = groupInvite.group)

        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupInviteRepository.findGroupInviteByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(groupInvite))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUser, testGroup)).thenReturn(Optional.empty())
        whenever(groupMemberRepository.save<GroupMember>(groupMember)).thenReturn(groupMember)

        groupService.reactOnInvite(testGroup.id, testUser.id, inviteAction)

        verify(groupMemberRepository).save<GroupMember>(any())
        verify(groupInviteRepository).delete(any())
    }

    @Test
    fun `Should decline an invite`() {
        val inviteAction = InviteActionWrapper(
                action = Optional.of("DENY")
        )

        val groupInvite = GroupInvite(
                user = testUser,
                group = testGroup
        )

        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupInviteRepository.findGroupInviteByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(groupInvite))

        groupService.reactOnInvite(testGroup.id, testUser.id, inviteAction)

        // No new member should be created when denying an invite
        verify(groupMemberRepository, never()).save<GroupMember>(any())
        verify(groupInviteRepository).delete(any())
    }

    /**
     * If a user leaves a group of which he is only a part of, this group must be removed.
     */
    @Test
    fun `User should leave the group and group should be deleted`() {
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(testGroupMember))
        whenever(groupMemberRepository.findGroupMembersByGroup(testGroup)).thenReturn(emptyList())
        whenever(orderRepository.findAllByGroup(testGroup)).thenReturn(emptyList())

        groupService.leaveGroup(testGroup.id, testUser.id)

        verify(groupRepository, never()).save<Group>(any())
        verify(groupInviteRepository).deleteAll(any())
        verify(groupMemberRepository).delete(any())
        verify(groupRepository).delete(any())
    }

    /**
     * If a user leaves a group of which he is NOT only a part of, this group must get a new creator and must be updated.
     */
    @Test
    fun `User should leave but the group has to be updated`() {
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(testGroupMember))
        whenever(groupMemberRepository.findGroupMembersByGroup(testGroup)).thenReturn(
                listOf(
                        GroupMember(
                                user = testUserTwee,
                                group = testGroup
                        )
                )
        )

        whenever(groupRepository.save(testGroup)).thenReturn(Group(name = testGroup.name, creator = testUserTwee))

        groupService.leaveGroup(testGroup.id, testUser.id)

        verify(groupRepository).save<Group>(any())
        verify(groupInviteRepository, never()).deleteAll(any())
        verify(groupMemberRepository).delete(any())
        verify(groupRepository, never()).delete(any())
    }

    @Test
    fun `Should remove a member of the group`() {
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(testUserTwee.id)).thenReturn(Optional.of(testUserTwee))
        whenever(userRepository.findById(testUser.id)).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(testGroupMembers[0]))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUserTwee, testGroup)).thenReturn(Optional.of(testGroupMembers[1]))

        groupService.deleteMember(testGroup.id, testUserTwee.id, testUser.id)

        verify(groupMemberRepository).delete(any())
    }

    @Test
    fun `Should not be able to remove the creator of the group`() {
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(testUserTwee.id)).thenReturn(Optional.of(testUserTwee))
        whenever(userRepository.findById(testUser.id)).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(testGroupMembers[0]))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUserTwee, testGroup)).thenReturn(Optional.of(testGroupMembers[1]))

        try {
            groupService.deleteMember(testGroup.id, testUser.id, testUserTwee.id)
        } catch (e: ThrowableList) {
            Assertions.assertEquals("You can not kick the creator of a group.", e.generalErrors[0].message)
        }

        verify(groupMemberRepository, never()).delete(any())
    }

    @Test
    fun `Should return all matching users that are able to invite for the group`() {

        val username = "El"

        val alreadyInvitedUsers = mutableListOf<GroupInvite>()
        for (i in 0..5) {
            alreadyInvitedUsers.add(GroupInvite(
                    user = User(id = faker.number().numberBetween(1, 1000000), username = username + faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password()),
                    group = testGroup
            ))
        }

        val allUsers: MutableList<User> = (alreadyInvitedUsers.map { it.user } + testGroupMembers.map { it.user }) as MutableList<User>
        for (i in 0..5) {
            if (i % 2 == 0) {
                var randomUsername = faker.name().username()

                // Random user name may not contain "El"
                while (randomUsername.contains(username, ignoreCase = true)) {
                    randomUsername = faker.name().username()
                }

                allUsers.add(
                        User(
                                id = faker.number().numberBetween(1, 1000000),
                                username = randomUsername,
                                email = faker.internet().emailAddress(),
                                password = faker.internet().password()
                        )
                )
            } else {
                allUsers.add(
                        User(
                                id = faker.number().numberBetween(1, 1000000),
                                username = username + faker.name().username(),
                                email = faker.internet().emailAddress(),
                                password = faker.internet().password()
                        )
                )
            }
        }

        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMemberByUserAndGroup(testUser, testGroup)).thenReturn(Optional.of(testGroupMembers[0]))
        whenever(groupMemberRepository.findGroupMembersByGroup(testGroup)).thenReturn(testGroupMembers)
        whenever(groupInviteRepository.findGroupInvitesByGroup(testGroup)).thenReturn(alreadyInvitedUsers)
        whenever(userRepository.findAll()).thenReturn(allUsers)

        val results = groupService.searchMatchingInviteUsers(testGroup.id, username, testUser.id)

        check(results.size == 9) { "${results.size}" }
    }

    @Test
    fun `Group should be updated`() {
        val groupCreateWrapper = GroupCreateWrapper(name = Optional.of(faker.name().firstName()))

        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(groupRepository.save<Group>(any())).thenReturn(Group(id = testGroup.id, name = groupCreateWrapper.name.get(), creator = testGroup.creator))

        val updatedGroup = groupService.updateGroup(testGroup.id, groupCreateWrapper)

        verify(groupRepository).save<Group>(any())
        check(updatedGroup.name == groupCreateWrapper.name.get())
    }

    @Test
    fun `Group should not be updated because of invalid name`() {
        val groupCreateWrapper = GroupCreateWrapper(name = Optional.of("./+=}&" + faker.name().firstName()))

        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))

        try {
            groupService.updateGroup(testGroup.id, groupCreateWrapper)
        } catch (e: ThrowableList) {
            Assertions.assertEquals("Name should only contain letters, numbers and spaces", e.inputErrors[0].message)
        }

        verify(groupRepository, never()).save<Group>(any())
    }

    @Test
    fun `Group should not be updated because of no name was given`() {
        val groupCreateWrapper = GroupCreateWrapper(name = Optional.empty())

        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))

        try {
            groupService.updateGroup(testGroup.id, groupCreateWrapper)
        } catch (e: ThrowableList) {
            Assertions.assertEquals("No name was given. Please try again.", e.generalErrors[0].message)
        }

        verify(groupRepository, never()).save<Group>(any())
    }

    @Test
    fun `Should return all the groups that contains the user`() {
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMembersByUser(testUser)).thenReturn(listOf(testGroupMember))

        val results = groupService.getGroups(testUser.id)

        check(results.size == 1)
    }

    @Test
    fun `Should return a group of the user`() {
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMembersByGroup(testGroup)).thenReturn(testGroupMembers)

        val groupWrapper = groupService.getGroup(testUser.id, testGroup.id)

        check(groupWrapper.members.size == testGroupMembers.size)
        check(groupWrapper.group == testGroup)
    }

    @Test
    fun `Should not return a group of the user`() {
        whenever(groupRepository.findById(anyInt())).thenReturn(Optional.of(testGroup))
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMembersByGroup(testGroup)).thenReturn(emptyList())

        try {
            groupService.getGroup(testUser.id, testGroup.id)
        } catch (e: ThrowableList) {
            Assertions.assertEquals("You are not part of this group.", e.generalErrors[0].message)
        }
    }
}