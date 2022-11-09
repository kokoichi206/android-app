package jp.mydns.kokoichi0206.playground

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Study from
// Android DevSummit "5 quick animations to make your Compose app stand out"
// https://www.youtube.com/watch?v=0mfCbXrYBPE&list=PLWz5rJ2EKKc92MGTd1CgUtXZfhA74nUpb&index=4&ab_channel=AndroidDevelopers

// Resources:
// https://developer.android.com/jetpack/compose/animation
// https://storage.googleapis.com/android-stories/compose/Compose_Animation_Cheat_Sheet.pdf
// https://medium.com/androiddevelopers/customizing-animatedcontent-in-jetpack-compose-629c67b45894

@Composable
fun Messages() {
    Column(
        modifier = Modifier
            .padding(16.dp),
    ) {
        ClickableMessageWithAnimate(1)
        ClickableMessageWithAnimate(2)
        ClickableMessageWithAnimate(3)
        ClickableMessageNormal(4)
    }
}

@Composable
fun ClickableMessageWithAnimate(i: Int) {
    var showDetails by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
            .padding(8.dp)
    ) {
        ClickableText(
            modifier = Modifier
                .fillMaxWidth(),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 30.sp)) {
                    append("text $i")
                }
            },
            onClick = {
                showDetails = !showDetails
            }
        )

        // https://developer.android.com/reference/kotlin/androidx/compose/animation/package-summary?hl=ja#(androidx.compose.animation.core.Transition).AnimatedVisibility(kotlin.Function1,androidx.compose.ui.Modifier,androidx.compose.animation.EnterTransition,androidx.compose.animation.ExitTransition,kotlin.Function1)
        AnimatedVisibility(visible = showDetails) {
            Text(
                text = "2022/11/06 20:24:0$i",
                color = Color.Black,
            )
        }
    }
}

@Composable
fun ClickableMessageNormal(i: Int) {
    var showDetails by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
            .padding(8.dp)
    ) {
        ClickableText(
            modifier = Modifier
                .fillMaxWidth(),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 30.sp)) {
                    append("No Animation")
                }
            },
            onClick = {
                showDetails = !showDetails
            }
        )

        if (showDetails) {
            Text(
                text = "2022/11/06 20:24:0$i",
                color = Color.Black,
            )
        }
    }
}

@Composable
fun ExpandingText() {
    var expanded by remember {
        mutableStateOf(false)
    }
    Text(
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray)
            .padding(8.dp)
//            .animateContentSize()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow,
                )
            )
            .clickable {
                expanded = !expanded
            },
        maxLines = if (!expanded) 2 else 10,
        color = Color.Black,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopBar() {
    var selected by remember {
        mutableStateOf("A")
    }
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    Log.d("hoge", coordinates.toString())
                    Log.d("hoge", coordinates.size.toString())
                    size = coordinates.size
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selected = "A" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "A", color = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selected = "B" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "B",
                        color = Color.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selected = "C" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        color = Color.Black
                    )
                }
            }
        }

        // ここ！
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentScope.SlideDirection.Up
//                            towards = AnimatedContentScope.SlideDirection.Up
                ).with(
                    slideOutOfContainer(
                        animationSpec = tween(300, easing = EaseOut),
                        towards = AnimatedContentScope.SlideDirection.Down
//                                towards = AnimatedContentScope.SlideDirection.Down
                    )
                )
            }
        ) { targetState ->
            when (targetState) {
                "A" -> A(size)
                "B" -> B(size)
                "C" -> C(size)
            }
        }
    }
}

@Composable
fun A(size: IntSize) {
    Row(
        modifier = Modifier
            .height(size.height.dp)
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .height(size.height.dp)
                .weight(1f)
                .border(4.dp, Color.Green, RoundedCornerShape(8.dp))
        )
        Box(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f))
    }
}

val Int.toDp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

@Composable
fun B(size: IntSize) {
    Row(
        modifier = Modifier
            .height(size.height.dp)
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Box(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .height(size.height.dp)
                .weight(1f)
                .border(4.dp, Color.Green, RoundedCornerShape(8.dp))
        )
        Box(modifier = Modifier.weight(1f))
    }
}

@Composable
fun C(size: IntSize) {
    Row(
        modifier = Modifier
            .height(size.height.dp)
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Box(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .height(size.height.dp)
                .weight(1f)
                .border(4.dp, Color.Green, RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun SurveyProgress() {
    var idx by remember {
        mutableStateOf(1)
    }
    val progress by animateFloatAsState(
        targetValue = idx.toFloat() / 10
    )
    LinearProgressIndicator(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxWidth(),
        progress = progress,
        color = Color.Red,
    )
    Row {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .clickable {
                    idx -= 1
                    Log.d("hoge", idx.toString())
                    Log.d("hoge", progress.toString())
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "-",
                color = Color.Red,
                fontSize = 32.sp,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .clickable {
                    idx += 1
                    Log.d("hoge", idx.toString())
                    Log.d("hoge", progress.toString())
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "+",
                color = Color.Red,
                fontSize = 32.sp,
            )
        }
    }
}

@Composable
fun NormalSurveyProgress() {
    var idx by remember {
        mutableStateOf(1)
    }
    val progress = idx.toFloat() / 10
    LinearProgressIndicator(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxWidth(),
        progress = progress,
        color = Color.Red,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
            .clickable {
                idx += 1
                Log.d("hoge", idx.toString())
                Log.d("hoge", progress.toString())
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Normal Indicator",
            color = Color.Red
        )
    }
}

@Composable
fun ImageBorder() {
    val rainbowColors = listOf(Color.Red, Color.Magenta, Color.Green, Color.Blue, Color.Yellow, Color.Cyan)
//    val brush = Brush.radialGradient(rainbowColors)
    val brush = Brush.linearGradient(rainbowColors)
    val borderWidth = 4.dp

    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .drawBehind {
//                drawCircle(brush, style = Stroke(borderWidth))
                    rotate(rotationAnimation.value) {
                        drawCircle(brush)
                    }
                }
                .padding(borderWidth)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "",
        )
    }
}
