package jp.mydns.kokoichi0206.newsapp

import android.icu.text.SimpleDateFormat
import java.util.*

object MockData {
    val topNewsList = listOf<NewsData>(
        NewsData(
            1,
            author = "hoge fuga",
            title = "This is my first post - About my history",
            description = "This is my first post - About my history",
            publishedAt = "2022-01-01T03:32:11Z",
        ),
        NewsData(
            5,
            author = "hoge fuga",
            title = "This is my 5th post - About my history",
            description = "This is my first post - About my history",
            publishedAt = "2022-01-01T03:32:11Z",
        ),
        NewsData(
            11,
            author = "hoge fuga",
            title = "This is my 11th post - About my history",
            description = "This is my first post - About my history",
            publishedAt = "2022-01-01T03:32:11Z",
        ),
        NewsData(
            12,
            author = "hoge fuga",
            title = "This is my 12 post - About my history",
            description = "This is my first post - About my history",
            publishedAt = "2022-01-01T03:32:11Z",
        ),
        NewsData(
            14,
            author = "hoge fuga",
            title = "This is my 14th post - About my history",
            description = "This is my first post - About my history",
            publishedAt = "2022-01-01T03:32:11Z",
        ),
        NewsData(
            2,
            author = "hoge fuga",
            title = "This is my second post - About my history",
            description = "This is my first post - About my history",
            publishedAt = "2022-01-01T03:32:11Z",
        ),
    )

    fun getNewsById(newsId: Int?): NewsData {
        return topNewsList.first { it.id == newsId }
    }

    fun Date.getTimeAgo(): String {
        val calendar = Calendar.getInstance()
        calendar.time = this

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)

        return if (year < currentYear) {
            val interval = currentYear - year
            if (interval == 1) "$interval year ago" else "$interval years ago"
        } else if (month < currentMonth) {
            val interval = currentMonth - month
            if (interval == 1) "$interval month ago" else "$interval months ago"
        } else if (day < currentDay) {
            val interval = currentDay - day
            if (interval == 1) "$interval day ago" else "$interval days ago"
        } else if (hour < currentHour) {
            val interval = currentDay - day
            if (interval == 1) "$interval hour ago" else "$interval hours ago"
        } else {
            "A moment ago"
        }
    }

    fun stringToDate(publishedAt: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssxx", Locale.ENGLISH)
            .parse(publishedAt) as Date
    }
}