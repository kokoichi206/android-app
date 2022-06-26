package jp.mydns.kokoichi0206.gmail.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeBottomMenuData(
    val icon: ImageVector,
    val title: String,
){
    object Mail: HomeBottomMenuData(
        icon = Icons.Outlined.Mail,
        title = "Mail",
    )
    object Meet: HomeBottomMenuData(
        icon = Icons.Outlined.VideoCall,
        title = "Meet",
    )
    object Add: HomeBottomMenuData(
        icon = Icons.Outlined.Add,
        title = "Add",
    )
}
