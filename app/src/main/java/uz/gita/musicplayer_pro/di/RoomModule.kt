package uz.gita.musicplayer_pro.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.gita.musicplayer_pro.data.sourse.local.dao.MusicDao
import uz.gita.musicplayer_pro.data.sourse.local.database.MusicDatabase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): MusicDatabase = Room.databaseBuilder(
        context,
        MusicDatabase::class.java, "musics"
    ).allowMainThreadQueries().build()

    @[Provides Singleton]
    fun provideMusicsDao(db: MusicDatabase): MusicDao = db.getMusicDao()
}