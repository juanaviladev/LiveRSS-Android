package es.juanavila.liverss.application.usecases.autoclean

import es.juanavila.liverss.application.services.FileService
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class GetDiskUsage @Inject constructor(
    private val fileService: FileService
) :
    UseCase<NonInput, GetDiskUsage.Output>() {

    override suspend fun run(param: NonInput): Output {
        val dbFile = fileService.searchInAppPrivateDir("databases","updates.db")
        val length = dbFile.length()
        return Output(
            length,
            humanReadableByteCount(length, true)
        )
    }

    private fun humanReadableByteCount(
        bytes: Long,
        si: Boolean
    ): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp =
            (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre =
            (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
        return String.format(
            "%.1f %sB",
            bytes / Math.pow(unit.toDouble(), exp.toDouble()),
            pre
        )
    }

    data class Output(
        val diskUsageInBytes: Long,
        val humanReadableByteCount: String
    ): UseCase.Output()
}