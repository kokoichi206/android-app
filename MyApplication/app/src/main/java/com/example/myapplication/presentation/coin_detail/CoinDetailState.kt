package com.example.myapplication.presentation.coin_detail

import com.example.myapplication.domain.model.Coin
import com.example.myapplication.domain.model.CoinDetail

data class CoinDetailState(
    val isLoading: Boolean = false,
    val coin: CoinDetail? = null,
    val error: String = ""
)
