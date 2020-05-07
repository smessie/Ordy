package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.GenericExceptionWrapper
import com.ordy.backend.exceptions.wrappers.ThrowableListWrapper
import org.springframework.http.HttpStatus

class GenericException(code: HttpStatus, override val message: String) : OrdyException(code) {
    override fun wrap(): GenericExceptionWrapper {
        return GenericExceptionWrapper(this)
    }

    override fun fullWrap(): ThrowableListWrapper {
        return ThrowableList()
                .also { it.addGenericException(message) }
                .also { it.code = code }.wrap()
    }
}