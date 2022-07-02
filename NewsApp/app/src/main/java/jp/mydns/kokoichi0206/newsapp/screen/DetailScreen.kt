package jp.mydns.kokoichi0206.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.coil.CoilImage
import jp.mydns.kokoichi0206.newsapp.MockData
import jp.mydns.kokoichi0206.newsapp.MockData.getTimeAgo
import jp.mydns.kokoichi0206.newsapp.NewsData
import jp.mydns.kokoichi0206.newsapp.R
import jp.mydns.kokoichi0206.newsapp.models.TopNewsArticle

@Composable
fun DetailScreen(
    article: TopNewsArticle,
    scrollState: ScrollState,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            MyTopAppBar(onBackPressed = {
                navController.popBackStack()
            })
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Details News", fontWeight = FontWeight.SemiBold)

            CoilImage(
                imageModel = article.urlToImage,
                contentScale = ContentScale.Crop,
                error = ImageBitmap.imageResource(id = R.drawable.ic_launcher_background),
                placeHolder = ImageBitmap.imageResource(id = R.drawable.ic_launcher_background),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoWithIcon(icon = Icons.Default.Edit, info = article.author ?: "Not Available")
                InfoWithIcon(
                    icon = Icons.Default.DateRange,
                    info = MockData.stringToDate(article.publishedAt!!).getTimeAgo(),
                )
            }

            Text(text = article.title ?: "Not Available", fontWeight = FontWeight.Bold)
            Text(
                text = article.description ?: "Not Available",
                modifier = Modifier
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun MyTopAppBar(
    onBackPressed: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(text = "Detail Screen", fontWeight = FontWeight.SemiBold)
        },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
        }
    )
}

@Composable
fun InfoWithIcon(
    icon: ImageVector,
    info: String,
) {

    Row {
        Icon(
            icon,
            contentDescription = "Author",
            modifier = Modifier
                .padding(end = 8.dp),
            colorResource(id = R.color.purple_200)
        )

        Text(text = info)
    }
}
