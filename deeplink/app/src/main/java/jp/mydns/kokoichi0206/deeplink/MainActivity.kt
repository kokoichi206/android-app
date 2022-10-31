package jp.mydns.kokoichi0206.deeplink

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import jp.mydns.kokoichi0206.deeplink.ui.theme.DeeplinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeeplinkTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(onClick = {
                                    navController.navigate("detail")
                                }) {
                                    Text(
                                        text = "Detail"
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://kokoichi0206.mydns.jp/999")
                                    )
                                    val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                                        addNextIntentWithParentStack(intent)
                                        getPendingIntent(
                                            0,
                                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                                        )
                                    }
                                    pendingIntent.send()
                                }) {
                                    Text(
                                        text = "Trigger"
                                    )
                                }
                            }

                        }
                    }

                    composable(
                        route = "detail",
                        deepLinks = listOf(
                            navDeepLink {
//                                uriPattern = "https://kokoichi0206.mydns.jp/android/{id}"
                                uriPattern = "https://kokoichi0206.mydns.jp/{id}"
                                action = Intent.ACTION_VIEW
                            }
                        ),
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        ),
                    ) { entry ->
                        val id = entry.arguments?.getInt("id")
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "The is is $id")
                        }
                    }
                }
            }
        }
    }
}
