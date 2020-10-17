package com.github.henning742.squarehomeupdater

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile

class FileHelperKt {
    companion object {
        fun copyFolder(
            context: Context,
            contentResolver: ContentResolver,
            src: DocumentFile,
            dstUri: Uri
        ) {
//        val createdDir =

            for (f in src.listFiles()) {
                if (f.isDirectory) {
                    val createDir = createDirectory(contentResolver, dstUri, f.name.toString())

                    copyFolder(context, contentResolver, f, createDir!!)
                } else {
                    val newFile = DocumentFile.fromTreeUri(context, dstUri)
                        ?.createFile("application/octet-stream", f.name.toString())
                    val out = contentResolver.openOutputStream(newFile!!.uri)
                    val inFile = contentResolver.openInputStream(f.uri)

                    val buffer = ByteArray(1024)
                    var read: Int
                    while (inFile!!.read(buffer).also { read = it } != -1) {
                        out!!.write(buffer, 0, read)
                    }

                }
            }
        }

        fun createDirectory(
            contentResolver: ContentResolver,
            parentFolder: Uri,
            name: String
        ): Uri? {
            return DocumentsContract.createDocument(
                contentResolver,
                parentFolder,
                "vnd.android.document/directory",
                name
            )
        }
    }
}