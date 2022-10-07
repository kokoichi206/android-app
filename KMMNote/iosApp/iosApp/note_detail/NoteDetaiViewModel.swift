//
//  NoteDetaiViewModel.swift
//  iosApp
//
//  Created by Takahiro Tominaga on 2022/10/07.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

extension NoteDetailScreen {
    
    @MainActor class NoteDetaiViewModel: ObservableObject {
        
        private var noteDataSource: NoteDatasource?
        
        private var noteId: Int64? = nil
        @Published var noteTitle = ""
        @Published var noteContent = ""
        @Published private(set) var noteColor = Note.Companion().generateRandomColor()
        
        init(noteDataSource: NoteDatasource? = nil) {
            self.noteDataSource = noteDataSource
        }
        
        func loadNoteIfExists(id: Int64?) {
            if let id = id {
                self.noteId = id
                noteDataSource?.getNoteById(id: id, completionHandler: { note, error in
                    self.noteTitle = note?.title ?? ""
                    self.noteContent = note?.content ?? ""
                    self.noteColor = note?.colorHex ?? Note.Companion().generateRandomColor()
                })
            }
        }
        
        func saveNote(onSaved: @escaping () -> Void) {
            noteDataSource?.insertNote(
                note: Note(
                    id: noteId == nil ? nil : KotlinLong(value: noteId!),
                    title: noteTitle,
                    content: noteContent,
                    colorHex: noteColor,
                    created: DateTimeUtil().now()
                ), completionHandler: { error in
                    onSaved()
                })
        }
        
        func setParamsAndLoadNote(noteDataSource: NoteDatasource, noteId: Int64?) {
            self.noteDataSource = noteDataSource
            loadNoteIfExists(id: noteId)
        }
    }
}
