package jp.mydns.kokoichi0206.kmmnote.android.note_list

import jp.mydns.kokoichi0206.kmmnote.domain.note.Note

data class NoteListState(
    val notes: List<Note> = emptyList(),
    val searchText: String = "",
    val isSearchActive: Boolean = false,
)
