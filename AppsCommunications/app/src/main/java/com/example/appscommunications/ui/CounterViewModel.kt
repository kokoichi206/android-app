package com.example.appscommunications.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

class CounterViewModel : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment(by: Int = 1): Int = _count.updateAndGet { it + by }

    fun decrement(by: Int = 1): Int = _count.updateAndGet { it - by }

    fun reset(): Int = _count.updateAndGet { 0 }

    fun current(): Int = _count.value
}