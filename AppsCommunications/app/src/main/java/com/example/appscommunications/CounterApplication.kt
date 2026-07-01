package com.example.appscommunications

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.appscommunications.appfunctions.CounterFunctions
import com.example.appscommunications.ui.CounterViewModel

class CounterApplication : Application(), ViewModelStoreOwner, AppFunctionConfiguration.Provider {

    override val viewModelStore = ViewModelStore()

    val counterViewModel: CounterViewModel by lazy {
        ViewModelProvider(this)[CounterViewModel::class.java]
    }

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() = AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(CounterFunctions::class.java) {
                CounterFunctions(counterViewModel)
            }
            .build()
}