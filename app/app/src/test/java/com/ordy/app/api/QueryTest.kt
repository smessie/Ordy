package com.ordy.app.api

import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test


class QueryTest {

    @Test
    fun `Query initial status must be INITIALIZED`() {
        val query: Query<String> = Query()

        Assert.assertEquals(query.status, QueryStatus.INITIALIZED)
    }

    @Test
    fun `requireData must throw an error when data is null`() {
        val query: Query<String> = Query()
        query.data = null

        try {
            query.requireData()

            fail("Expected an IllegalStateException to be thrown")
        } catch (exception: IllegalStateException) {
            Assert.assertEquals("Data is not present on Query object", exception.message)
        }
    }

    @Test
    fun `requireError must throw an error when error is null`() {
        val query: Query<String> = Query()
        query.error = null

        try {
            query.requireError()

            fail("Expected an IllegalStateException to be thrown")
        } catch (exception: IllegalStateException) {
            Assert.assertEquals("Error is not present on Query object", exception.message)
        }
    }
}