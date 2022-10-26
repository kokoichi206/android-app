package jp.mydns.kokoichi0206.playground

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ModifierTest() {

    Box(
        modifier = Modifier
            .redBall(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .greenBoxRotate(
                    size = 100.dp,
                    duration = 3000,
                )
        )
        Box(
            modifier = Modifier
                .greenBoxRotate(
                    size = 200.dp,
                    duration = 1200,
                )
        )
    }
}

fun Modifier.redBall(): Modifier =
    clip(CircleShape)
        .background(Color.Red)
        .size(250.dp)

@Composable
fun Modifier.hoge(): Modifier {

    val transition = rememberInfiniteTransition()
    // Float type の animation。
    val angleRatio by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            // tween, keyframes など
            animation = keyframes {
                durationMillis = 3000
            },
            // Reverse or Restart
            repeatMode = RepeatMode.Reverse,
        ),
    )

    return size(100.dp)
        .graphicsLayer(
            rotationZ = 360f * angleRatio,
            alpha = 1f * angleRatio,
        )
        .background(Color.Green)
}

// Composition context: にアクセスできる
fun Modifier.greenBoxRotate(
    size: Dp,
    duration: Int,
): Modifier = composed {

    // この情報を使用するのに composable である必要がある。
    val transition = rememberInfiniteTransition()
    // Float type の animation。
    val angleRatio by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            // tween, keyframes など
            animation = keyframes {
                durationMillis = duration
            },
            repeatMode = RepeatMode.Reverse,
        ),
    )

    size(size)
//        .clip(RectangleShape)
        .graphicsLayer(
            rotationZ = 360f * angleRatio,
            alpha = 1f * angleRatio,
        )
        .background(Color.Green)
}
