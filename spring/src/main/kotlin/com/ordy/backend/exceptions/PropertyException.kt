package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.PropertyExceptionWrapper
import com.ordy.backend.exceptions.wrappers.ThrowableListWrapper
import org.springframework.http.HttpStatus

class PropertyException(code: HttpStatus, val field: String, override val message: String) : OrdyException(code) {
    override fun wrap(): PropertyExceptionWrapper {
        return PropertyExceptionWrapper(this)
    }

    override fun fullWrap(): ThrowableListWrapper {
        return ThrowableList()
                .also { it.addPropertyException(field, message) }
                .also { it.code = code }.wrap()
    }
}