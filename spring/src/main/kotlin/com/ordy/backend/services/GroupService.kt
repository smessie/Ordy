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
        val user = userRepository.findById(userId).get()
        val groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }

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
        val user = userRepository.findById(userId).get()
        val group = groupRepository.findById(groupId)

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

        val invitedUser = userRepository.findById(userInviteId)
        val group = groupRepository.findById(groupId)

        if (!invitedUser.isPresent) {
            throwableList.addPropertyException("invitedUser", "This user was not found.")
        }

        if (!group.isPresent) {
            throwableList.addPropertyException("group", "This group was not found.")
        }

        throwableList.ifEmpty {
            if (groupMemberRepository.findGroupMemberByUserAndGroup(invitedUser.get(), group.get()).isPresent) {
                throwableList.addPropertyException("invitedUser", "This user is already a member of this group.")
            }

            // The user who sends the invite has to be in the group
            val user = userRepository.findById(userId).get()
            if (!groupMemberRepository.findGroupMemberByUserAndGroup(user, group.get()).isPresent) {
                throwableList.addPropertyException("user", "You have to be in this group before you can invite other users.")
            }

            val inviteOptional = groupInviteRepository.findGroupInviteByUserAndGroup(invitedUser.get(), group.get())
            if (inviteOptional.isPresent) {
                throwableList.addPropertyException("invitedUser", "This user is already invited to this group.")
            }

            throwableList.ifEmpty {
                val invite = GroupInvite(user = invitedUser.get(), group = group.get())
                groupInviteRepository.save(invite)
            }
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("Unable to invite user.")
            throw throwableList
        }
    }

    fun deleteInvite(groupId: Int, userId: Int) {
        val throwableList = ThrowableList()

        val invitedUser = userRepository.findById(userId)
        if (!invitedUser.isPresent) {
            throwableList.addPropertyException("user", "This user was not found.")
        }

        val group = groupRepository.findById(groupId)
        if (!group.isPresent) {
            throwableList.addPropertyException("group", "This group was not found.")
        }

        throwableList.ifEmpty {
            val inviteOptional = groupInviteRepository.findGroupInviteByUserAndGroup(invitedUser.get(), group.get())

            if (!inviteOptional.isPresent) {
                throwableList.addPropertyException("invite", "This invite does not exist.")
            }

            throwableList.ifEmpty { groupInviteRepository.delete(inviteOptional.get()) }
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("Unable to remove invite.")
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
            throwableList.addPropertyException("inviteActionWrapper", "No action was specified.")
        }

        val groupOptional = groupRepository.findById(groupId)
        val user = userRepository.findById(userId).get()
        if (!groupOptional.isPresent) {
            throwableList.addPropertyException("group", "This group was not found.")
        }

        throwableList.ifEmpty {
            val groupInviteOptional = groupInviteRepository.findGroupInviteByUserAndGroup(user, groupOptional.get())
            if (!groupInviteOptional.isPresent) {
                throwableList.addPropertyException("groupInvite", "You do not have an invite for this group.")
            }

            val inviteAction: InviteAction
            try {
                inviteAction = InviteAction.valueOf(inviteActionWrapper.action.get().toUpperCase())
            } catch (e: IllegalArgumentException) {
                throwableList.addGenericException("Wrong action was passed! This must be ACCEPT or DENY")
                throw throwableList
            }

            throwableList.ifEmpty {
                // accepting the invite means that you want to join the group
                if (inviteAction == InviteAction.ACCEPT) {

                    if (groupMemberRepository.findGroupMemberByUserAndGroup(user, groupOptional.get()).isPresent) {
                        throwableList.addPropertyException("user", "You are already in this group.")
                    } else {
                        val groupMember = GroupMember(user = user, group = groupOptional.get())
                        groupMemberRepository.save(groupMember)
                    }
                }

                throwableList.ifEmpty {
                    // the invite has to be deleted no matter if the action is equal to ACCEPT or DENY
                    groupInviteRepository.delete(groupInviteOptional.get())
                }
            }
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("Unable to to accept/deny an invite.")
            throw throwableList
        }
    }

    /**
     *  this is called when you want to leave a group
     */

    fun leaveGroup(groupId: Int, userId: Int) {
        val throwableList = ThrowableList()

        val user = userRepository.findById(userId).get()
        val groupOptional = groupRepository.findById(groupId)

        if (!groupOptional.isPresent) {
            throwableList.addPropertyException("group", "This group was not found.")
        }

        throwableList.ifEmpty {

            val groupMemberOptional = groupMemberRepository.findGroupMemberByUserAndGroup(user, groupOptional.get())

            if (!groupMemberOptional.isPresent) {
                throwableList.addPropertyException("groupMember", "You are not part of this group.")
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
            throwableList.addGenericException("Unable to leave group.")
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
                throwableList.addPropertyException("group", "This group was not found.")
            }

            throwableList.ifEmpty {
                val groupMemberLoginOptional = groupMemberRepository.findGroupMemberByUserAndGroup(user, groupOptional.get())

                if (!groupMemberLoginOptional.isPresent) {
                    throwableList.addPropertyException("user", "You have to be in the group in order to kick someone.")
                }

                val userKickOptional = userRepository.findById(userKickId)

                if (!userKickOptional.isPresent) {
                    throwableList.addPropertyException("userKick", "The user you want to kick was not found.")
                }

                throwableList.ifEmpty {
                    val groupMemberKickOptional = groupMemberRepository.findGroupMemberByUserAndGroup(userKickOptional.get(), groupOptional.get())

                    if (!groupMemberKickOptional.isPresent) {
                        throwableList.addPropertyException("userKick", "The user you want to kick is not in this group.")
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
                throwableList.addGenericException("Unable to kick user from this group.")
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
            throwableList.addPropertyException("group", "This group was not found.")
        }

        if (!groupMemberRepository.findGroupMemberByUserAndGroup(user, groupOptional.get()).isPresent) {
            throwableList.addPropertyException("user", "You have to be in this group before you can invite users.")
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("Unable to search for user.")
            throw throwableList
        }

        val alreadyInvitedUsers = groupInviteRepository.findGroupInvitesByGroup(groupOptional.get()).map { it.id }
        return userRepository.findAll()
                .filter { it.username.contains(userName) }
                .filter { !groupMemberRepository.findGroupMemberByUserAndGroup(it, groupOptional.get()).isPresent }
                .filter { !alreadyInvitedUsers.contains(it.id) }
    }
}