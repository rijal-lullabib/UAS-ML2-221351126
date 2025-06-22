package com.example.studentpredictor

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem // Untuk menangani item menu jika ditambahkan
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.studentpredictor.databinding.ActivityAboutAppBinding // Import view binding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar // Untuk notifikasi sederhana

class AboutAppActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAboutAppBinding

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayAppInfo()
    }

    private fun setupToolbar() {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Tentang Aplikasi"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun displayAppInfo() {
        try {
            val packageName = packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.longVersionCode


            binding.tvAppVersion.text = "Versi: $versionName (Build $versionCode)"
            binding.tvDeveloperInfo.text = "Dikembangkan oleh: Tim [Nama Tim Anda]"

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            binding.tvAppVersion.text = "Versi: N/A"
            binding.tvDeveloperInfo.text = "Dikembangkan oleh: N/A"
        }
    }
}