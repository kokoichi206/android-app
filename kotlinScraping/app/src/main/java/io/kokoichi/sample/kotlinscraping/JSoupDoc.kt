//package io.kokoichi.sample.kotlinscraping
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
//import org.jsoup.nodes.Element
//import java.util.*
//import java.util.function.Consumer
//import kotlin.streams.toList
//
//class JSoupDoc(private val document: Optional<Document>) {
//    companion object {
//        const val CONNECTION_TIMEOUT: Int = 150000
//        var errorHandler: Consumer<Throwable> = Consumer { }
//
//        @RequiresApi(Build.VERSION_CODES.N)
//        @JvmStatic
//        fun fromUrl(url: String): JSoupDoc {
//            val htmlDoc = kotlin.runCatching {
//                Jsoup.connect(url).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT).get()
//            }.fold(
//                onSuccess = { Optional.ofNullable(it) },
//                onFailure = {
//                    errorHandler.accept(it)
//                    Optional.empty()
//                }
//            )
//            return JSoupDoc(htmlDoc)
//        }
//    }
//
//    val isEmpty = document.isEmpty()
//    val isPresent = document.isPresent
//    val body: Optional<Element> = document.map { it.body() }
//
//    fun listElement(query: String): List<Element> {
//        return document.map { it.select(query).stream().toList() }.orElse(listOf())
//    }
//}
//
//fun Element.selectFirstOpt(query: String): Optional<Element> {
//    return Optional.ofNullable(this.select(query).first())
//}
//
//fun Element.textWithout(text: String): String = this.text().replace(text, "")
//
//fun Element.href(): String = this.attr("href")
//
//fun Element.absHref(): String = this.attr("abs:href")
//
//fun Element.firstText(query: String): String = this.selectFirstOpt(query).map { it.text() }.orElse("")
//
//fun Element.firstHref(query: String): String = this.selectFirstOpt(query).map { it.href() }.orElse("")
//
//fun Element.firstAbsHref(query: String): String = this.selectFirstOpt(query).map { it.absHref() }.orElse("")