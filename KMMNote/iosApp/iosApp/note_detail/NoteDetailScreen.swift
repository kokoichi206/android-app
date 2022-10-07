//
//  NoteDetailScreen.swift
//  iosApp
//
//  Created by Takahiro Tominaga on 2022/10/07.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteDetailScreen: View {
    
    private var noteDataSource: NoteDatasource
    private var noteId: Int64? = nil
    
    @StateObject var viewModel = NoteDetaiViewModel(noteDataSource: nil)
    
    // Environment is equivalent to Context in android
    @Environment(\.presentationMode) var presentation
    
    var body: some View {
        VStack(alignment: .leading) {
            TextField("Enter a title...", text: $viewModel.noteTitle)
                .font(.title)
            TextField("Enter some content...", text: $viewModel.noteContent)
            Spacer()
        }
        .toolbar {
            Button {
                viewModel.saveNote {
                    // back to the list screen
                    self.presentation.wrappedValue.dismiss()
                }
            } label: {
                Image(systemName: "checkmark")
            }
        }
        .padding()
        .background(Color(hex: viewModel.noteColor))
        .onAppear {
            viewModel.setParamsAndLoadNote(noteDataSource: noteDataSource, noteId: noteId)
        }
        .navigationBarTitle("")
    }
    
    init(noteDataSource: NoteDatasource, noteId: Int64? = nil) {
        self.noteDataSource = noteDataSource
        self.noteId = noteId
    }
}
