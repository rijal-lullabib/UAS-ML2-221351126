package com.example.studentpredictor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studentpredictor.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigationButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarMenu)
        supportActionBar?.title = "Student Predictor" // Judul aplikasi utama
    }

    private fun setupNavigationButtons() {
        binding.btnGoToPrediction.setOnClickListener {
            startActivity(Intent(this, PredictionActivity::class.java))
        }

        binding.btnGoToFeatures.setOnClickListener {
            startActivity(Intent(this, AppFeaturesActivity::class.java))
        }

        binding.btnGoToDataset.setOnClickListener {
            startActivity(Intent(this, DatasetMethodologyActivity::class.java))
        }

        binding.btnGoToAbout.setOnClickListener {
            startActivity(Intent(this, AboutAppActivity::class.java))
        }
    }
}