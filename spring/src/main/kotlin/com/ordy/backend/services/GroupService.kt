package com.ordy.backend.services

import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.GroupRepository
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.PropertyException
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.wrappers.GroupCreateWrapper
import com.ordy.backend.wrappers.GroupIdWrapper
import com.ordy.backend.wrappers.GroupWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupService(@Autowired val groupRepository: GroupRepository) {

    private val groupNameRegex = Regex("^[A-z0-9 ]+$")

    private fun checkGroupName(name: String, list: ThrowableList) {
        if (!groupNameRegex.matches(name)) {
            list.addException(PropertyException(HttpStatus.UNPROCESSABLE_ENTITY, "name", "Name should only contain letters, numbers an spaces"))
        }
    }

    fun createGroup(user: User, groupWrapper: GroupCreateWrapper) : GroupIdWrapper {
        val throwableList = ThrowableList()
        checkGroupName(groupWrapper.name, throwableList)
        throwableList.ifNotEmpty { throw throwableList }

        val group = Group(name = groupWrapper.name, creator = user)
        groupRepository.save(group)
        return GroupIdWrapper(group.id)
    }

    fun updateGroup(groupId: Int, groupWrapper: GroupCreateWrapper) : GroupWrapper {
        val throwableList = ThrowableList()
        checkGroupName(groupWrapper.name, throwableList)

        val group: Optional<Group> = groupRepository.findById(groupId)
        group.ifPresentOrElse(
                {
                    group.get().name = groupWrapper.name
                    groupRepository.save(group.get())
                },
                {throwableList.addException(GenericException(HttpStatus.NOT_FOUND, "Group with id $groupId not found"))})

        throwableList.ifNotEmpty { throw throwableList }

        return GroupWrapper(group.get().id, group.get().name)
    }

    fun createInvite(groupId: Int, userId: Int) {
        //TODO
    }

    fun deleteInvite(groupId: Int, userId: Int) {
        //TODO
    }

    fun deleteMember(groupId: Int, userId: Int) {
        //TODO
    }
}