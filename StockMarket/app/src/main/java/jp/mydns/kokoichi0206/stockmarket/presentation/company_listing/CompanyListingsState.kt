package jp.mydns.kokoichi0206.stockmarket.presentation.company_listing

import jp.mydns.kokoichi0206.stockmarket.domain.model.CompanyListing

data class CompanyListingsState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
)
