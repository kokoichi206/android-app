package jp.mydns.kokoichi0206.stockmarket.data.mapper

import jp.mydns.kokoichi0206.stockmarket.data.local.CompanyListingEntity
import jp.mydns.kokoichi0206.stockmarket.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange,
    )
}

fun CompanyListing.toCompanyListing(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange,
    )
}
