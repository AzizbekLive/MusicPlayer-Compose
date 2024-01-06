package uz.gita.musicplayer_pro.presentetion.like

import uz.gita.musicplayer_pro.presentetion.play.PlayScreen
import uz.gita.musicplayer_pro.utils.navigation.AppNavigator
import javax.inject.Inject

interface LikeDirection {
    suspend fun navigateToPlayScreen()
}

class LikeDirectionImpl @Inject constructor(
    private val appNavigator: AppNavigator
) : LikeDirection {

    override suspend fun navigateToPlayScreen() {
        appNavigator.navigateTo(PlayScreen())
    }
}