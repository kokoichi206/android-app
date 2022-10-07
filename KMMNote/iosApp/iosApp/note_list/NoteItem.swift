//
//  NoteItem.swift
//  iosApp
//
//  Created by Takahiro Tominaga on 2022/10/07.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteItem: View {
    
    var note: Note
    var onDeleteClick: () -> Void
    
    var body: some View {
        
        VStack(alignment: .leading) {
        
            HStack {
                
                Text(note.title)
                    .font(.title3)
                    .fontWeight(.semibold)
                Spacer()
                Button(action: onDeleteClick) {
                    Image(systemName: "xmark").foregroundColor(.black)
                }
            }.padding(.bottom, 3)
            
            Text(note.content)
                .fontWeight(.light)
                .padding(.bottom, 3)
            
            HStack {
                Spacer()
                Text(DateTimeUtil().formatNoteDate(dateTIme: note.created))
                    .font(.footnote)
                    .fontWeight(.light)
            }
        }
        .padding()
        .background(Color(hex: note.colorHex))
        .clipShape(RoundedRectangle(cornerRadius: 5.0))
    }
}

struct NoteItem_Previews: PreviewProvider {
    static var previews: some View {
        NoteItem(
            note: Note(
                id: nil,
                title: "My note",
                content: "Content",
                colorHex: 0x033330,
                created: DateTimeUtil().now())) {
                }
    }
}
