//
//  NoteListScreen.swift
//  iosApp
//
//  Created by Takahiro Tominaga on 2022/10/07.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteListScreen: View {
    
    private var noteDataSource: NoteDatasource
    // One instance to share
    @StateObject var viewModel = NoteListViewModel(noteDataSource: nil)
    
    @State private var isNoteSelected = false
    @State private var selectedNoteId: Int64? = nil
    
    init(noteDataSource: NoteDatasource) {
        self.noteDataSource = noteDataSource
    }
    
    var body: some View {
        VStack {
            // Box in Jetpack Compose
            ZStack {
                NavigationLink(destination: NoteDetailScreen(
                        noteDataSource: self.noteDataSource,
                        noteId: selectedNoteId
                    ),
                   isActive: $isNoteSelected) {
                    EmptyView()
                }
               .hidden()
                
                HideableSearchTextField<NoteDetailScreen>(
                    onSearchToggled: {
                        viewModel.toggleIsSearchActive()
                    },
                    destinationProvider: {
                        NoteDetailScreen(
                            noteDataSource: noteDataSource,
                            noteId: selectedNoteId
                        )
                    },
                    isSearchActive: viewModel.isSearchActive,
                    searchText: $viewModel.searchText
                )
                .frame(maxWidth: .infinity, minHeight: 40)
                .padding()
//                .background(.yellow)
//                .padding()
//                .background(.green)
                
                if !viewModel.isSearchActive {
                    Text("All notes")
                        .font(.title2)
                        .foregroundColor(.red)
                        .background(.black)
                }
            }
            
            List {
                ForEach(viewModel.filteredNotes, id: \.self.id) { note in
                    Button(action: {
                        isNoteSelected = true
                        selectedNoteId = note.id?.int64Value
                    }) {
                        NoteItem(note: note) {
                            viewModel.deleteNoteById(id: note.id?.int64Value)
                        }
                    }
                }
            }
            .onAppear {
                viewModel.loadNotes()
            }
            .listStyle(.plain)
            .listRowSeparator(.hidden)
        }
        .onAppear {
            viewModel.setNoteDataSource(noteDatasource: noteDataSource)
        }
    }
}
