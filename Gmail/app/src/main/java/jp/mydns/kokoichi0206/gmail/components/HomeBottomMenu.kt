package jp.mydns.kokoichi0206.gmail.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import jp.mydns.kokoichi0206.gmail.model.HomeBottomMenuData

@Composable
fun HomeBottomMenu() {
    val items = listOf(
        HomeBottomMenuData.Mail,
        HomeBottomMenuData.Meet,
        HomeBottomMenuData.Add,
    )

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black,
    ) {
        items.forEach {
            BottomNavigationItem(
                label = {
                    Text(text = it.title)
                },
                alwaysShowLabel = true,
                selected = false,
                onClick = {
                },
                icon = { Icon(imageVector = it.icon, contentDescription = it.title) }
            )
        }
    }
}
