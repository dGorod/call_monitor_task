package com.dgorod.callmonitor

import android.app.Application
import com.dgorod.callmonitor.data.CallsQueryStorage
import com.dgorod.callmonitor.data.repository.CallsRepository
import com.dgorod.callmonitor.data.repository.CallsRepositoryImpl
import com.dgorod.callmonitor.data.repository.ServerRepository
import com.dgorod.callmonitor.data.repository.ServerRepositoryImpl
import com.dgorod.callmonitor.data.service.CallMonitorServer
import com.dgorod.callmonitor.domain.CallsInteractor
import com.dgorod.callmonitor.domain.ServerInteractor
import com.dgorod.callmonitor.ui.viewmodel.MainActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication: Application() {

    private val dataModule = module {
        single { CallsQueryStorage }
        single { CallMonitorServer(get(), get()) }
        single<CallsRepository> { CallsRepositoryImpl(androidContext()) }
        single<ServerRepository> { ServerRepositoryImpl(get()) }
    }

    private val domainModule = module {
        single { CallsInteractor(androidContext(), get()) }
        single { ServerInteractor(androidContext(), get()) }
    }

    private val appModule = module {
        viewModel { MainActivityViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(dataModule, domainModule, appModule)
        }
    }
}