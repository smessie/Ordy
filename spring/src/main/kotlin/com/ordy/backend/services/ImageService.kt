package com.ordy.backend.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ordy.backend.database.models.Image
import com.ordy.backend.database.repositories.ImageRepository
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.ThrowableList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.lang.Exception
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ImageService (@Autowired val imageRepository: ImageRepository){

    /*
     * function to save an imageFile
     */

    @Transactional
    fun saveImage(file: MultipartFile): Int {
        val byteObjects = Array<Byte>(file.size.toInt()) {0}
        var i = 0
        for (byte in file.bytes){
            byteObjects[i++] = byte
        }
        return imageRepository.saveAndFlush(Image(image= byteObjects)).id
    }


    /*
     * function to get image from id: Int
     */

    @Transactional
    fun getImage(id: Int, request: HttpServletRequest): Image {
        val throwableList = ThrowableList()

        val image = imageRepository.findById(id)
        when {
            image.isPresent -> {
                return image.get()
            }
            else -> {
                throw throwableList.also { it.addGenericException("Image with given ID = $id was not found.") }
            }
        }
    }
}