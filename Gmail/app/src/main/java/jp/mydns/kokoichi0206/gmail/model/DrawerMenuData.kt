package jp.mydns.kokoichi0206.gmail.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DrawerMenuData(
    val icon: ImageVector? = null,
    val title: String? = null,
    val isDivider: Boolean = false,
    val isHeader: Boolean = false,
) {
    object AllInboxes : DrawerMenuData(
        icon = Icons.Outlined.AllInbox,
        title = "All inboxes",
    )

    object Primary : DrawerMenuData(
        icon = Icons.Outlined.Inbox,
        title = "Primary",
    )

    object Contacts : DrawerMenuData(
        icon = Icons.Outlined.Contacts,
        title = "Contacts",
    )

    object Settings : DrawerMenuData(
        icon = Icons.Outlined.Settings,
        title = "Settings",
    )

    object Help : DrawerMenuData(
        icon = Icons.Outlined.Help,
        title = "Help",
    )

    object Divider : DrawerMenuData(
        isDivider = true,
    )

    object HeaderOne : DrawerMenuData(
        isHeader = true,
        title = "ALL LABELS",
    )

    object HeaderTwo : DrawerMenuData(
        isHeader = true,
        title = "GOOGLE APPS",
    )
}
