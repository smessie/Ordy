package com.ordy.app.api

import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryGeneralError
import com.ordy.app.api.util.QueryInputError
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.mock
import retrofit2.HttpException
import retrofit2.Response

class ErrorHandlerTest {

    var faker = Faker()

    @Test
    fun `'parse' should use the error message when passed a Throwable`() {
        val errorMessage = faker.name().name()
        val error = Throwable(errorMessage)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals(errorMessage, queryError.message)
    }

    @Test
    fun `'parse' should use a generic message when passed a Throwable without message`() {
        val error = Throwable()

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals("An unknown error has occurred", queryError.message)
    }

    @Test
    fun `'parse' should use a custom error code when passed a Throwable`() {
        val error = Throwable()

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals("unknown", queryError.code)
    }

    @Test
    fun `'parse' should use a custom error description when passed a Throwable`() {
        val error = Throwable()

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals(
            "Something went wrong, please contact a developer.",
            queryError.description
        )
    }

    @Test
    fun `'parse' should use the error message when passed a HTTPException`() {
        val errorMessage = faker.name().name()

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn(errorMessage)

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals(errorMessage, queryError.message)
    }

    @Test
    fun `'parse' should use a placeholder message when passed a HTTPException without message`() {

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn("")

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals("An unknown error has occurred", queryError.message)
    }

    @Test
    fun `'parse' should use the error code when passed a HTTPException`() {
        val errorCode = 418

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn(faker.name().name())
        whenever(response.code()).thenReturn(errorCode)

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals(errorCode.toString(), queryError.code)
    }

    @Test
    fun `'parse' should use the response when passed a HTTPException`() {
        val errorCode = 418

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn(faker.name().name())

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals(error.response(), queryError.response)
    }

    @Test
    fun `'parse' should use the generalErrors when present when passed HTTPException`() {
        val generalErrorMessage = faker.name().name()
        val errorJson = """{ generalErrors: [ { "message": "$generalErrorMessage" } ] }"""

        // Mock a response body
        val responseBody = errorJson.toResponseBody()

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn(faker.name().name())
        whenever(response.errorBody()).thenReturn(responseBody)

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertFalse(queryError.generalErrors.isEmpty())
        Assert.assertEquals(generalErrorMessage, queryError.generalErrors.first().message)
    }

    @Test
    fun `'parse' should use an empty list for generalErrors when not present when passed HTTPException`() {
        val errorJson = """{}"""

        // Mock a response body
        val responseBody = errorJson.toResponseBody()

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn(faker.name().name())
        whenever(response.errorBody()).thenReturn(responseBody)

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals(emptyList<QueryGeneralError>(), queryError.generalErrors)
    }

    @Test
    fun `'parse' should use the inputErrors when present when passed HTTPException`() {
        val inputErrorMessage = faker.name().name()
        val errorJson = """{ inputErrors: [ { "message": "$inputErrorMessage" } ] }"""

        // Mock a response body
        val responseBody = errorJson.toResponseBody()

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn(faker.name().name())
        whenever(response.errorBody()).thenReturn(responseBody)

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertFalse(queryError.inputErrors.isEmpty())
        Assert.assertEquals(inputErrorMessage, queryError.inputErrors.first().message)
    }

    @Test
    fun `'parse' should use an empty list for inputErrors when not present when passed HTTPException`() {
        val errorJson = """{}"""

        // Mock a response body
        val responseBody = errorJson.toResponseBody()

        // Mock a response
        val response = mock(Response::class.java)
        whenever(response.message()).thenReturn(faker.name().name())
        whenever(response.errorBody()).thenReturn(responseBody)

        val error = HttpException(response)

        // Parse the error
        val queryError = ErrorHandler().parse(error)

        Assert.assertEquals(emptyList<QueryInputError>(), queryError.inputErrors)
    }
}