package jp.mydns.kokoichi0206.firstcompose.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import jp.mydns.kokoichi0206.firstcompose.R

@Composable
fun ProfileWithCardView() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(33.dp)
            ),
        elevation = 6.dp,
    ) {
        ProfileView()
    }
}

@Composable
fun ProfileView() {

    BoxWithConstraints {
        val constraints = if (minWidth < 600.dp) {
            portraitConstraints(margin = 16.dp)
        } else {
            landscapeConstraints(margin = 16.dp)
        }

        ConstraintLayout(constraintSet = constraints) {
//        val (image, nameText, bornText, rowStats, buttonFollow, buttonDirectMsg) = createRefs()
//
//        val guideLine = createGuidelineFromTop(0.1f)

            Image(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = Color.Red,
                        shape = CircleShape,
                    )
                    .layoutId("image"),
//                .constrainAs(image) {
//                    top.linkTo(guideLine)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                },
                painter = painterResource(id = R.drawable.face),
                contentDescription = "face",
                contentScale = ContentScale.Crop,
            )

            Text(
                text = "John Doe",
                modifier = Modifier
                    .layoutId("nameText")
//                .constrainAs(nameText) {
//                    top.linkTo(image.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
            )
            Text(
                text = "Wonderland",
                modifier = Modifier
//                .constrainAs(bornText) {
//                    top.linkTo(nameText.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
//                .constrainAs(rowStats) {
//                    top.linkTo(bornText.bottom)
//                }
                ,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ProfileStats(count = "150", title = "Followers")
                ProfileStats(count = "1010", title = "Following")
                ProfileStats(count = "32", title = "Posts")
            }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly,
//        ) {
            Button(onClick = { /*TODO*/ },
                modifier = Modifier
//                .constrainAs(buttonFollow) {
//                    top.linkTo(rowStats.bottom, margin = 16.dp)
//                    start.linkTo(parent.start)
//                    end.linkTo(buttonDirectMsg.start)
//                    width = Dimension.wrapContent
//                }
            ) {
                Text(text = "Follow me")
            }
            Button(onClick = { /*TODO*/ },
                modifier = Modifier
//                .constrainAs(buttonDirectMsg) {
//                    top.linkTo(rowStats.bottom, margin = 16.dp)
//                    start.linkTo(buttonFollow.end)
//                    end.linkTo(parent.end)
//                }
            ) {
                Text(text = "Direct Message")
            }
//        }
        }
    }


}

@Composable
fun ProfileStats(
    count: String,
    title: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = count, fontWeight = FontWeight.Bold)
        Text(text = title)
    }
}

private fun portraitConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val image = createRefFor("image")
        val nameText = createRefFor("nameText")
        constrain(image) {
//            top.linkTo(guideLine)
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(nameText) {
            top.linkTo(image.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }
}

private fun landscapeConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val image = createRefFor("image")
        val nameText = createRefFor("nameText")
        constrain(image) {
//            top.linkTo(guideLine)
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(nameText) {
            top.linkTo(image.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    ProfileView()
}
