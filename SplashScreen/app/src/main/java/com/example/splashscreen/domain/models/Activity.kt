package com.example.splashscreen.domain.models

import com.example.splashscreen.domain.util.ActivityAction

data class Activity(
    val username: String,
    val actionType: ActivityAction,
    val formattedTime: String,
)
