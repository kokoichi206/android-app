package jp.mydns.kokoichi0206.playground.blogs

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            "Animated Indicator",
            color = Color.Black,
            fontSize = 24.sp,
        )
    }

    var idx by remember {
        mutableStateOf(1)
    }
    val progress by animateFloatAsState(
        targetValue = idx.toFloat() / 5,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        )
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
fun NormalIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            "Normal Indicator",
            color = Color.Black,
            fontSize = 24.sp,
        )
    }

    var idx by remember {
        mutableStateOf(1)
    }
    val progress = idx.toFloat() / 5
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
