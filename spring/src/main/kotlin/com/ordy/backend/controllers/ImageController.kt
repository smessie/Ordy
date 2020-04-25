package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.User
import com.ordy.backend.services.GroupService
import com.ordy.backend.services.ImageService
import com.ordy.backend.wrappers.GroupCreateWrapper
import com.ordy.backend.wrappers.GroupWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/images")
class ImageController(var imageService: ImageService){

    @GetMapping("/{id}")
    fun getImage(@PathVariable id:Int) {
        // TODO
    }
}