package com.ordy.app

import android.app.Application
import com.ordy.app.api.RepositoryProvider
import com.ordy.app.ui.groups.GroupsViewModel
import com.ordy.app.ui.groups.invite.InviteMemberViewModel
import com.ordy.app.ui.groups.overview.OverviewGroupViewModel
import com.ordy.app.ui.locations.LocationsViewModel
import com.ordy.app.ui.login.LoginViewModel
import com.ordy.app.ui.orders.OrdersViewModel
import com.ordy.app.ui.orders.create.CreateOrderViewModel
import com.ordy.app.ui.orders.create.location.CreateOrderLocationViewModel
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.ui.orders.overview.additem.AddItemOrderViewModel
import com.ordy.app.ui.payments.PaymentsViewModel
import com.ordy.app.ui.profile.ProfileViewModel
import com.ordy.app.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {

    private val appModule = module {

        // Create the repository
        single { RepositoryProvider().create(androidContext()) }

        // Register the viewmodels
        viewModel {
            LoginViewModel(get())
        }

        viewModel {
            OrdersViewModel(get())
        }

        viewModel {
            OverviewOrderViewModel(get())
        }

        viewModel {
            CreateOrderViewModel(get())
        }

        viewModel {
            CreateOrderLocationViewModel(get())
        }

        viewModel {
            AddItemOrderViewModel(get())
        }

        viewModel {
            GroupsViewModel(get())
        }

        viewModel {
            OverviewGroupViewModel(get())
        }

        viewModel {
            InviteMemberViewModel(get())
        }

        viewModel {
            ProfileViewModel(get())
        }

        viewModel {
            SettingsViewModel(get())
        }

        viewModel {
            LocationsViewModel(get())
        }

        viewModel {
            PaymentsViewModel(get())
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}