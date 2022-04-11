package jp.mydns.kokoichi0206.stockmarket.domain.repository

import jp.mydns.kokoichi0206.stockmarket.domain.model.CompanyListing
import jp.mydns.kokoichi0206.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<CompanyListing>>>
}