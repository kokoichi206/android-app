import SwiftUI
import shared

@main
struct iOSApp: App {
    
    private let databaseModule = DatabaseModule()
    
	var body: some Scene {
		WindowGroup {
            NavigationView {
                NoteListScreen(noteDataSource: databaseModule.noteDataSource)
                    .navigationBarTitle("")
                    .navigationBarHidden(true)
            }
        }
	}
}
