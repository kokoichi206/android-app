package jp.mydns.kokoichi0206.stockmarket.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.mydns.kokoichi0206.stockmarket.data.csv.CSVParser
import jp.mydns.kokoichi0206.stockmarket.data.csv.CompanyListingsParser
import jp.mydns.kokoichi0206.stockmarket.data.repository.StockRepositoryImpl
import jp.mydns.kokoichi0206.stockmarket.domain.model.CompanyListing
import jp.mydns.kokoichi0206.stockmarket.domain.repository.StockRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}