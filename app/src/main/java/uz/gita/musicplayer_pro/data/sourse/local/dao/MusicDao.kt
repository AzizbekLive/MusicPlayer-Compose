package uz.gita.musicplayer_pro.data.sourse.local.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.gita.musicplayer_pro.data.sourse.local.entity.MusicEntity

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMusic(musicEntity: MusicEntity)

    @Delete
    fun deleteMusic(musicEntity: MusicEntity)

    @Query("Select * from musics")
    fun getAllMusics(): Flow<List<MusicEntity>>

    @Query("Select * from musics")
    fun getSavedMusics(): Cursor

    @Query("SELECT * FROM musics WHERE data = :query")
    fun queryMusicSaved(query: String): MusicEntity?
}