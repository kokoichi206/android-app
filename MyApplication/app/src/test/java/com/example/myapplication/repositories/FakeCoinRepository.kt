package com.example.myapplication.repositories

import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.remote.dto.CoinDto

class FakeCoinRepository {

    // We don't wanna do real API access when we do the tests
    // cause those take time. Simulate the behaviour !

    private val coinLists = mutableListOf<CoinDto>()

    private val getCoins = MutableLiveData<List<CoinDto>>(coinLists)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }
}