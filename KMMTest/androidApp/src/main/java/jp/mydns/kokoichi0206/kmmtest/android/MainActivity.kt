package jp.mydns.kokoichi0206.kmmtest.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import jp.mydns.kokoichi0206.kmmtest.android.ui.Primary
import jp.mydns.kokoichi0206.kmmtest.entity.RocketLaunch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = MainViewModel(this)
        setContent {
            val swipeRefreshStete = rememberSwipeRefreshState(false)
            SwipeRefresh(
                state = swipeRefreshStete,
                onRefresh = {
                    viewModel.loadLaunches(this, true)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainList(viewModel = viewModel)

                    if (viewModel.state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainList(
    viewModel: MainViewModel,
) {
    Column {
        TopAppBar(
            title = { Text("SpaceX Launches") },
            backgroundColor = Primary,
            contentColor = Color.White,
        )
        LazyColumn {
            viewModel.state.launcher?.let { items ->
                items(items) {
                    OneItem(item = it)
                }
            }
        }
    }
}

@Composable
fun OneItem(
    item: RocketLaunch,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 10.dp
    ) {
        Column(
        ) {
            Text(
                text = item.missionName,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
            if (item.launchSuccess == true) {
                Text(
                    text = "Successful",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.Green,
                )
            } else {
                Text(
                    text = "Unsuccessful",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.Red,
                )
            }
            Text(
                text = item.launchYear.toString(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Text(
                text = item.details ?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}
