package uz.gita.musicplayer_pro.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.gita.musicplayer_pro.presentetion.like.LikeDirection
import uz.gita.musicplayer_pro.presentetion.like.LikeDirectionImpl
import uz.gita.musicplayer_pro.presentetion.music.MusicListDirection
import uz.gita.musicplayer_pro.presentetion.music.MusicListDirectionImpl
import uz.gita.musicplayer_pro.presentetion.play.PlayDirection
import uz.gita.musicplayer_pro.presentetion.play.PlayDirectionImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DirectionModule {

    @Binds
    fun bindMusicListDirection(impl: MusicListDirectionImpl): MusicListDirection


    @Binds
    fun bindPlayDirection(impl: PlayDirectionImpl): PlayDirection

    @Binds
    fun bindLikeDirection(impl: LikeDirectionImpl): LikeDirection
}
