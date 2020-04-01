package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.ThrowableListWrapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ThrowableList::class)
    fun handleException(e: ThrowableList): ResponseEntity<ThrowableListWrapper> {
        return ResponseEntity
                . status(e.code)
                . body(e.wrap())
    }

    @ExceptionHandler(GenericException::class)
    fun handleException(e: GenericException) : ResponseEntity<ThrowableListWrapper> {
        return ResponseEntity
                . status(e.code)
                . body(e.fullWrap())
    }

    @ExceptionHandler(PropertyException::class)
    fun handleException(e: PropertyException) : ResponseEntity<ThrowableListWrapper> {
        val ec = e.fullWrap()
        return ResponseEntity
                . status(e.code)
                . body(e.fullWrap())
    }
}