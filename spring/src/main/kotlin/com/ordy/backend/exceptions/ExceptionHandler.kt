package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.ThrowableListWrapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ThrowableList::class)
    fun handleException(e: ThrowableList): ResponseEntity<ThrowableListWrapper> {
        return ResponseEntity
                . status(e.code)
                . body(e.wrap())
    }

    @ExceptionHandler(GenericException::class)
    fun handleException(e: GenericException) : ResponseEntity<ThrowableListWrapper> {
        val ec = ThrowableList().also { it.addGenericException(e.message) }
        return ResponseEntity
                . status(e.code)
                . body(ec.wrap())
    }

    @ExceptionHandler(PropertyException::class)
    fun handleException(e: PropertyException) : ResponseEntity<ThrowableListWrapper> {
        val ec = ThrowableList().also { it.addPropertyException(e.field, e.message) }
        return ResponseEntity
                . status(e.code)
                . body(ec.wrap())
    }
}