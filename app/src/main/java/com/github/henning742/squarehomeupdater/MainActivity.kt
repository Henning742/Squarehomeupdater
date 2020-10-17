package com.github.henning742.squarehomeupdater

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.documentfile.provider.DocumentFile
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.lang.StringBuilder

const val FOLDERPICKER_CODE = 1903

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//            val intent = Intent(this, FolderPicker::class.java)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                // Provide read access to files and sub-directories in the user-selected
                // directory.
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, FOLDERPICKER_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == FOLDERPICKER_CODE && resultCode == Activity.RESULT_OK) {
            intent?.data?.also { uri ->             // Perform operations on the document using its URI.
//                Snackbar.make(findViewById(android.R.id.content), uri.toString(), Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()

                val backupFolder = DocumentFile.fromTreeUri(this, uri)
                val subFolders = backupFolder?.listFiles()?.sortedByDescending { it.lastModified() }

                for (f in subFolders!!) {
                    if (f.name!!.takeLast(1)[0].isDigit()) {
                        Log.i("asdfg", f.name.toString())
//                        Log.i("asdfg", Date(f.lastModified()).toString())
//                        // copy directory and modify files.
//                        // should check if new folder exists?
//                        Log.i("asdfg", "Found the folder.")

                        val newDirUrl = FileHelperKt.createDirectory(
                            contentResolver,
                            backupFolder.uri,
                            f.name.toString() + "_modified"
                        )
                        FileHelperKt.copyFolder(this, contentResolver, f, newDirUrl!!)

                        val layouts = mutableListOf<JsonArray<JsonObject>>()
                        val folders1 = mutableListOf<List<String>>()
                        val folders2 = mutableListOf<List<String>>()
                        for (ff in f.listFiles()) {
                            if (ff.isDirectory) {
                                if (ff.name == "folders") {
                                    for (jsfile in ff.listFiles()) {
                                        val jso =
                                            JsonUtil.readJsonFromDocument(contentResolver, jsfile)

//                                        val str = (jso as JsonArray<JsonObject>)[0]["t"] as String
//                                        val parser: Parser = Parser.default()
//                                        val obj =
//                                            (parser.parse(StringBuilder(str)) as JsonObject)["c"] as String
                                        val f1 = mutableListOf<String>()
                                        val f2 = mutableListOf<String>()

                                        for (str in ((jso as JsonObject) ["i"] as JsonArray<*>).filterIsInstance<String>())
                                        {
                                            f1.add(str.split("/")[0])
                                            f2.add(str.split("/")[1])
                                        }

                                        folders1.add(f1)
                                        folders2.add(f2)
                                    }

                                } else if (ff.name == "layout") {
                                    for (jsfile in ff.listFiles()) {

                                        layouts.add(
                                            (JsonUtil.readJsonFromDocument(
                                                contentResolver,
                                                jsfile
                                            ) as JsonArray<JsonObject>)
                                        )
                                    }
                                }
                            }
                        }

                        // process the layouts.
                        for (jsArray in layouts) {
                            try {
                                val d = jsArray[0]["t"] as String
                            } catch (e: Exception) {
                                continue
                            }

                            var maxCol = 0
                            var maxCount = 0
                            val names = mutableListOf<String>()
                            // assume no entry appears in more than one folder.
                            for (entry in jsArray)
                            {
                                val count = entry["O"] as Int
                                val col = entry["X"] as Int
                                names.add(((Parser.default().parse(
                                    StringBuilder(
                                        entry["t"] as String
                                    )
                                ) as JsonObject)["c"] as String).split("/")[0])

                                maxCol = if (col > maxCol) col else maxCol
                                maxCount = if (count > maxCount) count else maxCount
                            }

                            val cnt = folders1.map{
                                it.intersect(names)
                            }

                            Log.i("adsag", cnt.toString())


                        }


                        break
                    }
                }

//                val path = FileUtil.getFullPathFromTreeUri(uri, this)
//
//                try {
//                    FileHelper.CopyFolder(path+"/.backup_3", "_modified")
//                }
//                catch (e: Exception) {
//                    // handler
//                    Snackbar.make(findViewById(android.R.id.content), e.localizedMessage, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show()
//                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}