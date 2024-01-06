package uz.gita.musicplayer_pro.presentetion.like

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import uz.gita.musicplayer_pro.domain.repository.AppRepository
import uz.gita.musicplayer_pro.utils.MyEventBus
import uz.gita.musicplayer_pro.utils.base.getMusicDataByPosition
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val direction: LikeDirection,
    private val appRepository: AppRepository
) : ViewModel(), LikeContract.ViewModel {

    override val container =
        container<LikeContract.UIState, LikeContract.SideEffect>(LikeContract.UIState.Loading)


    override fun onEventDispatcher(intent: LikeContract.Intent) {
        when (intent) {
            LikeContract.Intent.CheckMusicExistence -> {
                viewModelScope.launch {
                    MyEventBus.roomCursor = appRepository.getSavedMusics()

                    for (i in 0 until MyEventBus.roomCursor!!.count) {
                        val roomData = MyEventBus.roomCursor!!.getMusicDataByPosition(i)
                        var bool = true
                        for (j in 0 until MyEventBus.cursor!!.count) {
                            val storageData = MyEventBus.cursor!!.getMusicDataByPosition(j)
                            bool = bool && roomData != storageData
                        }
                        if (bool) {
                            LikeContract.Intent.DeleteMusic(roomData)
                        }
                    }
                    intent { reduce { LikeContract.UIState.IsExistMusic } }
                }
            }

            LikeContract.Intent.LoadMusics -> {
                intent { reduce { LikeContract.UIState.PreparedData } }
            }

            is LikeContract.Intent.DeleteMusic -> {
                appRepository.deleteMusic(intent.musicData)
            }

            LikeContract.Intent.PlayMusic -> {
                intent { postSideEffect(LikeContract.SideEffect.StartMusicService) }
            }

            LikeContract.Intent.OpenPlayScreen -> {
                viewModelScope.launch { direction.navigateToPlayScreen() }
            }
        }
    }
}