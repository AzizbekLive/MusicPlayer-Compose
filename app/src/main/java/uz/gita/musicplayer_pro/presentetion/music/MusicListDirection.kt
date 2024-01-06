package uz.gita.musicplayer_pro.presentetion.music

import uz.gita.musicplayer_pro.presentetion.like.LikeScreen
import uz.gita.musicplayer_pro.presentetion.play.PlayScreen
import uz.gita.musicplayer_pro.utils.navigation.AppNavigator
import javax.inject.Inject

interface MusicListDirection {
    suspend fun navigationToPlayScreen()
    suspend fun navigationToLikeScreen()
}

class MusicListDirectionImpl @Inject constructor(private val navigator: AppNavigator) :
    MusicListDirection {
    override suspend fun navigationToPlayScreen() {
        navigator.navigateTo(PlayScreen())
    }

    override suspend fun navigationToLikeScreen() {
        navigator.navigateTo(LikeScreen())
    }

}