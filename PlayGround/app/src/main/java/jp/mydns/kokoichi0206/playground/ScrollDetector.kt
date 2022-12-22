package jp.mydns.kokoichi0206.playground

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScrollDetector() {

//    Scaffold(
//        bottomBar = {
//            BottomAppBar(
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.Center,
//                ) {
//                    Icon(imageVector = Icons.Default.List, contentDescription = "List")
//                }
//            }
//        }
//    ) {
//        Box(
//            modifier = Modifier
//                .padding(it)
////                .padding(top = it.calculateTopPadding())
//        ) {
////            ListView()
//
//            val offset = remember { mutableStateOf(0f) }
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .scrollable(
//                        orientation = Orientation.Vertical,
//                        state = rememberScrollableState { delta ->
//                            offset.value = offset.value + delta
//                            Log.d("hoge", "delta: $delta")
//                            offset.value
//                            delta
//                        }
//                    )
//            ) {
//                (0..100).forEach {
//                    Text(
//                        modifier = Modifier
//                            .padding(8.dp),
//                        text = "hi, $it",
//                    )
//                }
//            }
//        }
//    }

    Test()

//    ListView()
}

@Composable
fun Test() {
    val offset = remember { mutableStateOf(0f) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(
                enabled = true,
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    offset.value = offset.value + delta
                    Log.d("hoge", "delta: $delta")
                    offset.value
                    delta
                }
            )
            .offset(y = offset.value.toDp())
    ) {
        (0..100).forEach {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = "hi, $it",
            )
        }
    }
}


@Composable
fun ListView(
    onScroll: (Int) -> Unit = {},
) {

    val scrollState = rememberLazyListState()
    var lastIndex by remember { mutableStateOf(0) }
//    val behavior = rememberSnapFlingBehavior(lazyListState = scrollState)
//
    LaunchedEffect(scrollState) {

        snapshotFlow {
            scrollState.layoutInfo
        }.collect {

            if (it.visibleItemsInfo.isNotEmpty()) {
                val info = it.visibleItemsInfo[0]
                if (lastIndex != info.index) {
                    // Scroll された Index 分、呼び出し元に返してあげる
                    val diff = lastIndex - info.index
                    lastIndex = info.index
                    onScroll(diff)
                }
            }
        }
    }

    LazyColumn(
        state = scrollState,
    ) {
        items(100) { idx ->
            Box(
                modifier= Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = "hi, $idx",
                )
            }
        }
    }
}
