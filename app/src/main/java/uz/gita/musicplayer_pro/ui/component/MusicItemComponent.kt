package uz.gita.musicplayer_pro.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import uz.gita.musicplayer_pro.data.model.MusicData
import uz.gita.musicplayermn.R


@OptIn(ExperimentalUnitApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun MusicItemComponent(
    musicData: MusicData,
    onClick: () -> Unit,
) {
    Surface(modifier = Modifier
        .wrapContentHeight()
        .background(Color(0xFF262839))
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 4.dp)
        .clickable { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .background(Color(0xFF262839))
        ) {

            if (musicData.uri != null) {
                val bitmap1 = musicData.uri
                Image(
                    bitmap = getAlbumArt(LocalContext.current, bitmap1!!).asImageBitmap(),
                    contentDescription = "MusicDisk",
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillBounds
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.item_music),
                    contentDescription = "MusicDisk",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = musicData.title ?: "Unknown name",
                    color = Color.White,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = musicData.artist ?: "Unknown artist",
                    color = Color(0XFF988E8E),
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MusicItemComponentPreview() {
//        val musicDate = MusicData(
//            0,
//            "My artist",
//            "Test title",
//            null,
//            10000,
//
//        )
//
//        MusicItemComponent(
//            musicData = musicDate,
//            onClick = {}
//        )
}


fun getAlbumArt(context: Context, uri: Uri): Bitmap {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(context, uri)
    val data = mmr.embeddedPicture
    return if (data != null) {
        BitmapFactory.decodeByteArray(data, 0, data.size)
    } else {
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_bag)
    }
}