package uz.gita.musicplayer_pro.data.sourse.local.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.gita.musicplayer_pro.data.model.MusicData

@Entity(tableName = "musics")
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val artist: String?,
    val title: String?,
    val data: String?,
    val duration: Long,
    val uri: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val albumArt: ByteArray?,
) {
    fun toData() = MusicData(id, artist, title, data, duration, Uri.parse(uri),
        albumArt?.let { convertByteArrayToBitmap(it) })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicEntity

        if (id != other.id) return false
        if (artist != other.artist) return false
        if (title != other.title) return false
        if (data != other.data) return false
        if (duration != other.duration) return false
        if (albumArt != null) {
            if (other.albumArt == null) return false
            if (!albumArt.contentEquals(other.albumArt)) return false
        } else if (other.albumArt != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + duration.hashCode()
        result = 31 * result + (albumArt?.contentHashCode() ?: 0)
        return result
    }

    private fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return if (byteArray.isNotEmpty()) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            null
        }
    }

    private fun convertStringToBitmap(encodedString: String): Bitmap {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

}