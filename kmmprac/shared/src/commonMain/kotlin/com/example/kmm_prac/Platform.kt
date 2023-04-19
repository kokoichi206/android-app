package com.example.kmm_prac

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform