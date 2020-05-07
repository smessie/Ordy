package com.ordy.backend.services

import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired val userRepository: UserRepository) {

    /**
     * Return the info of the current logged in user
     */
    fun getUserInfo(userId: Int): User {
        return userRepository.findById(userId).get()
    }
}