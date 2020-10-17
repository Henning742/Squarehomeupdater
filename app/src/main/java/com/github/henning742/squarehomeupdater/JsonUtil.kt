package com.github.henning742.squarehomeupdater

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import com.beust.klaxon.*
import java.io.IOException

class JsonUtil {
    companion object {
        fun readJsonFromDocument(contentResolver: ContentResolver, doc: DocumentFile): JsonBase {
            val parser: Parser = Parser.default()
            val inFile = contentResolver.openInputStream(doc.uri)
            val ret = parser.parse(inFile!!) as JsonBase

            inFile.close()
            return ret
        }

        fun writeJsonToDocument(
            contentResolver: ContentResolver,
            input: String,
            doc: DocumentFile
        ) {
            val out = contentResolver.openOutputStream(doc.uri)

            if (!doc.canWrite()) {
                throw IOException("Can't not write on the file.")
            }
            out!!.write(input.toByteArray())
            out.flush()
            out.close()
        }
    }
}