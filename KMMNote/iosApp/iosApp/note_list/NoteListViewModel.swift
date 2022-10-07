//
//  NoteListViewModel.swift
//  iosApp
//
//  Created by Takahiro Tominaga on 2022/10/07.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

extension NoteListScreen {
    // Only valid in NoteListScreen
    // MainActor -> Main Thread
    @MainActor class NoteListViewModel: ObservableObject {

        private var noteDataSource: NoteDatasource? = nil

        // let = const, val
        private let searchNotes = SearchNotes()

        private var notes = [Note]()
        @Published private(set) var filteredNotes = [Note]()
        @Published var searchText = "" {
            didSet {
                // Whenever the value is set,
                self.filteredNotes = searchNotes.execute(notes: self.notes, query: searchText)
            }
        }
        @Published private(set) var isSearchActive = false

        init(noteDataSource: NoteDatasource? = nil) {
            self.noteDataSource = noteDataSource
        }
        
        func loadNotes() {
            noteDataSource?.getAllNotes(completionHandler: { notes, error in
                self.notes = notes ?? []
                self.filteredNotes = self.notes
                
                print("self.notes: \(self.notes)")
            })
        }
        
        func deleteNoteById(id: Int64?) {
            if let id = id {
                noteDataSource?.deleteNoteById(id: id, completionHandler: { error in
                    self.loadNotes()
                })
            }
        }
        
        func toggleIsSearchActive() {
            isSearchActive.toggle()
            
            if !isSearchActive {
                searchText = ""
            }
        }
        
        func setNoteDataSource(noteDatasource: NoteDatasource) {
            self.noteDataSource = noteDatasource
        }
    }
}
