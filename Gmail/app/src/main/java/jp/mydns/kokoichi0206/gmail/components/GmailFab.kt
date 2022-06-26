package jp.mydns.kokoichi0206.gmail.components

import androidx.compose.foundation.ScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable

@Composable
fun GmailFab(
    scrollState: ScrollState,
) {
    if (scrollState.value > 100) {
        ExtendedFloatingActionButton(
            text = {
                Text(text = "Compose")
            },
            onClick = { /*TODO*/ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "",
                )
            },
        )
    } else {
        FloatingActionButton(
            onClick = { /*TODO*/ },
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "",
            )
        }
    }
}
