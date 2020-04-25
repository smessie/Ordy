package com.ordy.backend.controllers

import com.ordy.backend.services.ImageService
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/images")
@ResponseStatus(HttpStatus.OK)
class ImageController(@Autowired val imageService: ImageService) {

    @GetMapping("/{id}")
    fun getImageTest(@PathVariable id: Int, request: HttpServletRequest, response: HttpServletResponse) {
        val image = imageService.getImage(id, request = request)
        val byteArray = ByteArray(image.image.size)
        var i = 0

        for (wrappedByte in image.image) {
            byteArray[i++] = wrappedByte
        }

        val inputStream: InputStream = ByteArrayInputStream(byteArray)
        IOUtils.copy(inputStream, response.outputStream)
    }
}