package com.github.henning742.squarehomeupdater

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import lib.folderpicker.FolderPicker
import java.io.IOException

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
                Snackbar.make(findViewById(android.R.id.content), uri.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()


                try {
                    FileHelper.CopyFolder(uri.toString(), "_modified")
                }
                catch (e: IOException) {
                    // handler
                    Snackbar.make(findViewById(android.R.id.content), e.localizedMessage, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
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