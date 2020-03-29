package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.ExceptionWrapper
import org.springframework.http.HttpStatus

abstract class OrdyException(var code: HttpStatus) : Throwable() {
    abstract fun wrap() : ExceptionWrapper
}