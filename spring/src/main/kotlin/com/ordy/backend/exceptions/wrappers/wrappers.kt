package com.ordy.backend.exceptions.wrappers

import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.PropertyException
import com.ordy.backend.exceptions.ThrowableList

interface ExceptionWrapper

class GenericExceptionWrapper(ex: GenericException) : ExceptionWrapper {
    val message = ex.message
}

class PropertyExceptionWrapper(ex: PropertyException) : ExceptionWrapper {
    val field = ex.field
    val message = ex.message
}

class ThrowableListWrapper(l: ThrowableList) : ExceptionWrapper {
    val inputErrors: List<PropertyExceptionWrapper> = l.inputErrors.map { it.wrap() }
    val generalErrors: List<GenericExceptionWrapper> = l.generalErrors.map { it.wrap() }
}