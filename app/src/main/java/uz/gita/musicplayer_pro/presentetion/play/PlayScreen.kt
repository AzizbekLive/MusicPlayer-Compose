package uz.gita.musicplayer_pro.presentetion.play

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.hilt.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.musicplayer_pro.data.model.CommandEnum
import uz.gita.musicplayer_pro.data.model.PlayEnum
import uz.gita.musicplayer_pro.service.MusicService
import uz.gita.musicplayer_pro.ui.component.getAlbumArt
import uz.gita.musicplayer_pro.utils.MyEventBus
import uz.gita.musicplayer_pro.utils.base.getMusicDataByPosition
import uz.gita.musicplayer_pro.utils.navigation.AppScreen
import uz.gita.musicplayermn.R
import java.util.concurrent.TimeUnit

class PlayScreen : AppScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val viewModel: PlayContract.ViewModel = getViewModel<PlayViewModel>()
        val uiState = viewModel.collectAsState()

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is PlayContract.SideEffect.UserAction -> {
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
            }
        }

        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold {
                PlayScreenContent(
                    uiState,
                    viewModel::onEventDispatcher,
                    modifier = Modifier.padding(it)
                )
            }
        }
    }

    private fun startService(context: Context, commandEnum: CommandEnum) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("COMMAND", commandEnum)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else context.startService(intent)
    }
}

private fun getTime(time: Int): String {
    val hour = time / 3600
    val minute = (time % 3600) / 60
    val second = time % 60

    val hourText = if (hour > 0) {
        if (hour < 10) "0$hour:"
        else "$hour:"
    } else ""

    val minuteText = if (minute < 10) "0$minute:"
    else "$minute:"

    val secondText = if (second < 10) "0$second"
    else "$second"

    return "$hourText$minuteText$secondText"
}


@Composable
fun PlayScreenContent(
    uiState: State<PlayContract.UIState>,
    onEventDispatcher: (PlayContract.Intent) -> Unit,
    modifier: Modifier,
) {

    val musicData = MyEventBus.currentMusicData.collectAsState(
        initial = MyEventBus.cursor!!.getMusicDataByPosition(MyEventBus.selectMusicPos)
    )

    val seekBarState = MyEventBus.currentTimeFlow.collectAsState(initial = 0)
    var seekBarValue by remember { mutableStateOf(seekBarState.value) }
    val musicIsPlaying = MyEventBus.isPlaying.collectAsState()

    val milliseconds = musicData.value!!.duration
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = (milliseconds / 1000 / 60) % 60
    val seconds = (milliseconds / 1000) % 60

    val duration = if (hours == 0L) "%02d:%02d".format(minutes, seconds)
    else "%02d:%02d:%02d".format(hours, minutes, seconds) // 03:45

    onEventDispatcher(PlayContract.Intent.CheckMusic(musicData.value!!))

    Column(Modifier.fillMaxSize()) {
        when (uiState.value) {
            is PlayContract.UIState.CheckMusic -> {
                val isSaved = (uiState.value as PlayContract.UIState.CheckMusic).isSaved
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF262839))
                            .height(56.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { onEventDispatcher(PlayContract.Intent.Back) }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            color = Color.White,
                            text = "Play",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                if (isSaved) {
                                    onEventDispatcher.invoke(
                                        PlayContract.Intent.DeleteMusic(musicData.value!!)
                                    )
                                } else {
                                    onEventDispatcher.invoke(
                                        PlayContract.Intent.SaveMusic(musicData.value!!)
                                    )
                                }

                                onEventDispatcher.invoke(PlayContract.Intent.CheckMusic(musicData.value!!))
                            }
                        ) {
                            Image(
                                painter = painterResource(id = if (isSaved) R.drawable.ic_favorite_selected else R.drawable.round_favorite_border_24),
                                contentDescription = null,
                                Modifier.size(28.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = Color.Black
                    ) {}
                }
            }

            else -> {}
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF191B28))
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                if (musicData.value!!.uri != null) {
                    val bitmap = musicData.value!!.uri!!
                    Image(
                        bitmap = getAlbumArt(LocalContext.current, bitmap!!).asImageBitmap(),
                        contentDescription = "MusicDisk",
                        modifier = Modifier
                            .size(250.dp)
                            .padding(top = 70.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .size(250.dp)
                            .padding(top = 70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = R.drawable.ic_bag),
                        contentDescription = null
                    )

                }

                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = musicData.value!!.title ?: "Unknown",
                    fontSize = 24.sp,
                    color = Color.White
                )

                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    text = musicData.value!!.artist ?: "Unknown",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Slider(
                    value = seekBarState.value.toFloat(),
                    onValueChange = { newState ->
                        seekBarValue = newState.toInt()
                        MyEventBus.currentTime.value = seekBarValue
                        onEventDispatcher.invoke(PlayContract.Intent.UserAction(PlayEnum.UPDATE_SEEKBAR))
                    },
                    onValueChangeFinished = {
                        MyEventBus.currentTime.value = seekBarValue
                        onEventDispatcher.invoke(PlayContract.Intent.UserAction(PlayEnum.UPDATE_SEEKBAR))
                    },
                    valueRange = 0f..musicData.value!!.duration.toFloat(),
                    steps = 1000,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF9900),
                        activeTickColor = Color(0xFFFF9900),
                        activeTrackColor = Color(0xFFFFFFFF)
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f),
                        text = getTime(seekBarState.value / 1000)
                    )
                    Text(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f),
                        textAlign = TextAlign.End,
                        text = duration
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        onEventDispatcher(PlayContract.Intent.UserAction(PlayEnum.PREV))
                        seekBarValue = 0
                        onEventDispatcher(PlayContract.Intent.CheckMusic(musicData.value!!))
                    }) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp),
                            painter = painterResource(id = R.drawable.vector_prev),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = {
                        onEventDispatcher.invoke(
                            PlayContract.Intent.UserAction(
                                PlayEnum.MANAGE
                            )
                        )
                    }) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9900)),
                            painter = painterResource(
                                id = if (musicIsPlaying.value) R.drawable.ic_pause
                                else R.drawable.ic_play
                            ),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                    IconButton(onClick = {
                        onEventDispatcher(PlayContract.Intent.UserAction(PlayEnum.NEXT))
                        seekBarValue = 0
                        onEventDispatcher(PlayContract.Intent.CheckMusic(musicData.value!!))
                    }) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp),
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