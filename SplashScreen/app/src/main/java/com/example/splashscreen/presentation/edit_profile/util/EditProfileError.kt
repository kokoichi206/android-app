package com.example.splashscreen.presentation.edit_profile.util

sealed class EditProfileError : Error() {
    object FieldEmpty : EditProfileError()
}