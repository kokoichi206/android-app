package io.kokoichi.sample.mastodonclient

data class UserCredential (
    val instanceUrl: String,
    var username: String? = null,
    var accessToken: String? = null
)
