package com.ordy.backend.services

import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.GroupInvite
import com.ordy.backend.database.models.GroupMember
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.GroupInviteRepository
import com.ordy.backend.database.repositories.GroupMemberRepository
import com.ordy.backend.database.repositories.GroupRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.wrappers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupService(@Autowired val groupRepository: GroupRepository,
                   @Autowired val userRepository: UserRepository,
                   @Autowired val groupMemberRepository: GroupMemberRepository,
                   @Autowired val groupInviteRepository: GroupInviteRepository) {

    private val groupNameRegex = Regex("^[A-z0-9 ]+$")

    private fun checkGroupName(name: String, list: ThrowableList) {
        if (!groupNameRegex.matches(name)) {
            list.addPropertyException("name", "Name should only contain letters, numbers an spaces")
        }
    }

    fun createGroup(userId: Int, groupWrapper: GroupCreateWrapper): Group {
        val throwableList = ThrowableList()
        checkGroupName(groupWrapper.name.get(), throwableList)
        throwableList.ifNotEmpty { throw throwableList }

        val creator = userRepository.findById(userId).get()
        val group = Group(name = groupWrapper.name.get(), creator = creator)
        groupRepository.save(group)

        // the creator is automatically added to the members of the group he just created
        val groupMember = GroupMember(user = creator, group = group)
        groupMemberRepository.save(groupMember)

        return group
    }

    fun updateGroup(groupId: Int, groupWrapper: GroupCreateWrapper): Group {
        val throwableList = ThrowableList()
        checkGroupName(groupWrapper.name.get(), throwableList)

        val group: Optional<Group> = groupRepository.findById(groupId)

        if (group.isPresent) {
            group.get().name = groupWrapper.name.get()
            groupRepository.save(group.get())
        } else {
            throwableList.addGenericException("Group does not exist")
        }

        throwableList.ifNotEmpty { throw throwableList }

        return group.get()
    }

    /**
     * get groups of a specific user
     */
    fun getGroups(userId: Int): List<GroupListWrapper> {
        var user = userRepository.findById(userId).get()
        var groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }

        return groups.map {
            GroupListWrapper(
                    group = it,
                    membersCount = groupMemberRepository.findGroupMembersByGroup(it).size
            )
        }
    }

    /**
     * get groups of a specific user
     */
    fun getGroup(userId: Int, groupId: Int): GroupWrapper {
        val throwableList = ThrowableList()
        var user = userRepository.findById(userId).get()
        var group = groupRepository.findById(groupId)

        // Check if the group exists
        if (!group.isPresent) {
            throw throwableList.also { it.addGenericException("Group does not exist.") }
        }

        // Check if the user is part of the group
        if (!groupMemberRepository.findGroupMembersByGroup(group.get()).map { it.user }.contains(user)) {
            throw throwableList.also { it.addGenericException("You are not part of this group.") }
        }

        return GroupWrapper(
                group = group.get(),
                members = groupMemberRepository.findGroupMembersByGroup(group.get()).map { it.user }
        )
    }

    /**
     *  the logged in user can invite others users in groups where he is part of
     */

    fun createInvite(groupId: Int, userInviteId: Int, userId: Int) {
        val throwableList = ThrowableList()

        var invitedUser = userRepository.findById(userInviteId)
        var group = groupRepository.findById(groupId)

        if (!invitedUser.isPresent) {
            throw throwableList.also { it.addGenericException("The person you want to invite is not found!") }
        }

        if (!group.isPresent) {
            throw throwableList.also { it.addGenericException("The group with the given id does not exist!") }
        }

        if (!groupMemberRepository.findGroupMembersByUser(invitedUser.get()).filter { it.group.id == groupId }.isEmpty()) {
            throw throwableList.also { it.addGenericException("The user you want to invite is already in this group") }
        }

        // The user who sends the invite has to be in the group
        var user = userRepository.findById(userId).get()
        if (groupMemberRepository.findGroupMembersByUser(user).filter { it.group.id == groupId }.isEmpty()) {
            throw throwableList.also { it.addGenericException("You have to be in this group before you can invite other users!") }
        }

        var inviteOptional: Optional<GroupInvite> = groupInviteRepository.findGroupInviteByUserAndGroup(invitedUser.get(), group.get())
        if (inviteOptional.isPresent) {
            throw throwableList.also { it.addGenericException("This person is already invited to this group!") }
        }

        val invite = GroupInvite(user = invitedUser.get(), group = group.get())
        groupInviteRepository.save(invite)
    }

    fun deleteInvite(groupId: Int, userId: Int) {
        val throwableList = ThrowableList()

        val invitedUser = userRepository.findById(userId)
        if (!invitedUser.isPresent) {
            throwableList.addPropertyException("user", "The user from the invite you want to delete does not exist!")
        }

        val group = groupRepository.findById(groupId)
        if (!group.isPresent) {
            throwableList.addPropertyException("group", "The group of the invite you want to delete does not exist!")
        }

        throwableList.ifEmpty {
            val inviteOptional = groupInviteRepository.findGroupInviteByUserAndGroup(invitedUser.get(), group.get())

            if (!inviteOptional.isPresent) {
                throwableList.addPropertyException("invite", "The invite with given user and group does not exist")
            }

            throwableList.ifEmpty { groupInviteRepository.delete(inviteOptional.get()) }
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("failed to delete invite for userid=$userId in groupId=$groupId")
            throw throwableList
        }
    }

    /**
     * gives all the invites from a user
     */

    fun getInvites(userId: Int): List<GroupInvite> {
        val user = userRepository.findById(userId).get()
        return groupInviteRepository.findGroupInvitesByUser(user)
    }

    /**
     *  accept or deny the invite for the specific group for the logged in user
     */

    fun reactOnInvite(groupId: Int, userId: Int, inviteActionWrapper: InviteActionWrapper) {
        val throwableList = ThrowableList()

        if (!inviteActionWrapper.action.isPresent) {
            throw throwableList.also { it.addGenericException("There is no action in the InviteActionWrapper") }
        }

        val groupOptional = groupRepository.findById(groupId)
        val user = userRepository.findById(userId).get()
        if (!groupOptional.isPresent) {
            throw throwableList.also { it.addGenericException("The group (id=$groupId) from the invite does not exist!") }
        }

        val groupInviteOptional = groupInviteRepository.findGroupInviteByUserAndGroup(user, groupOptional.get())
        if (!groupInviteOptional.isPresent) {
            throw throwableList.also { it.addGenericException("You do not have an invite for group $groupId!") }
        }

        // accepting the invite means that you want to join the group
        if (inviteActionWrapper.action.get() == Action.ACCEPT) {
            val groupMember = GroupMember(user = user, group = groupOptional.get())
            groupMemberRepository.save(groupMember)
        }

        // the invite has to be deleted no matter if the action is equal to ACCEPT or DENY
        groupInviteRepository.delete(groupInviteOptional.get())
    }

    /**
     *  this is called when you want to leave a group
     */

    fun leaveGroup(groupId: Int, userId: Int) {
        val throwableList = ThrowableList()

        val user = userRepository.findById(userId).get()
        val groupOptional = groupRepository.findById(groupId)

        if (!groupOptional.isPresent) {
            throwableList.addPropertyException("group", "The group $groupId was not found!")
        }

        throwableList.ifEmpty {

            val groupMemberOptional = groupMemberRepository.findGroupMemberByUserAndGroup(user, groupOptional.get())

            if (!groupMemberOptional.isPresent) {
                throwableList.addPropertyException("groupMember", "You are not part of this group!")
            } else {
                groupMemberRepository.delete(groupMemberOptional.get())

                // we have to assign a new creator if the user was creator of the group
                if (groupOptional.get().creator.id == userId) {
                    val otherMembersOfGroup = groupMemberRepository.findGroupMembersByGroup(groupOptional.get())

                    // if there are no other users in the group, we delete the group
                    if (otherMembersOfGroup.isEmpty()) {
                        groupRepository.delete(groupOptional.get())
                    } else {
                        groupOptional.get().creator = otherMembersOfGroup.first().user
                        groupRepository.save(groupOptional.get())
                    }
                }
            }
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("Your try to leave the group failed!")
            throw throwableList
        }
    }

    /**
     *  this is called when the person who has logged in, wants to kick another member
     */

    fun deleteMember(groupId: Int, userKickId: Int, userId: Int) {
        if (userId == userKickId) { // leaving the group is the same as he want to kick himself
            this.leaveGroup(groupId, userId)
        } else {
            val throwableList = ThrowableList()

            val user = userRepository.findById(userId).get()
            val groupOptional = groupRepository.findById(groupId)

            if (!groupOptional.isPresent) {
                throwableList.addPropertyException("group", "The group $groupId was not found!")
            }

            throwableList.ifEmpty {
                val groupMemberLoginOptional = groupMemberRepository.findGroupMemberByUserAndGroup(user, groupOptional.get())

                if (!groupMemberLoginOptional.isPresent) {
                    throwableList.addPropertyException("user", "You have to be in the group $groupId if you want to kick someone!")
                }

                val userKickOptional = userRepository.findById(userKickId)

                if (!userKickOptional.isPresent) {
                    throwableList.addPropertyException("userKick", "The person $userKickId you want to kick was not found")
                }

                throwableList.ifEmpty {
                    val groupMemberKickOptional = groupMemberRepository.findGroupMemberByUserAndGroup(userKickOptional.get(), groupOptional.get())

                    if (!groupMemberKickOptional.isPresent) {
                        throwableList.addPropertyException("userKick", "The person $userKickId you want to kick is not in group $groupId")
                    } else {

                        // if the kicked person was the creator of the group, then you become the creator of the group
                        if (groupOptional.get().creator.id == userKickId) {
                            groupOptional.get().creator = user
                            groupRepository.save(groupOptional.get())
                        }

                        groupMemberRepository.delete(groupMemberKickOptional.get())
                    }
                }
            }

            throwableList.ifNotEmpty {
                throwableList.addGenericException("The action to kick person $userKickId failed!")
                throw throwableList
            }
        }
    }

    /**
     *  this gives a list of users that matches the given username
     */

    fun searchMatchingInviteUsers(groupId: Int, userName: String, userId: Int): List<User> {
        val throwableList = ThrowableList()

        val user = userRepository.findById(userId).get()
        val groupOptional = groupRepository.findById(groupId)

        if (!groupOptional.isPresent) {
            throwableList.addPropertyException("group", "Group $groupId was not found!")
        }

        if (!groupMemberRepository.findGroupMemberByUserAndGroup(user, groupOptional.get()).isPresent) {
            throwableList.addPropertyException("user", "You have to be in this group (groupId=$groupId) before you can invite users!")
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("Searching failed!")
            throw throwableList
        }

        return userRepository.findAll()
                .filter { it.username.contains(userName) }
                .filter { !groupMemberRepository.findGroupMemberByUserAndGroup(it, groupOptional.get()).isPresent }
    }
}