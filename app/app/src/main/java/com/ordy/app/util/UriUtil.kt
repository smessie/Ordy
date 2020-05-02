package com.ordy.app.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore




class UriUtil {

    companion object {

        /**
         * Get the real path of an Uri.
         * @param uri Android Uri
         * @param context Context
         */
        fun getPath(uri: Uri, context: Context): String? {
            var path = ""

            context.contentResolver.query(
                uri, arrayOf(MediaStore.Images.Media.DATA),
                null, null, null
            )?.apply {
                val columnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                moveToFirst()
                path = getString(columnIndex)
                close()
            }

            return path
        }
    }
}