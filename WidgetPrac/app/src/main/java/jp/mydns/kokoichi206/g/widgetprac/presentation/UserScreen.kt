package jp.mydns.kokoichi206.g.widgetprac.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier

@Composable
fun UserScreen(
    state: ViewerViewModel.UserState,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(CenterHorizontally),
            )
        } else {
            state.user?.let { user ->
                Text(text = user.login)
                Text(text = user.location)
            }
        }
    }
}
