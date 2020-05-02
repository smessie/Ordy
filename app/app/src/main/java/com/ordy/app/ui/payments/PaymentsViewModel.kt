package com.ordy.app.ui.payments

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.actions.PaymentUpdate
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class PaymentsViewModel(repository: Repository) : RepositoryViewModel(repository) {
    val debtorsSearch = MutableLiveData("")
    fun getDebtorsSearchMLD() = debtorsSearch

    // Debtors MLD
    fun getDebtorsMLD() = repository.userDebtorsResult

    // Refresh debtors
    fun refreshDebtors() = repository.refreshDebtors()

    // Mark As paid
    fun markAsPaid(
        liveData: MutableLiveData<Query<ResponseBody>>,
        orderId: Int,
        userId: Int
    ) {
        repository.updatePaid(
            liveData,
            orderId,
            userId,
            PaymentUpdate(true)
        )
    }

    // notify
    fun notifyDebtor(
        liveData: MutableLiveData<Query<ResponseBody>>,
        orderId: Int,
        userId: Int
    ) {
        repository.notifyDebtor(
            liveData, orderId, userId
        )
    }

    val debtsSearch = MutableLiveData("")
    fun getDebtsSearchMLD() = debtsSearch

    // Debts MLD
    fun getDebtsMLD() = repository.userDebtsResult

    // Refresh Debts
    fun refreshDebts() = repository.refreshDebts()
}