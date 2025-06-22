package com.example.studentpredictor

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentpredictor.databinding.ActivityPredictionBinding // Import view binding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PredictionActivity : AppCompatActivity() {

    private var interpreter: Interpreter? = null

    private lateinit var binding: ActivityPredictionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupInputSpinners()
        loadTFLiteModel()
        setupPredictButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarPrediction)
        supportActionBar?.apply {
            title = "Prediksi Performa Siswa"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbarPrediction.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupInputSpinners() {
        val schoolAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.schools)
        )
        binding.spinnerSchool.setAdapter(schoolAdapter)

        binding.tilSpinnerSchool.setEndIconOnClickListener {
            binding.spinnerSchool.showDropDown()
        }

        binding.spinnerSchool.setOnClickListener {
            binding.spinnerSchool.showDropDown()
        }


        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.genders)
        )
        binding.spinnerGender.setAdapter(genderAdapter)

        binding.tilSpinnerGender.setEndIconOnClickListener {
            binding.spinnerGender.showDropDown()
        }

        binding.spinnerGender.setOnClickListener {
            binding.spinnerGender.showDropDown()
        }
    }

    private fun loadTFLiteModel() {
        try {
            interpreter = Interpreter(loadModelFile())
            Toast.makeText(this, "Model TFLite berhasil dimuat.", Toast.LENGTH_SHORT).show()
            binding.btnPredict.isEnabled = true
        } catch (e: IOException) {
            Toast.makeText(this, "Gagal memuat model TFLite: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            binding.btnPredict.isEnabled = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupPredictButton() {
        binding.btnPredict.setOnClickListener {
            val currentInterpreter = interpreter
            if (currentInterpreter == null) {
                Toast.makeText(this, "Model prediksi belum dimuat.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {

                val schoolText = binding.spinnerSchool.text.toString()
                val genderText = binding.spinnerGender.text.toString()


                val schoolValue = resources.getStringArray(R.array.schools).indexOf(schoolText).toFloat()
                val genderValue = resources.getStringArray(R.array.genders).indexOf(genderText).toFloat()


                if (schoolValue == -1f) {
                    binding.tilSpinnerSchool.error = "Mohon pilih sekolah"
                    Toast.makeText(this, "Mohon lengkapi semua input!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else {
                    binding.tilSpinnerSchool.error = null
                }

                if (genderValue == -1f) {
                    binding.tilSpinnerGender.error = "Mohon pilih jenis kelamin"
                    Toast.makeText(this, "Mohon lengkapi semua input!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else {
                    binding.tilSpinnerGender.error = null
                }


                val age = binding.inputAge.text.toString().toFloatOrNull()
                val study = binding.inputStudy.text.toString().toFloatOrNull()
                val failures = binding.inputFailures.text.toString().toFloatOrNull()
                val g1 = binding.inputG1.text.toString().toFloatOrNull()
                val g2 = binding.inputG2.text.toString().toFloatOrNull()
                val g3 = binding.inputG3.text.toString().toFloatOrNull()


                val numericInputs = listOf(
                    Pair(age, binding.tilInputAge),
                    Pair(study, binding.tilInputStudy),
                    Pair(failures, binding.tilInputFailures),
                    Pair(g1, binding.tilInputG1),
                    Pair(g2, binding.tilInputG2),
                    Pair(g3, binding.tilInputG3)
                )


                var allInputsValid = true
                numericInputs.forEach { (value, layout) ->
                    if (value == null) {
                        layout.error = "Input tidak boleh kosong"
                        allInputsValid = false
                    } else {
                        layout.error = null

                        when (layout.id) {
                            R.id.til_input_age -> if (value !in 15f..22f) { layout.error = "Usia antara 15-22"; allInputsValid = false }
                            R.id.til_input_study -> if (value !in 1f..4f) { layout.error = "Jam belajar 1-4"; allInputsValid = false }
                            R.id.til_input_g1, R.id.til_input_g2, R.id.til_input_g3 -> if (value !in 0f..20f) { layout.error = "Nilai antara 0-20"; allInputsValid = false }
                        }
                    }
                }

                if (!allInputsValid) {
                    Toast.makeText(this, "Mohon periksa kembali input Anda!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }



                val inputFeatures = floatArrayOf(
                    schoolValue,
                    genderValue,
                    age!!,
                    study!!,
                    failures!!,
                    g1!!,
                    g2!!,
                    g3!!
                )

                val inputBuffer = ByteBuffer.allocateDirect(4 * inputFeatures.size).order(ByteOrder.nativeOrder())
                inputFeatures.forEach { inputBuffer.putFloat(it) }
                val outputBuffer = ByteBuffer.allocateDirect(4 * 3).order(ByteOrder.nativeOrder())

                currentInterpreter.run(inputBuffer, outputBuffer)
                outputBuffer.rewind()
                val resultArray = FloatArray(3)
                outputBuffer.asFloatBuffer().get(resultArray)

                val maxIndex = resultArray.indices.maxByOrNull { resultArray[it] } ?: 0
                val labelNames = listOf("Rendah", "Sedang", "Tinggi")

                val probabilitiesText = resultArray.mapIndexed { index, value ->
                    "${labelNames[index]}: ${"%.2f".format(value * 100)}%"
                }.joinToString("\n")

                binding.textResultProbabilities.text = probabilitiesText
                binding.textResultFinal.text = "Prediksi akhir: ${labelNames[maxIndex]}"
                binding.textResultFinal.visibility = android.view.View.VISIBLE
                binding.textResultProbabilities.visibility = android.view.View.VISIBLE

            } catch (e: Exception) {
                binding.textResultProbabilities.text = ""
                binding.textResultFinal.text = "Error saat prediksi: ${e.message}"
                binding.textResultFinal.visibility = android.view.View.VISIBLE
                e.printStackTrace()
                Toast.makeText(this, "Terjadi kesalahan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        assets.openFd("student_model.tflite").use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    override fun onDestroy() {
        interpreter?.close()
        super.onDestroy()
    }
}