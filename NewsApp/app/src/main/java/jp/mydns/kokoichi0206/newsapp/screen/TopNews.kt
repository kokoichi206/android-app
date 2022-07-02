package jp.mydns.kokoichi0206.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.coil.CoilImage
import jp.mydns.kokoichi0206.newsapp.MockData
import jp.mydns.kokoichi0206.newsapp.MockData.getTimeAgo
import jp.mydns.kokoichi0206.newsapp.R
import jp.mydns.kokoichi0206.newsapp.models.TopNewsArticle

@Composable
fun TopNews(
    navController: NavController,
    articles: List<TopNewsArticle>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Top News", fontWeight = FontWeight.SemiBold)

        LazyColumn {
            items(articles.size) { index ->
                TopNewsItem(
                    article = articles[index],
                    onNewsClick = {
                        navController.navigate("${Screen.Detailed.route}/$index")
                    }
                )
            }
        }
    }
}

@Composable
fun TopNewsItem(
    article: TopNewsArticle,
    onNewsClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(
                8.dp
            )
            .background(Color.Blue)
            .clickable {
                onNewsClick()
            }
    ) {
        CoilImage(
            imageModel = article.urlToImage,
            contentScale = ContentScale.Crop,
            error = ImageBitmap.imageResource(id = R.drawable.ic_launcher_background),
            placeHolder = ImageBitmap.imageResource(id = R.drawable.ic_launcher_background),
        )
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(top = 16.dp, start = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = MockData.stringToDate(article.publishedAt!!).getTimeAgo(),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = article.title!!,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
