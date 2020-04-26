package com.ordy.app.ui.payments

import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel

class PaymentsViewModel(repository: Repository) : RepositoryViewModel(repository) {
    // Debtors MLD
    fun getDebtorsMLD() = repository.userDebtorsResult

    // Debtors List
    fun getDebtors() = getDebtorsMLD().value!!

    // Refresh debtors
    fun refreshDebtors() = repository.refreshDebtors()

    // Debts MLD
    fun getDebtsMLD() = repository.userDebtsResult

    // Debts List
    fun getDebts() = getDebtsMLD().value!!

    // Refresh Debts
    fun refreshDebts() = repository.refreshDebts()
}