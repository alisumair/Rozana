package com.example.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

object ImageUtils {
    @Composable
    fun rememberUriBitmap(uriString: String?): ImageBitmap? {
        val context = LocalContext.current
        return remember(uriString) {
            if (uriString.isNullOrBlank()) null
            else {
                try {
                    val uri = Uri.parse(uriString)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        bitmap?.asImageBitmap()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ImageUtils", "Failed to load bitmap: ${e.message}")
                    null
                }
            }
        }
    }
}
