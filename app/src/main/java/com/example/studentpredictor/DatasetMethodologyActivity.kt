package com.example.studentpredictor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studentpredictor.databinding.ActivityDatasetMethodologyBinding

class DatasetMethodologyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatasetMethodologyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatasetMethodologyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Dataset & Metodologi"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}