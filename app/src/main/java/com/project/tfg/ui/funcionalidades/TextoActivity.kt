package com.project.tfg.ui.funcionalidades

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.project.tfg.R


class TextoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_texto)

        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.texto_dialog_cuerpo)
            .setCancelable(false)
            .setPositiveButton(R.string.texto_dialog_opcion_galeria) { dialog, id ->
                requestPermission()
            }
            .setNegativeButton(R.string.texto_dialog_opcion_camara) { dialog, id -> //  Action for 'NO' Button
                dialog.cancel()
                Toast.makeText(
                    applicationContext, "you choose no action for alertbox",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNeutralButton("Cancelar") { dialog, id ->
                finish()
            }
        val alert = builder.create()
        alert.setTitle(R.string.texto_dialog_title)
        alert.show()

        setContentView(R.layout.activity_texto)
    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            val imagenPlaceholder:ImageView = findViewById(R.id.imagePlaceholder)
            imagenPlaceholder.setImageURI(data)
            if (data != null) {
                galeria(data)
            }
        }
    }

    private fun pickPhotoFromGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        startForActivityGallery.launch(photoPickerIntent)
    }

    private fun galeria(data:Uri) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val imagen: InputImage = InputImage.fromFilePath(this, data)

        val result = recognizer.process(imagen)
            .addOnSuccessListener { visionText ->
                val text: TextView = findViewById(R.id.texto_resultado)
                text.setText(visionText.text)
                val c_layout: ConstraintLayout = findViewById(R.id.c_layout)

                var lines = 0
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        lines = lines + 1
                    }
                }

                val constraintSet = ConstraintSet()
                constraintSet.clone(c_layout)
                constraintSet.constrainHeight(R.id.texto_resultado, lines*45)
                constraintSet.applyTo(c_layout)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Fallo", Toast.LENGTH_SHORT).show()
            }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }

                else -> requestPremissionLauncher.launch(READ_EXTERNAL_STORAGE)
            }
        }else {
            pickPhotoFromGallery()
        }
    }

    private val requestPremissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted) {
            pickPhotoFromGallery()
        }else {
            Toast.makeText(this, "Se necesitan permisos", Toast.LENGTH_LONG).show()
        }
    }



}