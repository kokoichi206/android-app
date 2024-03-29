package core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIViewController

actual class ImagePickerFactory(
    private val rootController: UIViewController,
) {

    @Composable
    actual fun create(): ImagePicker {
        return remember(rootController) {
            ImagePicker(rootController)
        }
    }
}