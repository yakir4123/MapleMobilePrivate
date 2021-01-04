package com.bapplications.maplemobile.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.databinding.ActivityDownloadBinding
import com.bapplications.maplemobile.ui.adapters.DownloaderAdapter
import com.bapplications.maplemobile.ui.etc.FileDownloader
import com.bapplications.maplemobile.ui.view_models.DownloadActivityViewModel
import java.io.File

val TAG = "DownloadManager"


class DownloadActivity : AppCompatActivity() {

    private val viewModel: DownloadActivityViewModel by viewModels()

    private lateinit var bgm: MediaPlayer

    val adapter = DownloaderAdapter()

    val downloadingState = object : DownloadingState {
        override fun onDownloadFinished(fileDownloader: FileDownloader) {
            viewModel.files.value!!.remove(fileDownloader)
            if (viewModel.files.value!!.size == 0) {
                startGameActivity()
            }
        }
    }

    fun startGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.CACHE_DIRECTORY = cacheDir.absolutePath
        Configuration.WZ_DIRECTORY = getExternalFilesDir(null)?.absolutePath

        viewModel.files.value = mutableListOf()
        viewModel.wifiConnection.value = isWifiAvailable()

        bgm = MediaPlayer.create(this, R.raw.download_bgm).apply { isLooping = true }
        bgm.setOnPreparedListener({ mp -> mp.start() })
        createNotificationChannel()
        setUpView()

        viewModel.files.observe(this, Observer {
            it.let {
                adapter.data = it
            }
        })
        downloadFiles()
    }

    override fun onResume() {
        super.onResume()
        bgm.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        bgm.pause()
    }

    private fun continueDownload() {
        viewModel.wifiConnection.value = isWifiAvailable()

         if (!viewModel.wifiConnection.value!!) {
             Toast.makeText(this, "Not connected to wifi.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.files.value!!.forEach {
            if (!it.downloading) {
                it.startDownload()
            }
        }
    }

    private fun downloadFiles() {
        viewModel.wifiConnection.value = isWifiAvailable()

        if (!viewModel.wifiConnection.value!!) {
            Toast.makeText(this, "Not connected to wifi.", Toast.LENGTH_SHORT).show()
        }

        val files = arrayOf(
                "Map.nx",
                "Mob.nx",
                "Npc.nx",
                "Item.nx",
                "Sound.nx",
                "String.nx",
                "Character.nx",
        )

        files.forEach {
            viewModel.files.postValue(viewModel.files.value?.apply {
                if (!File(Configuration.WZ_DIRECTORY, it).exists()) {
                    val fileDownloader = FileDownloader(it, downloadingState)
                    this.add(fileDownloader)

                    if (viewModel.wifiConnection.value!!) {
                        fileDownloader.startDownload()
                    }
                }
            })
        }
        if (viewModel.files.value!!.size == 0) {
            startGameActivity()
        }
    }

    override fun onPause() {
        bgm.pause()
        if (viewModel.files.value!!.size != 0)
            createNotification()
        super.onPause()
    }

    private fun setUpView() {
        val binding: ActivityDownloadBinding = ActivityDownloadBinding.inflate(layoutInflater)

        setContentView(binding.rootLayout)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // background animation
        backgroundAnimation(binding.backgroundIv)
        binding.downloaderRecycler.layoutManager = LinearLayoutManager(this)
        binding.downloaderRecycler.adapter = adapter
        binding.connectWifiBackground.setOnClickListener { continueDownload() }
    }


    private fun backgroundAnimation(backgroundIv: ImageView) {
        val fadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out).apply { startOffset = 10000 }
        val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        backgroundIv.startAnimation(fadeOut)

        val backgroundsDrawableResources = listOf(R.drawable.wallpaper1,
                R.drawable.wallpaper2,
                R.drawable.wallpaper3,
                R.drawable.wallpaper4,
                R.drawable.wallpaper5,
                R.drawable.wallpaper6)
        val randomWallpaper = { current: Int ->
            var drawable: Int
            do {
                drawable = backgroundsDrawableResources.random()
            } while (drawable == current)
            drawable
        }
        var currentBackground = backgroundsDrawableResources.random()
        backgroundIv.setImageDrawable(ContextCompat.getDrawable(this, currentBackground))
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                currentBackground = randomWallpaper(currentBackground)
                backgroundIv.setImageDrawable(ContextCompat.getDrawable(this@DownloadActivity, currentBackground))
                backgroundIv.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                backgroundIv.startAnimation(fadeOut)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }


    private fun createNotification() {
        val intent = Intent(this, DownloadActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MapleMobile")
                .setContentText("Downloading files..")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isWifiAvailable(): Boolean {
        var result = false
        val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false

            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}

