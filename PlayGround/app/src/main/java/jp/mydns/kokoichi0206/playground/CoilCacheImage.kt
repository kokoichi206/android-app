package jp.mydns.kokoichi0206.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage

@Composable
fun CoilCacheImage(
    clear: () -> Unit,
) {
    val imgUrl = "https://avatars.githubusercontent.com/u/52474650?v=4"

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        AsyncImage(
            model = imgUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
//            .aspectRatio(1280f/847f)
        )

//        SubcomposeAsyncImage(
//            model = imgUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxWidth(),
//            loading = {
//                CircularProgressIndicator()
//            },
//        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                clear()
            },
        ) {
            Text("clear cache")
        }
    }

}
