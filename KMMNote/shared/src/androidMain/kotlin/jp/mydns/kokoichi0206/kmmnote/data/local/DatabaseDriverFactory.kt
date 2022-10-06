package jp.mydns.kokoichi0206.kmmnote.data.local

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import jp.mydns.kokoichi0206.kmmnote.database.NoteDatabase

actual class DatabaseDriverFactory(
    private val context: Context,
) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NoteDatabase.Schema, context, "note.db")
    }
}