package com.ordy.app.api.util

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FetchHandler {

    companion object {

        /**
         * Handle the execution of a RetroFit RX Observable.
         * @param observable RetroFit RX Observable
         * @return Wrapped Query object.
         */
        fun <T> handleLive(observable: Observable<T>): MutableLiveData<Query<T>> {
            return handle(MutableLiveData(Query(QueryStatus.LOADING)), observable)
        }

        fun <T> handle(mutableLiveData: MutableLiveData<Query<T>>, observable: Observable<T>): MutableLiveData<Query<T>> {

            val query = Query<T>()

            // Set the query in loading state.
            query.status = QueryStatus.LOADING

            // Update the livedata.
            mutableLiveData.postValue(query)

            val fetch = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        query.data = result

                        // Set the query in success state.
                        query.status = QueryStatus.SUCCESS

                        // Update the livedata.
                        mutableLiveData.postValue(query)
                    },

                    { error ->
                        val errorObject = ErrorHandler.parse(error)

                        query.error = errorObject

                        // Set the query in error state.
                        query.status = QueryStatus.ERROR

                        // Execute the handler.
                        mutableLiveData.postValue(query)
                    }
                )

            return mutableLiveData
        }
    }
}