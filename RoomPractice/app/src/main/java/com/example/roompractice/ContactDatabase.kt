package com.example.roompractice

import androidx.room.Database

@Database(
    entities = [Contact::class],
    version = 1,
)
abstract class ContactDatabase {

    abstract val dao: ContactDao
}