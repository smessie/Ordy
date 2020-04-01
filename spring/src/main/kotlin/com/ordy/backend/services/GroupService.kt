package com.ordy.backend.services

import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.GroupMember
import com.ordy.backend.database.models.GroupInvite
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.GroupRepository
import com.ordy.backend.database.repositories.GroupInviteRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.database.repositories.GroupMemberRepository
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.wrappers.GroupCreateWrapper
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
        checkGroupName(groupWrapper.name, throwableList)
        throwableList.ifNotEmpty { throw throwableList }

        var creator = userRepository.findById(userId).get()
        val group = Group(name = groupWrapper.name, creator = creator)
        groupRepository.save(group)

        // the creator is automatically added to the members of the group he just created
        val groupMember = GroupMember(user = creator, group = group)
        groupMemberRepository.save(groupMember)

        return group
    }

    fun updateGroup(groupId: Int, groupWrapper: GroupCreateWrapper): Group {
        val throwableList = ThrowableList()
        checkGroupName(groupWrapper.name, throwableList)

        val group: Optional<Group> = groupRepository.findById(groupId)

        if(group.isPresent()) {
            group.get().name = groupWrapper.name
            groupRepository.save(group.get())
        } else {
            throwableList.addGenericException("Group with id $groupId not found")
        }

        throwableList.ifNotEmpty { throw throwableList }

        return group.get()
    }

    /**
     * get groups of a specific user
     */

    fun getGroups(userId: Int) : List<Group> {
        var user = userRepository.findById(userId).get()
        var groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }

        return groups
    }

    /**
     *  the logged in user can invite others users in groups where he is part of
     */

    fun createInvite(groupId: Int,userInviteId: Int, userId: Int) {
        val throwableList = ThrowableList()

        var invitedUser = userRepository.findById(userInviteId)
        var group = groupRepository.findById(groupId)

        if (!invitedUser.isPresent){
            throw throwableList.also{it.addGenericException("The person you want to invite is not found!")}
        }

        if (!group.isPresent){
            throw throwableList.also{it.addGenericException("The group with the given id does not exist!")}
        }

        if (!groupMemberRepository.findGroupMembersByUser(invitedUser.get()).filter {  it.group.id == groupId }.isEmpty()){
            throw throwableList.also{it.addGenericException("The user you want to invite is already in this group")}
        }

        // The user who sends the invite has to be in the group
        var user = userRepository.findById(userId).get()
        if (groupMemberRepository.findGroupMembersByUser(user).filter {  it.group.id == groupId }.isEmpty()){
            throw throwableList.also{it.addGenericException("You have to be in this group before you can invite other users!")}
        }

        var inviteOptional = groupInviteRepository.findGroupInvitesByUserAndGroup(invitedUser.get(), group.get())
        if(!inviteOptional.isEmpty()){
            throw throwableList.also{it.addGenericException("This person is already invited to this group!")}
        }

        val invite = GroupInvite(user = invitedUser.get(), group = group.get())
        groupInviteRepository.save(invite)
    }

    fun deleteInvite(groupId: Int, userId: Int) {
        //TODO
    }

    /**
     *  this is called when you want to leave a group
     */

    fun leaveGroup(groupId: Int, userId: Int) {
        val throwableList = ThrowableList()

        var user = userRepository.findById(userId).get()
        var groupMember = groupMemberRepository.findGroupMembersByUser(user).filter { it.group.id == groupId }

        if (groupMember.isEmpty()){
            throw throwableList.also{it.addGenericException("You are not in this group!")}
        }

        if (groupMember.size > 1){
            throw throwableList.also{it.addGenericException("Strange behaviour: you are added more than 1 times in this group?")}
        }

        groupMemberRepository.delete(groupMember.first())
    }

    /**
     *  this is called when the person who has logged in, wants to kick another member
     */

    fun deleteMember(groupId: Int, userKickId: Int, userId: Int) {
        val throwableList = ThrowableList()

        var user = userRepository.findById(userId).get()
        var groupMemberLogin = groupMemberRepository.findGroupMembersByUser(user).filter { it.group.id == groupId }

        if (groupMemberLogin.isEmpty()){
            throw throwableList.also{it.addGenericException("You have to be in this group to kick someone!")}
        }

        var userKick = userRepository.findById(userKickId)

        if (!userKick.isPresent){
            throw throwableList.also{it.addGenericException("The person you want to kick is not found!")}
        }

        var groupMemberKick = groupMemberRepository.findGroupMembersByUser(userKick.get()).filter { it.group.id == groupId }

        if (groupMemberLogin.isEmpty()){
            throw throwableList.also{it.addGenericException("The person you want to kick is not in this group!")}
        }

        if (groupMemberKick.size > 1 || groupMemberLogin.size > 1) {
            throw throwableList.also{it.addGenericException("Strange behaviour: someone is added more than 1 times in this group?")}
        }

        groupMemberRepository.delete(groupMemberKick.first())
    }

}