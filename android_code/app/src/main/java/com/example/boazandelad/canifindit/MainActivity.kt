package com.example.boazandelad.canifindit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.boazandelad.canifindit.MainActivity.Companion.SERVERPORT
import com.example.boazandelad.canifindit.MainActivity.Companion.SERVER_URL
import java.lang.Exception
import khttp.post
import android.os.StrictMode
import khttp.extensions.fileLike
import kotlinx.coroutines.*
import java.io.*
import java.nio.charset.Charset
import java.util.*
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.EditText
import khttp.responses.Response
import java.net.*


fun getRealPathFromURI(uri: Uri, context: Context): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor.moveToFirst()
    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
    return cursor.getString(idx)
}

class MainActivity : AppCompatActivity() {

    private var imageView: ImageView? = null

    companion object {
        const val PICK_IMAGE: Int = 100
        val SERVERPORT = 5000
        val SERVER_URL = "https://www.floydlabs.com/serve/nyenyu/projects/objectdetector"
        val REQUEST_MEMORY_ACCESS = 101
        var path: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)*/

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            findViewById<TextView>(R.id.image_desc).text = "Premission not granted immediately"
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_MEMORY_ACCESS
            )
        }

        imageView = findViewById(R.id.pic_view)

    }

    fun openGallery(view: View) {
        var gallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            var imageUri = data?.data
            imageView?.setImageURI(imageUri)
            if (imageUri != null) {
                path = getRealPathFromURI(imageUri, baseContext)
            }
            findViewById<TextView>(R.id.image_desc).text = "Image not processed yet"+ path
        }
    }


    fun sendImage(view: View) {

        try {
            var response: Response? = null
            findViewById<TextView>(R.id.image_desc).text = "Connecting to " + path;

            try {
                val job = GlobalScope.launch(Dispatchers.Main) {
                    val postOperation = async(Dispatchers.IO) { // <- extension on launch scope, launched in IO dispatcher
                        // blocking I/O operation
                        Log.d("io coroutine",File(path).exists().toString())
                        post(SERVER_URL, files = listOf(File(path).fileLike(name = "Image.jpg")))/*"http://10.0.0.1:5000"*/
                    }
                    response = postOperation.await() // wait for result of I/O operation without blocking the main thread
                    findViewById<TextView>(R.id.image_desc).text = "Image contains: ${response?.text}"
                }
            } catch (e: Exception) {
                findViewById<TextView>(R.id.image_desc).text = "Connection failed - please check fields are valid"
                findViewById<TextView>(R.id.image_desc).text = e.toString()
            }

        } catch (e: UnknownHostException) {
            findViewById<TextView>(R.id.image_desc).text = "Unknown host :("
            e.printStackTrace()
        } catch (e: IOException) {
            findViewById<TextView>(R.id.image_desc).text = "IO exceptiion :("
            e.printStackTrace()
        } catch (e: Exception) {
            findViewById<TextView>(R.id.image_desc).text = "Other exception :("
            e.printStackTrace()
        }
    }
}
