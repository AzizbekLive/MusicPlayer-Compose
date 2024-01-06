package uz.gita.musicplayer_pro.utils.navigation

import cafe.adriel.voyager.androidx.AndroidScreen

interface AppNavigator {
    suspend fun pop()
    suspend fun navigateTo(screen: AndroidScreen)
    suspend fun replace(screen: AndroidScreen)
}