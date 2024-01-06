package uz.gita.musicplayer_pro.utils.navigation

import kotlinx.coroutines.flow.SharedFlow

interface NavigatorHandler {
    val navigatorState: SharedFlow<NavigationArgs>
}