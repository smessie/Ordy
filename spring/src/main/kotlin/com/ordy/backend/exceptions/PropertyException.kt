package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.PropertyExceptionWrapper
import org.springframework.http.HttpStatus

class PropertyException(code: HttpStatus, val field: String, override val message: String) : OrdyException(code) {
    override fun wrap(): PropertyExceptionWrapper {
        return PropertyExceptionWrapper(this)
    }
}