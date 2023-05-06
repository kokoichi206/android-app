package jp.mydns.kokoichi206.g.layouts

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.dp
import jp.mydns.kokoichi206.g.layouts.ui.theme.LayoutsTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutsTheme {
                BoxWithConstraints(
                    modifier = Modifier
//                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    val pageHeight = this.maxHeight

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        FlowRow(
                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(pageHeight)
                        ) {
                            repeat(33) {
                                Box(
                                    modifier = Modifier
                                        .width(Random.nextInt(50, 200).dp)
                                        .height(100.dp)
                                        .background(Color(Random.nextLong(0xFFFFFFFF)))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map {
                it.measure(constraints)
            }

            val groupedPlaceables = mutableListOf<List<Placeable>>()
            var currentGroup = mutableListOf<Placeable>()
            var currentGroupWidth = 0

            placeables.forEach { placeable ->
                if (currentGroupWidth + placeable.width <= constraints.maxWidth) {
                    currentGroup.add(placeable)
                    currentGroupWidth += placeable.width
                } else {
                    groupedPlaceables.add(currentGroup)
                    currentGroup = mutableListOf(placeable)
                    currentGroupWidth = placeable.width
                }
            }

            if (currentGroup.isNotEmpty()) {
                groupedPlaceables.add(currentGroup)
            }

            Log.d("hoge", constraints.maxWidth.toString())
            Log.d("hoge", constraints.maxHeight.toString())
            layout(
                width = constraints.maxWidth,
                height = constraints.maxHeight,
            ) {
                var yPosition = 0
                groupedPlaceables.forEach { row ->
                    var xPosition = 0
                    row.forEach { placeable ->
                        placeable.place(
                            x = xPosition,
                            y = yPosition,
                        )

                        xPosition += placeable.width
                    }

                    yPosition += row.maxOfOrNull { it.height } ?: 0
                }
            }
        },
    )
}
