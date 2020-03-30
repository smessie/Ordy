package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.GenericExceptionWrapper
import org.springframework.http.HttpStatus

class GenericException(code: HttpStatus, override val message: String) : OrdyException(code) {
    override fun wrap(): GenericExceptionWrapper {
        return GenericExceptionWrapper(this)
    }
}