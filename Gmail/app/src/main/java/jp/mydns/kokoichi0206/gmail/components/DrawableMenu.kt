package jp.mydns.kokoichi0206.gmail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi0206.gmail.model.DrawerMenuData

@Composable
fun DrawableMenu(
    scrollState: ScrollState,
) {

    val menuList = listOf(
        DrawerMenuData.Divider,
        DrawerMenuData.AllInboxes,
        DrawerMenuData.Divider,
        DrawerMenuData.Primary,
        DrawerMenuData.HeaderOne,
        DrawerMenuData.HeaderTwo,
        DrawerMenuData.Contacts,
        DrawerMenuData.Divider,
        DrawerMenuData.Settings,
        DrawerMenuData.Help,

        // For scroll test
        DrawerMenuData.Primary,
        DrawerMenuData.HeaderOne,
        DrawerMenuData.HeaderTwo,
        DrawerMenuData.Contacts,
        DrawerMenuData.Divider,
        DrawerMenuData.Settings,
        DrawerMenuData.Help,
    )

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Gmail",
            color = Color.Red,
            modifier = Modifier
                .padding(start = 20.dp, top = 20.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        menuList.forEach { item ->
            when {
                item.isDivider -> {
                    Divider(modifier = Modifier.padding(bottom = 20.dp, top = 20.dp))
                }
                item.isHeader -> {
                    Text(
                        text = item.title!!,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(start = 20.dp, bottom = 20.dp, top = 20.dp)
                    )
                }
                else -> {
                    MailDrawerItem(item = item)
                }
            }
        }
    }
}

@Composable
fun MailDrawerItem(item: DrawerMenuData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 16.dp)
    ) {
        Image(
            imageVector = item.icon!!,
            contentDescription = item.title!!,
            modifier = Modifier
                .weight(0.5f),
        )
        Text(
            text = item.title,
            modifier = Modifier
                .weight(2.0f)
        )
    }
}
