package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import java.io.File

interface DownloadCompleted {

    @Throws(Exception::class)
    fun downloadCompleted(file: File)
}