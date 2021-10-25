package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.CoinDetailDto
import com.example.myapplication.data.remote.dto.CoinDto

interface CoinRepository {

    suspend fun getCoins(): List<CoinDto>

    suspend fun getCoinById(coinId: String): CoinDetailDto
}