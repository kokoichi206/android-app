package jp.mydns.kokoichi206.bellog.config

data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
) : Comparable<SemanticVersion> {
    override fun compareTo(other: SemanticVersion): Int = compareValuesBy(
        this,
        other,
        SemanticVersion::major,
        SemanticVersion::minor,
        SemanticVersion::patch,
    )

    fun requiresReclassificationFrom(previous: SemanticVersion): Boolean =
        major != previous.major || minor != previous.minor

    override fun toString(): String = "$major.$minor.$patch"

    companion object {
        fun parse(value: String): SemanticVersion {
            val parts = value.split('.')
            require(parts.size == 3) { "Config version must use major.minor.patch: $value" }
            return SemanticVersion(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }
}
