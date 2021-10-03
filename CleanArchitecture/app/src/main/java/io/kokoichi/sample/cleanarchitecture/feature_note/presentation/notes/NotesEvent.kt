package io.kokoichi.sample.cleanarchitecture.feature_note.presentation.notes

import io.kokoichi.sample.cleanarchitecture.feature_note.domain.model.Note
import io.kokoichi.sample.cleanarchitecture.feature_note.domain.util.NoteOrder

sealed class NotesEvent {
    data class Order(val noteOrder: NoteOrder): NotesEvent()
    data class DeletesNote(val note: Note): NotesEvent()
    object RestoreNote: NotesEvent()
    object ToggleOrderSection: NotesEvent()
}
