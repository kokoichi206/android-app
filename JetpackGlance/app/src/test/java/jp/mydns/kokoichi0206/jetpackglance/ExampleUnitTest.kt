package jp.mydns.kokoichi0206.jetpackglance

import androidx.compose.ui.unit.dp
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun hoge() {
        assertEquals(3, ((276.dp - 57.dp) / 70.dp).toInt())
    }
}