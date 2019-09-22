package es.juanavila.liverss.application.services

import java.io.File

interface FileService {
    fun searchInAppPrivateDir(fileName: String) : File
    fun searchInAppPrivateDir(subFolder: String,fileName: String) : File
}