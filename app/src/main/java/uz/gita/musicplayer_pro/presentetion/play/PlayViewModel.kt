package uz.gita.musicplayer_pro.presentetion.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import uz.gita.musicplayer_pro.domain.repository.AppRepository
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val direction: PlayDirection,
    private val appRepository: AppRepository,
) : ViewModel(),
    PlayContract.ViewModel {

    override val container =
        container<PlayContract.UIState, PlayContract.SideEffect>(PlayContract.UIState.InitState)

    override fun onEventDispatcher(intent: PlayContract.Intent) {
        when (intent) {
            is PlayContract.Intent.UserAction -> {
                intent { postSideEffect(PlayContract.SideEffect.UserAction(intent.playEnum)) }
            }

            is PlayContract.Intent.CheckMusic -> {
                intent {
                    reduce {
                        PlayContract.UIState.CheckMusic(
                            appRepository.queryMusicIsSaved(intent.musicData)
                        )
                    }
                }
            }

            is PlayContract.Intent.SaveMusic -> {
                appRepository.addMusic(intent.musicData)
            }

            is PlayContract.Intent.DeleteMusic -> {
                appRepository.deleteMusic(intent.musicData)
            }

            PlayContract.Intent.Back -> {
                viewModelScope.launch { direction.back() }
            }
        }
    }

}