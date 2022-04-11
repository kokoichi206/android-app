package jp.mydns.kokoichi0206.stockmarket.data.repository

import jp.mydns.kokoichi0206.stockmarket.data.csv.CSVParser
import jp.mydns.kokoichi0206.stockmarket.data.local.StockDatabase
import jp.mydns.kokoichi0206.stockmarket.data.mapper.toCompanyListing
import jp.mydns.kokoichi0206.stockmarket.data.remote.StockAPI
import jp.mydns.kokoichi0206.stockmarket.domain.model.CompanyListing
import jp.mydns.kokoichi0206.stockmarket.domain.repository.StockRepository
import jp.mydns.kokoichi0206.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockAPI,
    val db: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListing>,
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListing() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
//                emit(Resource.Loading(false))
            }
        }
    }
}