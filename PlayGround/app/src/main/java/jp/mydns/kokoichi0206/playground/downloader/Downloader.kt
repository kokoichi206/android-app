package jp.mydns.kokoichi0206.playground.downloader

interface Downloader {

    fun downloadFile(url: String): Long
}
