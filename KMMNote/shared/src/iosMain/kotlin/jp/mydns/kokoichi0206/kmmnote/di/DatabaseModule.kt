package jp.mydns.kokoichi0206.kmmnote.di

import jp.mydns.kokoichi0206.kmmnote.data.local.DatabaseDriverFactory
import jp.mydns.kokoichi0206.kmmnote.data.note.SqlDelightNoteDataSource
import jp.mydns.kokoichi0206.kmmnote.database.NoteDatabase
import jp.mydns.kokoichi0206.kmmnote.domain.note.NoteDatasource

class DatabaseModule {

    private val factory by lazy { DatabaseDriverFactory() }
    val noteDataSource: NoteDatasource by lazy {
        SqlDelightNoteDataSource(NoteDatabase(factory.createDriver()))
    }
}