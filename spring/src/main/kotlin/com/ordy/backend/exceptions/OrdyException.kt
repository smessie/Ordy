package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.ExceptionWrapper
import org.springframework.http.HttpStatus

abstract class OrdyException(var code: HttpStatus) : Exception() {
    abstract fun wrap(): ExceptionWrapper
    abstract fun fullWrap(): ExceptionWrapper
}