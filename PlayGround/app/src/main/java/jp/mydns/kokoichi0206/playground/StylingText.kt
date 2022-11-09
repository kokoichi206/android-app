package jp.mydns.kokoichi0206.playground

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Styling text in Compose
// https://www.youtube.com/watch?v=_qls2CEAbxI&list=PLWz5rJ2EKKc92MGTd1CgUtXZfhA74nUpb&index=7&ab_channel=AndroidDevelopers

// Links
// https://developer.android.com/jetpack/compose/text
// https://github.com/android/compose-samples/tree/main/Jetchat

@Composable
fun StylingText() {
    val gradientColors = listOf(Color.Green, Color.Yellow, Color.Red)
    var textFieldValue by remember {
        mutableStateOf("")
    }
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth(),
//            .background(Color.Gray),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
        },
        cursorBrush = Brush.linearGradient(gradientColors),
        textStyle = MaterialTheme.typography.h3,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.background(Color.Gray),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp),
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "",
                )
                innerTextField()
            }
        }
    )

    var showMore by remember {
        mutableStateOf(false)
    }
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            style = MaterialTheme.typography.body1
                .copy(color = Color.Red),
            maxLines = if (showMore && expanded) 20 else 4,
//            maxLines = 8,
            onTextLayout = {
                if (it.hasVisualOverflow) {
                    showMore = true
                }
            },
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow,
                    )
                )
        )
        if (showMore) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    expanded = !expanded
                }) {
                    Text(
                        text = if (expanded) "Less" else "More",
                    )
                }
            }
        }
    }


    var showMoreWithLess by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Lorem ipsum dolor sit amet, cons ... less text",
            style = MaterialTheme.typography.body1
                .copy(color = Color.Red),
            maxLines = 8,
            onTextLayout = {
                if (it.hasVisualOverflow) {
                    showMore = true
                }
            }
        )
        if (showMoreWithLess) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { /*TODO*/ }) {
                    Text("More")
                }
            }
        }
    }
}
