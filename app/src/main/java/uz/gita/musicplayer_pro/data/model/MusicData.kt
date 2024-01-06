package uz.gita.musicplayer_pro.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import uz.gita.musicplayer_pro.data.sourse.local.entity.MusicEntity
import java.io.ByteArrayOutputStream

data class MusicData(
    val id: Int,
    val artist: String?,
    val title: String?,
    val data: String?,
    val duration: Long,
    val uri: Uri?,
    val albumArt: Bitmap?,
) {
    fun toEntity() = MusicEntity(id, artist, title, data, duration, uri = Uri.parse(uri.toString()).toString(),
        albumArt?.let { convertBitmapToByteArray(it) }
    )

    private fun convertBitmapToByteArray(
        bitmap: Bitmap,
        maxWidth: Int = 800,
        maxHeight: Int = 800,
    ): ByteArray {
        val resizedBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
            val ratioBitmap = bitmap.width.toFloat() / bitmap.height.toFloat()
            val finalWidth: Int
            val finalHeight: Int
            if (ratioBitmap > 1) {
                finalWidth = maxWidth
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            } else {
                finalHeight = maxHeight
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            }
            Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
        } else {
            bitmap
        }

        ByteArrayOutputStream().use { stream ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            return stream.toByteArray()
        }
    }

    fun convertBitmapToString(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return encodedString
    }
}