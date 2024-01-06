package uz.gita.musicplayer_pro.presentetion.music

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.hilt.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.musicplayer_pro.MainActivity
import uz.gita.musicplayer_pro.data.model.CommandEnum
import uz.gita.musicplayer_pro.data.model.CursorEnum
import uz.gita.musicplayer_pro.data.model.PlayEnum
import uz.gita.musicplayer_pro.service.MusicService
import uz.gita.musicplayer_pro.ui.component.LoadingComponent
import uz.gita.musicplayer_pro.ui.component.MusicItemComponent
import uz.gita.musicplayer_pro.ui.component.getAlbumArt
import uz.gita.musicplayer_pro.utils.MyEventBus
import uz.gita.musicplayer_pro.utils.base.checkPermissions
import uz.gita.musicplayer_pro.utils.base.getMusicDataByPosition
import uz.gita.musicplayer_pro.utils.navigation.AppScreen
import uz.gita.musicplayermn.R

class MusicListScreen : AppScreen() {

    private fun startService(context: Context, commandEnum: CommandEnum) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("COMMAND", commandEnum)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else context.startService(intent)
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val activity = LocalContext.current as MainActivity
        val viewModel: MusicListContract.ViewModel = getViewModel<MusicListViewModel>()

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                MusicListContract.SideEffect.StartMusicService -> {
                    MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                    val intent = Intent(activity, MusicService::class.java)
                    intent.putExtra("COMMAND", CommandEnum.PLAY)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        activity.startForegroundService(intent)
                    } else activity.startService(intent)
                }

                is MusicListContract.SideEffect.UserAction -> {
                    when (sideEffect.playEnum) {
                        PlayEnum.MANAGE -> {
                            startService(context, CommandEnum.MANAGE)
                        }

                        PlayEnum.NEXT -> {
                            startService(context, CommandEnum.NEXT)
                        }

                        PlayEnum.PREV -> {
                            startService(context, CommandEnum.PREV)
                        }

                        PlayEnum.UPDATE_SEEKBAR -> {
                            startService(context, CommandEnum.UPDATE_SEEKBAR)
                        }
                    }
                }

                MusicListContract.SideEffect.OpenPermissionDialog -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
                        context.checkPermissions(
                            arrayListOf(
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.POST_NOTIFICATIONS,
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
                        }
                    } else {
                        context.checkPermissions(arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
                        }
                    }
                    viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
                }

                MusicListContract.SideEffect.OpenPermissionDialog -> {
                    viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
                }

            }
            viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
        }

        val uiState = viewModel.collectAsState().value
        viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
        Column {
            viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
            TopBar(eventListener = viewModel::onEventDispatcher)
            MusicListContent(uiState = uiState, eventListener = viewModel::onEventDispatcher)
        }

        viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
    }

}

@OptIn(ExperimentalUnitApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
private fun MusicListContent(
    uiState: MusicListContract.UIState,
    eventListener: (MusicListContract.Intent) -> Unit,
) {
    val context = LocalContext.current
    val dp = remember {
        mutableStateOf(64.dp)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF191B28))
    ) {
        eventListener.invoke(MusicListContract.Intent.LoadMusics(context))
        when (uiState) {
            MusicListContract.UIState.Loading -> {
                LoadingComponent()
                eventListener.invoke(MusicListContract.Intent.RequestPermission)
                eventListener.invoke(MusicListContract.Intent.LoadMusics(context))
            }

            MusicListContract.UIState.PreparedData -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFF151722))
                ) {
                    Column {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp),
                            color = Color.Black
                        ) {}
                        LazyColumn(Modifier.padding(bottom = dp.value)) {
                            for (pos in 0 until MyEventBus.cursor!!.count) {
                                item {
                                    MusicItemComponent(
                                        musicData = MyEventBus.cursor!!.getMusicDataByPosition(
                                            pos
                                        ),
                                        onClick = {
                                            eventListener.invoke(MusicListContract.Intent.OpenPlayScreen)
                                            MyEventBus.selectMusicPos = pos
                                            eventListener.invoke(MusicListContract.Intent.PlayMusic)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    val musicData = MyEventBus.currentMusicData.collectAsState()
                    if (musicData.value == null) {
                        dp.value = 0.dp
                    } else {
                        Column(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .background(color = Color(0xFF151722))
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color(0xFF151722))
                                    .height(2.dp),
                                color = Color.Black
                            ) {}
                            Surface(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .background(color = Color(0xFF151722))
                                    .fillMaxWidth()
                                    .clickable {
                                        eventListener.invoke(MusicListContract.Intent.OpenPlayScreen)
                                    },
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .background(color = Color(0xFF151722))
                                        .padding(all = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    if (musicData.value?.uri != null) {
                                        val bitmap =
                                            musicData.value!!.uri!!

                                        Image(
                                            bitmap = getAlbumArt(
                                                LocalContext.current,
                                                bitmap
                                            ).asImageBitmap(),
                                            contentDescription = "MusicDisk",
                                            modifier = Modifier
                                                .width(56.dp)
                                                .height(56.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.FillBounds
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_bag),
                                            contentDescription = "MusicDisk",
                                            modifier = Modifier
                                                .width(56.dp)
                                                .height(56.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = musicData.value!!.title ?: "Unknown name",
                                            color = Color.White,
                                            fontSize = TextUnit(16f, TextUnitType.Sp),
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = musicData.value!!.artist ?: "Unknown artist",
                                            color = Color(0XFF988E8E),
                                            fontSize = TextUnit(12f, TextUnitType.Sp),
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                    }

                                    IconButton(onClick = {
                                        eventListener.invoke(
                                            MusicListContract.Intent.UserAction(
                                                PlayEnum.PREV
                                            )
                                        )
                                    }) {
                                        Icon(
                                            modifier = Modifier
                                                .size(28.dp),
                                            painter = painterResource(id = R.drawable.vector_prev),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    val musicIsPlaying = MyEventBus.isPlaying.collectAsState()

                                    IconButton(onClick = {
                                        eventListener.invoke(
                                            MusicListContract.Intent.UserAction(
                                                PlayEnum.MANAGE
                                            )
                                        )
                                    }) {
                                        Icon(
                                            modifier = Modifier
                                                .size(28.dp),
                                            painter = painterResource(
                                                id = if (musicIsPlaying.value) R.drawable.ic_pause
                                                else R.drawable.ic_play
                                            ),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(onClick = {
                                        eventListener.invoke(
                                            MusicListContract.Intent.UserAction(
                                                PlayEnum.NEXT
                                            )
                                        )
                                    }) {
                                        Icon(
                                            modifier = Modifier
                                                .size(28.dp),
                                            painter = painterResource(id = R.drawable.vector_next),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(eventListener: (MusicListContract.Intent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF151722))
            .height(56.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bag),
            contentDescription = "",
            modifier = Modifier
                .padding(start = 16.dp)
                .size(22.dp)
        )
        Text(
            text = "MUSIC BAG",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF969696),
            ),
            modifier = Modifier.padding(start = 10.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable { eventListener.invoke(MusicListContract.Intent.OpenLikeScreen) },
            painter = painterResource(id = R.drawable.ic_fav),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MusicListContentPreview() {
    MusicListContent(MusicListContract.UIState.Loading) {}
}
