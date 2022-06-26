package jp.mydns.kokoichi0206.gmail.model

data class MailData(
    val id: Int,
    val userName: String,
    val subject: String,
    val body: String,
    val timeStamp: String = "",
)
