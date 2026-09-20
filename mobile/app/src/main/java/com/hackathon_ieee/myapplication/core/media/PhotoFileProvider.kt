package com.hackathon_ieee.myapplication.core.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object PhotoFileProvider {
    fun createCameraPhotoUri(context: Context): Uri {
        val photoDirectory = File(
            context.cacheDir,
            "report_photos"
        ).apply {
            mkdirs()
        }

        val photoFile = File.createTempFile(
            "riverguard_report_",
            ".jpg",
            photoDirectory
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }
}
