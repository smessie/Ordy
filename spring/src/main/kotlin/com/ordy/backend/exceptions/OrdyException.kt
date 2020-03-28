package com.ordy.backend.exceptions

import com.ordy.backend.exceptions.wrappers.ExceptionWrapper
import org.springframework.http.HttpStatus

abstract class OrdyException(val code: HttpStatus) : Throwable() {
    abstract fun wrap() : ExceptionWrapper
}