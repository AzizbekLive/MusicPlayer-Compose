package uz.gita.musicplayer_pro.domain.repository

import android.database.Cursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.gita.musicplayer_pro.data.model.MusicData
import uz.gita.musicplayer_pro.data.sourse.local.dao.MusicDao
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val dao: MusicDao,
) : AppRepository {

    override fun addMusic(musicData: MusicData) {
        dao.addMusic(musicData.toEntity())
    }

    override fun deleteMusic(musicData: MusicData) {
        dao.deleteMusic(musicData.toEntity())
    }

    override fun getAllMusics(): Flow<List<MusicData>> =
        dao.getAllMusics().map { list ->
            list.map { musicEntity ->
                musicEntity.toData()
            }
        }

    override fun getSavedMusics(): Cursor = dao.getSavedMusics()

    override fun queryMusicIsSaved(musicData: MusicData): Boolean {
        val data = dao.queryMusicSaved(musicData.data ?: "")
        return data != null
    }
}