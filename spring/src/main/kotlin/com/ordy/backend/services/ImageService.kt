package com.ordy.backend.services

import com.ordy.backend.database.models.Image
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.repositories.ImageRepository
import com.ordy.backend.exceptions.ThrowableList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest

@Service
class ImageService(val imageRepository: ImageRepository) {

    /**
     * function to save an image
     */

    @Transactional
    fun saveImage(file: MultipartFile, order: Order?): Image {
        val byteObjects = Array<Byte>(file.size.toInt()) { 0 }
        var i = 0
        for (byte in file.bytes) {
            byteObjects[i++] = byte
        }
        return imageRepository.saveAndFlush(Image(image = byteObjects, order = order))
    }


    /**
     * function to get image from id: Int
     */

    @Transactional
    fun getImage(imageId: Int, request: HttpServletRequest): Image {
        val throwableList = ThrowableList()

        val imageOptional = imageRepository.findById(imageId)
        if (imageOptional.isPresent) {
            return imageOptional.get()
        } else {
            throw throwableList.also { it.addGenericException("Image of bill was not found.") }
        }
    }

    /**
     *  delete a bill image
     */

    @Transactional
    fun deleteImage(imageId: Int) {
        val throwableList = ThrowableList()

        val imageOptional = imageRepository.findById(imageId)
        if (imageOptional.isPresent) {
            imageRepository.delete(imageOptional.get())
        } else {
            throw throwableList.also { it.addGenericException("Failed to delete the bill image.") }
        }
    }
}