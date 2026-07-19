package jp.mydns.kokoichi206.bellog.config

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SemanticVersionTest {
    @Test
    fun patchUpdateDoesNotRequireReclassification() {
        assertFalse(
            SemanticVersion.parse("1.2.4")
                .requiresReclassificationFrom(SemanticVersion.parse("1.2.3")),
        )
    }

    @Test
    fun minorUpdateRequiresReclassification() {
        assertTrue(
            SemanticVersion.parse("1.3.0")
                .requiresReclassificationFrom(SemanticVersion.parse("1.2.9")),
        )
    }

    @Test
    fun majorUpdateRequiresReclassification() {
        assertTrue(
            SemanticVersion.parse("2.0.0")
                .requiresReclassificationFrom(SemanticVersion.parse("1.9.9")),
        )
    }
}
