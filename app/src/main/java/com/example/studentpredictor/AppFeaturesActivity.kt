package com.example.studentpredictor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studentpredictor.databinding.ActivityAppFeaturesBinding

class AppFeaturesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppFeaturesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Fitur Aplikasi"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}