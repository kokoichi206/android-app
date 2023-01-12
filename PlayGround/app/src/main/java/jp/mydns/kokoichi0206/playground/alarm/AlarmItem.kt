package jp.mydns.kokoichi0206.playground.alarm

import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val message: String,
)
