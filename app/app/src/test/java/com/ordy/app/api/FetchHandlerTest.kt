package com.ordy.app.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.api.util.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.spy


class FetchHandlerTest {

    var faker = Faker()

    @Before
    fun setup() {

        // Change the RX Java thread scheduler to trampoline instead of Android
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Test
    fun `'handle' should change the status of the query to LOADING`() {
        val observable = Observable.just(faker.name().name())
        val mutableLiveData: MutableLiveData<Query<String>> = MutableLiveData(Query())

        // Handle the request.
        val queryMLD = FetchHandler.handle(mutableLiveData, observable)
        val query = queryMLD.value!!

        Assert.assertEquals(QueryStatus.LOADING, query.status)
    }

    @Test
    fun `'handle' should update the data & success status when the observable succeeds`() {
        val data = faker.name().name()

        val observable = Observable.just(data)
        val mutableLiveData: MutableLiveData<Query<String>> = MutableLiveData(Query())

        // Handle the request.
        val queryMLD = FetchHandler.handle(mutableLiveData, observable)
        val query = queryMLD.value!!

        //  Wait until the observable has succeeded
        observable.blockingSubscribe()

        Assert.assertEquals(QueryStatus.SUCCESS, query.status)
        Assert.assertEquals(data, query.data)
    }

    @Test
    fun `'handle' should update the error & error status when the observable fails`() {
        val errorMessage = faker.name().name()
        val error = Throwable(errorMessage)

        val queryError = QueryError()
        queryError.message = errorMessage

        val observable: Observable<String> = Observable.error(error)
        val mutableLiveData: MutableLiveData<Query<String>> = MutableLiveData(Query())

        // Handle the request.
        val queryMLD = FetchHandler.handle(mutableLiveData, observable)
        val query = queryMLD.value!!

        // Mock the ErrorHandler
        val errorHandler = spy(ErrorHandler::class.java)
        whenever(errorHandler.parse(error)).thenReturn(queryError)

        try {
            //  Wait until the observable has succeeded
            observable.blockingSubscribe()
        } catch (e: Exception) {}

        Assert.assertEquals(QueryStatus.ERROR, query.status)
        Assert.assertEquals(error.message, query.error?.message)
    }

    @Test
    fun `'handleLive' should return mutable live data with a status of LOADING`() {
        val observable = Observable.just(faker.name().name())

        // Handle the request.
        val queryMLD = FetchHandler.handleLive(observable)
        val query = queryMLD.value!!

        Assert.assertEquals(QueryStatus.LOADING, query.status)
    }
}