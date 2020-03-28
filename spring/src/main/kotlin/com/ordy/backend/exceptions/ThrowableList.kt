package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.ExceptionWrapper
import com.ordy.backend.exceptions.wrappers.ThrowableListWrapper
import org.springframework.http.HttpStatus
import java.util.*

class ThrowableList(code: HttpStatus = HttpStatus.NOT_FOUND) : OrdyException(code) {
    var inputErrors: LinkedList<PropertyException> = LinkedList()
        private set
    var generalErrors: LinkedList<GenericException> = LinkedList()
        private set

    fun addException(ex: PropertyException) {
        inputErrors.add(ex)
    }

    fun addException(ex: GenericException) {
        generalErrors.add(ex)
    }

    fun ifEmpty(f: () -> Unit) {
        if (inputErrors.isEmpty() and generalErrors.isEmpty()) {
            f.invoke()
        }
    }

    fun ifNotEmpty(f: () -> Unit) {
        if (inputErrors.isEmpty().not() or generalErrors.isEmpty().not()) {
            f.invoke()
        }
    }

    override fun wrap(): ThrowableListWrapper {
        return ThrowableListWrapper(this)
    }
}