package com.ordy.backend.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ordy.backend.database.models.Image
import com.ordy.backend.database.repositories.ImageRepository
import com.ordy.backend.exceptions.ThrowableList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ImageService (@Autowired val imageRepository: ImageRepository){

    /*
     * function to save an imageFile
     */

    fun saveImage(file: MultipartFile): Int {
        // TODO
        return 0
    }


    /*
     * function to get image from id: Int
     */

    fun getImage(id: Int): Image {
        val image = imageRepository.findById(id);
        // TODO
        return image.get()
    }
}