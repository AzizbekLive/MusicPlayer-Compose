package uz.gita.musicplayer_pro.presentetion.play

import uz.gita.musicplayer_pro.utils.navigation.AppNavigator
import javax.inject.Inject

interface PlayDirection {
    suspend fun back()
}

class PlayDirectionImpl @Inject constructor(
    private val navigator : AppNavigator
): PlayDirection{
    override suspend fun back() {
        navigator.pop()
    }

}