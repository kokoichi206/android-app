package jp.mydns.kokoichi0206.kmmtest

import jp.mydns.kokoichi0206.kmmtest.cache.Database
import jp.mydns.kokoichi0206.kmmtest.cache.DatabaseDriverFactory
import jp.mydns.kokoichi0206.kmmtest.entity.RocketLaunch
import jp.mydns.kokoichi0206.kmmtest.network.SpaceXApi

class SpaceXSDK(databaseDriverFactory: DatabaseDriverFactory) {

    private val database = Database(databaseDriverFactory)
    private val api = SpaceXApi()

    // To handle exceptions produced by the Ktor client, in Swift, we need to mark our function with @Throws annotation.
    // All Kotlin exceptions are unchecked, while Swift has only checked errors.
    @Throws(Exception::class)
    suspend fun getLaunches(forceReload: Boolean): List<RocketLaunch> {
        val cachedLaunches = database.getAllLaunches()
        // If there is no cached data, it will load information from the internet
        // independently of forceReload flag value
        return if (cachedLaunches.isNotEmpty() && !forceReload) {
            cachedLaunches
        } else {
            api.getAllLaunches().also {
                database.clearDatabase()
                database.createLaunches(it)
            }
        }
    }
}