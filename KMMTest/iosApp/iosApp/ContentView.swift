import SwiftUI
import shared
import UIKit

struct ContentView: View {
  @ObservedObject private(set) var viewModel: ViewModel
    var body: some View {
        NavigationView {
            if (viewModel.isRecording) {
                Text("Screen is recording.")
                    .font(.title)
                    .foregroundColor(.red)
            } else {
                listView()
                    .navigationBarTitle("SpaceX Launches")
                    .navigationBarItems(trailing:
                                            Button("Reload") {
                        //                        self.viewModel.loadLaunches(forceReload: true)
                        print("notification: tapped: \(viewModel.screenShot)")
                    })
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
            }
        }
        .onReceive(NotificationCenter.default.publisher(for: UIScreen.capturedDidChangeNotification)) { _ in
            viewModel.refreshIsRecording()
        }
//        ViewControllerWrapper()
    }

    private func listView() -> AnyView {
        switch viewModel.launches {
        case .loading:
            return AnyView(Text("Loading...").multilineTextAlignment(.center))
        case .result(let launches):
            return AnyView(List(launches) { launch in
                RocketLaunchRow(rocketLaunch: launch)
            })
        case .error(let description):
            return AnyView(Text(description).multilineTextAlignment(.center))
        }
    }
}

struct ViewControllerWrapper : UIViewControllerRepresentable {
    typealias UIViewControllerType = UIViewController
    func makeUIViewController(context: Context) -> UIViewController {
        return ViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {

    }
}
class ViewController: UIViewController {
    
    @IBOutlet weak var secureView: UIView!

    override func viewDidLoad() {
        super.viewDidLoad()
        
//        secureView.makeSecure()

        self.view.makeSecure()
        self.view.backgroundColor = UIColor.red
//        NotificationCenter.default.addObserver(self, selector: #selector(didTakeScreenshot(notification:)), name: UIApplication.userDidTakeScreenshotNotification, object: nil)
    }
    

    
    @objc func didTakeScreenshot(notification: Notification) {
        print("Screen Shot Taken")
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}

extension UIView {
    func makeSecure() {
        DispatchQueue.main.async {
            let field = UITextField()
            field.isSecureTextEntry = true
            self.addSubview(field)
            field.centerYAnchor.constraint(equalTo: self.centerYAnchor).isActive = true
            field.centerXAnchor.constraint(equalTo: self.centerXAnchor).isActive = true
            self.layer.superlayer?.addSublayer(field.layer)
            field.layer.sublayers?.first?.addSublayer(self.layer)
        }
    }
}

extension ContentView {

    enum LoadableLaunches {
        case loading
        case result([RocketLaunch])
        case error(String)
    }

    class ViewModel: ObservableObject {
        let sdk: SpaceXSDK
        @Published var launches = LoadableLaunches.loading
        @Published var screenShot: Bool = false
        @Published var isRecording: Bool = false

        init(sdk: SpaceXSDK) {
            self.sdk = sdk
            self.loadLaunches(forceReload: false)
            NotificationCenter.default.addObserver(
                forName: UIApplication.userDidTakeScreenshotNotification,
                object: nil,
                queue: .main) { notification in
                    print("notification: \(notification)")
                    self.screenShot = true
                }
            refreshIsRecording()
        }
        @objc func captured(_ notification: NSNotification) {
            if UIScreen.main.isCaptured {
                print("録画が始まりました")
            } else {
                print("録画が終わりました")
            }
        }

        func loadLaunches(forceReload: Bool) {
            self.launches = .loading
            sdk.getLaunches(forceReload: forceReload, completionHandler: { launches, error in
                if let launches = launches {
                    self.launches = .result(launches)
                } else {
                    self.launches = .error(error?.localizedDescription ?? "error")
                }
            })
        }

        func refreshIsRecording() -> Void {
            for screen in UIScreen.screens {
                if (screen.isCaptured) {
                    isRecording = true
                    return
                }
            }
            isRecording = false
        }
    }
}

extension RocketLaunch: Identifiable { }
