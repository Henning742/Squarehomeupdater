package com.github.henning742.squarehomeupdater

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import com.beust.klaxon.*
import java.io.IOException

class JsonUtil {
    companion object {
        fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
            tailrec fun String.collectIndexesOf(offset: Int = 0, indexes: List<Int> = emptyList()): List<Int> =
                when (val index = indexOf(substr, offset, ignoreCase)) {
                    -1 -> indexes
                    else -> collectIndexesOf(index + substr.length, indexes + index)
                }

            return when (this) {
                null -> emptyList()
                else -> collectIndexesOf()
            }
        }

        fun readJsonFromDocument(contentResolver: ContentResolver, doc: DocumentFile, trim_json: Boolean = false): JsonBase {




            val parser: Parser = Parser.default()
            val inFile = contentResolver.openInputStream(doc.uri)
            var contentStr = inFile!!.bufferedReader().use { it.readText() }
            if (trim_json)
            {
                val c1 = contentStr.count{it=='['}
                val c2 = contentStr.count{it==']'}
                val ind = contentStr.indexesOf(']'.toString()).reversed()[c2-c1]
                contentStr = contentStr.take(ind + 1)
            }
            val ret = parser.parse(StringBuilder(contentStr)) as JsonBase

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