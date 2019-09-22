package es.juanavila.liverss.framework.storage

import android.app.Application
import android.content.Context
import es.juanavila.liverss.application.services.FileService
import java.io.File
import javax.inject.Inject

class AndroidFileService @Inject constructor(ctx: Application) : FileService {

    override fun searchInAppPrivateDir(subFolder: String, fileName: String): File {
        return File(File(ctx.filesDir.parent,subFolder),fileName)
    }

    private val ctx : Context = ctx.applicationContext

    override fun searchInAppPrivateDir(fileName: String): File {
        return File(ctx.filesDir.parent,fileName)
    }
}