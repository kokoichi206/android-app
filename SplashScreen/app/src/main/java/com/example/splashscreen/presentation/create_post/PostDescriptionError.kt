package com.example.splashscreen.presentation.create_post

sealed class PostDescriptionError : Error() {
    object FieldEmpty : PostDescriptionError()
}
