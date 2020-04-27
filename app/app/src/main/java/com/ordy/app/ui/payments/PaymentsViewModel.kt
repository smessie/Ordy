package com.ordy.app.ui.payments

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.actions.PaymentUpdate
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class PaymentsViewModel(repository: Repository) : RepositoryViewModel(repository) {
    // Debtors MLD
    fun getDebtorsMLD() = repository.userDebtorsResult

    // Debtors List
    fun getDebtors() = getDebtorsMLD().value!!

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

    // Debts MLD
    fun getDebtsMLD() = repository.userDebtsResult

    // Debts List
    fun getDebts() = getDebtsMLD().value!!

    // Refresh Debts
    fun refreshDebts() = repository.refreshDebts()
}