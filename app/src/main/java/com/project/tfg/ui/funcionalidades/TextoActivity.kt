package com.project.tfg.ui.funcionalidades

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.project.tfg.R


class TextoActivity : AppCompatActivity() {
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var recognizer: TextRecognizer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_texto)

        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.texto_dialog_cuerpo)
            .setCancelable(false)
            .setPositiveButton(R.string.texto_dialog_opcion_galeria) { dialog, id ->
                requestPermissionGallery()
            }
            .setNegativeButton(R.string.texto_dialog_opcion_camara) { dialog, id -> //  Action for 'NO' Button
                requestPermissionCamera()
            }
            .setNeutralButton(R.string.texto_dialog_opcion_cancelar) { dialog, id ->
                finish()
            }
        val alert = builder.create()
        alert.setTitle(R.string.texto_dialog_title)
        alert.show()

        setContentView(R.layout.activity_texto)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    private fun requestPermissionGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }

                else -> requestPremissionGalleryLauncher.launch(READ_EXTERNAL_STORAGE)
            }
        }else {
            pickPhotoFromGallery()
        }
    }

    private val requestPremissionGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted) {
            pickPhotoFromGallery()
        }else {
            Toast.makeText(this, R.string.permisos_no_concedidos, Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    takePhotoWithCamera()
                }

                else -> requestPremissionCameraLauncher.launch(CAMERA)
            }
        }else {
            takePhotoWithCamera()
        }
    }

    private val requestPremissionCameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted) {
            takePhotoWithCamera()
        }else {
            Toast.makeText(this, R.string.permisos_no_concedidos, Toast.LENGTH_LONG).show()
        }
    }

    private val startForActivityRecognition = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            val imagenPlaceholder:ImageView = findViewById(R.id.imagePlaceholder)
            imagenPlaceholder.setImageURI(data)
            if (data != null) {
                textRecognition(data)
            }
        }
    }


    private fun takePhotoWithCamera() {
        val photoCameraIntent = Intent(this, CamaraActivity::class.java)
        startForActivityRecognition.launch(photoCameraIntent)
    }

    private fun pickPhotoFromGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        startForActivityRecognition.launch(photoPickerIntent)
    }

    private fun textRecognition(data:Uri) {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val imagen: InputImage = InputImage.fromFilePath(this, data)

        val result = recognizer.process(imagen)
            .addOnSuccessListener { visionText ->
                val text: TextView = findViewById(R.id.texto_resultado)
                text.setText(visionText.text)

                val utteranceProgressListener = object : UtteranceProgressListener() {
                    override fun onDone(utteranceId: String?) {
                        textToSpeech.stop()
                        textToSpeech.shutdown()
                    }

                    override fun onError(utteranceId: String?) {
                        textToSpeech.stop()
                        textToSpeech.shutdown()
                    }

                    override fun onStart(utteranceId: String?) {
                        // El TextToSpeech empezó a leer el texto
                    }
                }

                textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        // El TextToSpeech se inicializó correctamente, ahora se puede llamar al método speak()
                        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener)
                        textToSpeech.speak(visionText.text, TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        // Hubo un error al inicializar el TextToSpeech
                        // Maneja el error
                    }
                })

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, R.string.text_recognition_error, Toast.LENGTH_SHORT).show()
            }

    }

}