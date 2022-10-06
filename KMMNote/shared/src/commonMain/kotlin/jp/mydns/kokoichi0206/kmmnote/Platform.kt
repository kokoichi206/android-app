package jp.mydns.kokoichi0206.kmmnote

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform