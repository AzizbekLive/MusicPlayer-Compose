package uz.gita.musicplayer_pro.presentetion.play

import org.orbitmvi.orbit.ContainerHost
import uz.gita.musicplayer_pro.data.model.MusicData
import uz.gita.musicplayer_pro.data.model.PlayEnum

sealed interface PlayContract {

    interface ViewModel : ContainerHost<UIState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    interface UIState {
        object InitState : UIState
        data class CheckMusic(val isSaved: Boolean) : UIState
    }

    interface SideEffect {
        data class UserAction(val playEnum: PlayEnum) : SideEffect
    }

    interface Intent {
        data class UserAction(val playEnum: PlayEnum) : Intent
        data class SaveMusic(val musicData: MusicData) : Intent
        data class DeleteMusic(val musicData: MusicData) : Intent
        data class CheckMusic(val musicData: MusicData) : Intent
        object Back : Intent
    }
}