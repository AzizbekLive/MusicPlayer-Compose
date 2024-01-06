package uz.gita.musicplayer_pro.utils.base

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.gita.musicplayer_pro.data.model.MusicData
import java.nio.ByteBuffer

private val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.ALBUM_ID
)

fun Context.getMusicsCursor(): Flow<Cursor> = flow {
    val cursor: Cursor = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        MediaStore.Audio.Media.IS_MUSIC + "!=0",
        null,
        null
    ) ?: return@flow
    emit(cursor)
}

fun Cursor.getMusicDataByPosition(pos: Int): MusicData {
    this.moveToPosition(pos)
    val id = this.getInt(0)
    val artist = this.getString(1)
    val title = this.getString(2)
    val data = this.getString(3)
    val duration = this.getLong(4)
//    val albumId = this.getString(5)
//    val albumArt = getAlbumArt()
    val uri =
        ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id.toLong()
        )
//    val uri = this.getString(5).toUri()

    return MusicData(id, artist, title, data, duration, uri, null)
}

//fun Cursor.getMusicDataByPosition(pos: Int): MusicData {
//    this.moveToPosition(pos)
//    val id = this.getInt(0)
//    val artist = this.getString(1)
//    val title = this.getString(2)
//    val data = this.getString(3)
//    val duration = this.getLong(4)
//    val albumId = this.getBlob(5)
//    val albumArt = getAlbumArt(byteArrayToLong(albumId))
//
//    return MusicData(id, artist, title, data, duration, albumArt)
//}

fun byteArrayToLong(byteArray: ByteArray): Long {
//    require(byteArray.size == 8) { "Byte array length must be 8" }

    val buffer = ByteBuffer.allocate(byteArray.size)
    buffer.put(byteArray)
    buffer.flip()

    return buffer.long
}

private fun Cursor.getAlbumArt(albumId: Long): Bitmap? {
//    val contentResolver = App.context.contentResolver
//    val albumArtUri = Uri.parse("content://media/external/audio/albumart")
//    val albumUri = Uri.withAppendedPath(albumArtUri, albumId.toString())
//    var albumArt: Bitmap? = null
//
//    try {
//        val inputStream = contentResolver.openInputStream(albumUri)
//        albumArt = BitmapFactory.decodeStream(inputStream)
//    } catch (e: FileNotFoundException) {
//        Log.e("MusicUtils", "Album art not found for album ID: $albumId", e)
//    }

    return null
}