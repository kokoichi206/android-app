package jp.mydns.kokoichi0206.kmmnote.domain.time

import kotlinx.datetime.*

object DateTimeUtil {

    fun now(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun toEpochMills(dateTIme: LocalDateTime): Long {
        return dateTIme.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    fun formatNoteDate(dateTIme: LocalDateTime): String {
        val month = dateTIme.month.name.lowercase().take(3).replaceFirstChar { it.uppercase() }
        val day = if(dateTIme.dayOfMonth < 10) "0${dateTIme.dayOfMonth}" else dateTIme.dayOfMonth
        val year = dateTIme.year
        val hour = if(dateTIme.hour < 10) "0${dateTIme.hour}" else dateTIme.hour
        val minute = if(dateTIme.minute < 10) "0${dateTIme.minute}" else dateTIme.minute

        return buildString {
            append(month)
            append(" ")
            append(day)
            append(" ")
            append(year)
            append(", ")
            append(hour)
            append(":")
            append(minute)
        }
    }
}