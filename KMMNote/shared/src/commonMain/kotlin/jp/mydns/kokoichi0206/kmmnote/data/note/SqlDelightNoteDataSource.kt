package jp.mydns.kokoichi0206.kmmnote.data.note

import jp.mydns.kokoichi0206.kmmnote.database.NoteDatabase
import jp.mydns.kokoichi0206.kmmnote.domain.note.Note
import jp.mydns.kokoichi0206.kmmnote.domain.note.NoteDatasource
import jp.mydns.kokoichi0206.kmmnote.domain.time.DateTimeUtil

class SqlDelightNoteDataSource(
    db: NoteDatabase,
) : NoteDatasource {

    private val queries = db.noteQueries

    override suspend fun insertNote(note: Note) {
        queries.insertNote(
            id = note.id,
            title = note.title,
            content = note.content,
            colorHex = note.colorHex,
            created = DateTimeUtil.toEpochMills(note.created),
        )
    }

    override suspend fun getNoteById(id: Long): Note? {
        return queries
            .getNoteById(id)
            .executeAsOneOrNull()
            ?.toNote()
    }

    override suspend fun getAllNotes(): List<Note> {
        return queries.getAllNotes().executeAsList()
            .map { it.toNote() }
    }

    override suspend fun deleteNoteById(id: Long) {
        return queries.deleteNoteById(id)
    }
}