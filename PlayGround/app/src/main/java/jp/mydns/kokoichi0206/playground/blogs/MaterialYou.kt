package jp.mydns.kokoichi0206.playground.blogs

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// AndroidDevSummit
//https://www.youtube.com/watch?v=xS4GpdIQUEo&list=PLWz5rJ2EKKc92MGTd1CgUtXZfhA74nUpb&index=40&ab_channel=AndroidDevelopers

// Links
// https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#dynamicDarkColorScheme(android.content.Context)
// https://m3.material.io/get-started
// https://foso.github.io/Jetpack-Compose-Playground/cookbook/detect_darkmode/
// https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ModalDrawerSheet(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.foundation.layout.WindowInsets,kotlin.Function1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialYouTest() {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val dark = isSystemInDarkTheme()
    val colorScheme = if (dynamicColor) {
        if (dark) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            dynamicLightColorScheme(LocalContext.current)
        }
    } else {
        if (dark) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }
    }

    // https://developer.android.com/reference/kotlin/androidx/compose/material3/TopAppBarScrollBehavior
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        // ここも必要！
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
//            MediumTopAppBar(
                title = {
                    Text(
                        text = "Title Text",
                        color = colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .background(colorScheme.onSurface),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .background(colorScheme.onSurface),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->

            LazyColumn(
                contentPadding = innerPadding,
            ) {
                items(10) { index ->
                    Row(
                        modifier = Modifier
                            .background(colorScheme.background),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "$index $it",
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
