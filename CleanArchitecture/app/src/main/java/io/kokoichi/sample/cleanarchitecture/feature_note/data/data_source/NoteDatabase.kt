package io.kokoichi.sample.cleanarchitecture.feature_note.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import io.kokoichi.sample.cleanarchitecture.feature_note.domain.model.Note

@Database(
    entities = [Note::class],
    version = 1
)

abstract class NoteDatabase: RoomDatabase() {

    abstract val noteDao: NoteDao
}