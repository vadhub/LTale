package com.vad.ltale.data.local

import android.content.Context
import okio.IOException
import java.io.InputStream

class SaveInternalHandle(private val context: Context) {

    fun saveFile(fileName: String, fileData: InputStream): String {

        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(fileData.readBytes())
            }

            return context.getFileStreamPath(fileName).absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }

}