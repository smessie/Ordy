package com.ordy.app.ui.payments

import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel

class PaymentsViewModel(repository: Repository) : RepositoryViewModel(repository) {
    fun getDebtorsMLD() = repository.userDebtorsResult
    fun getDebtsMLD() = repository.userDebtsResult
}