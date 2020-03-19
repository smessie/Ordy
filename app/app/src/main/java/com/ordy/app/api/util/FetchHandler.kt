package com.ordy.app.api.util

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.models.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FetchHandler {

    companion object {

        /**
         * Handle the execution of a RetroFit RX Observable.
         * @param observable RetroFit RX Observable
         * @return Wrapped Query object.
         */
        fun <T> handleLive(observable: Observable<T>): MutableLiveData<Query<T>> {
            val query = Query<T>()

            // Set the query in loading state.
            query.status = QueryStatus.LOADING

            val liveData = MutableLiveData(query)

            val fetch = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        query.data = result

                        // Set the query in success state.
                        query.status = QueryStatus.SUCCESS

                        // Update the livedata.
                        liveData.postValue(query)
                    },

                    { error ->
                        query.error = ErrorHandler.handle(error)

                        // Set the query in error state.
                        query.status = QueryStatus.ERROR

                        // Update the livedata.
                        liveData.postValue(query)
                    }
                )

            return liveData
        }

        fun <T> handle(observable: Observable<T>, handler: QueryHandler<T>) {

            val query = Query<T>()

            // Set the query in loading state.
            query.status = QueryStatus.LOADING

            val fetch = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        query.data = result

                        // Set the query in success state.
                        query.status = QueryStatus.SUCCESS

                        // Execute the handler.
                        handler.onQuerySuccess(result)
                    },

                    { error ->
                        val errorObject = ErrorHandler.handle(error)

                        query.error = errorObject

                        // Set the query in error state.
                        query.status = QueryStatus.ERROR

                        // Execute the handler.
                        handler.onQueryError(errorObject)
                    }
                )
        }
    }
}