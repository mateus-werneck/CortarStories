package com.example.cortarstories

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.*
import android.content.Intent
import android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.lang.System.out
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import java.nio.file.Paths.get


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val permission = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")
        requestPermissions(permission, 1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<FloatingActionButton>(R.id.select).setOnClickListener {
            if (checkSelfPermission("READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {requestPermissions(permission, 2)}
            val intent = Intent()
                    .setType("video/*")
                    .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Selecione um Video"), 111)

        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //val appDir = getDir("InstagramCut", Context.MODE_PRIVATE)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialogTitle)
        builder.setMessage(R.string.dialogMessage)
        builder.setIcon(android.R.drawable.ic_popup_sync)
        builder.setPositiveButton("OK"){ dialogInterface, which -> Toast.makeText(applicationContext, "Processando", Toast.LENGTH_SHORT).show()}
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)

        val builder2 = AlertDialog.Builder(this)
        builder2.setTitle(R.string.dialogTitle2)
        builder2.setMessage(R.string.dialogMessage2)
        builder2.setIcon(android.R.drawable.ic_dialog_info)
        builder2.setPositiveButton("OK"){ dialogInterface, which -> Toast.makeText(applicationContext, "Pronto", Toast.LENGTH_LONG).show()}
        val alertDialog2: AlertDialog = builder2.create()
        alertDialog2.setCancelable(false)

        val builder3 = AlertDialog.Builder(this)
        builder3.setTitle(R.string.dialogTitle3)
        builder3.setMessage(R.string.dialogMessage3)
        builder3.setIcon(android.R.drawable.ic_dialog_info)
        builder3.setPositiveButton("OK"){ dialogInterface, which -> Toast.makeText(applicationContext, "Tente Novamente", Toast.LENGTH_LONG).show()}
        val alertDialog3: AlertDialog = builder3.create()
        alertDialog3.setCancelable(false)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val input = data?.data!!
            var source: InputStream = applicationContext.contentResolver.openInputStream(input)!!
            val storiesDir = getDir("CortarStories", MODE_PRIVATE)
            var outPath = Uri.fromFile(File("$storiesDir/stories.mp4"))
            var destination: OutputStream = applicationContext.contentResolver.openOutputStream(outPath)!!
            alertDialog.show()

            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            val progressBar2 = findViewById<ProgressBar>(R.id.progressBar2)
            progressBar.visibility = View.VISIBLE
            progressBar2.visibility = View.VISIBLE
            var Snack: View = findViewById(android.R.id.content)

            var notification = NotificationCompat.Builder(this, "CortarStories")
                .setSmallIcon(R.mipmap.cortar_stories_round)
                .setContentText("Processando...")
                .setProgress(100, 0, true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            var notification2 = NotificationCompat.Builder(this, "CortarStories")
                .setSmallIcon(R.mipmap.cortar_stories_round)
                .setContentText("Pronto")
                .setProgress(100, 100, false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("CortarStories", name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            notificationManager.notify(103, notification.build())
            }


            val thread = Thread(Runnable {
                source.copyTo(destination)
                source.close()
                destination.close()
                var path = "$storiesDir/stories.mp4"
                var code = Stories(path, progressBar, Snack, progressBar2)
                runOnUiThread {
                    if (code == 1){
                    alertDialog.hide()
                    alertDialog2.show()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val name = getString(R.string.channel_name)
                            val descriptionText = getString(R.string.channel_description)
                            val importance = NotificationManager.IMPORTANCE_DEFAULT
                            val channel = NotificationChannel("CortarStories", name, importance).apply {
                                description = descriptionText
                            }
                            val notificationManager: NotificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.createNotificationChannel(channel)
                            notificationManager.cancel(103)
                            notificationManager.notify(104, notification2.build())
                        }
                    }else if (code == 0){alertDialog.hide()
                        alertDialog3.show()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val name = getString(R.string.channel_name)
                            val descriptionText = getString(R.string.channel_description)
                            val importance = NotificationManager.IMPORTANCE_DEFAULT
                            val channel = NotificationChannel("CortarStories", name, importance).apply {
                                description = descriptionText
                            }
                            val notificationManager: NotificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.createNotificationChannel(channel)
                            notificationManager.cancel(103)}
                    }
                }
            })

            thread.start()

        }


    }
}


