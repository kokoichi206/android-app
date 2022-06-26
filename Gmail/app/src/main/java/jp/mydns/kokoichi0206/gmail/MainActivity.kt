package jp.mydns.kokoichi0206.gmail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import jp.mydns.kokoichi0206.gmail.components.*
import jp.mydns.kokoichi0206.gmail.ui.theme.GmailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GmailTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    GmailApp()
                }
            }
        }
    }
}

@Composable
fun GmailApp() {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeAppBar(
                scaffoldState = scaffoldState,
                scope = coroutineScope,
            )
        },
        drawerContent = {
            DrawableMenu(
                scrollState = scrollState,
            )
        },
        bottomBar = {
            HomeBottomMenu()
        },
        floatingActionButton = {
            GmailFab(
                scrollState = scrollState,
            )
        },
    ) {
        // Can use 'it' as PaddingValues !!!
        // Bottom bar height !
        MailList(
            paddingValues = it,
            scrollState = scrollState,
        )
    }
}
