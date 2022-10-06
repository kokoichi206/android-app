package jp.mydns.kokoichi0206.kmmnote.domain.note

interface NoteDatasource {

    suspend fun insertNote(note: Note)
    suspend fun getNoteById(id: Long): Note?
    suspend fun getAllNotes(): List<Note>
    suspend fun deleteNoteById(id: Long)
}