package com.project.tfg.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.project.tfg.R
import com.project.tfg.ui.funcionalidades.CamaraActivity

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var textToSpeech: TextToSpeech
    protected var isSharingImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isSharingImage) {
            val dialogUtils = DialogUtils()
            dialogUtils.showAlertDialog(this,
                getString(R.string.texto_dialog_title),
                getString(R.string.texto_dialog_cuerpo),
                { _, _ ->
                    requestPermissionGallery()
                },
                { _, _ ->
                    requestPermissionCamera()
                },
                { _, _ ->
                    finish()
                }
            )
        }

        setContentView(R.layout.functionality_result)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    private fun requestPermissionGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }

                else -> requestPremissionGalleryLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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
            val text: TextView = findViewById(R.id.texto_resultado)
            text.text = getString(R.string.permisos_no_concedidos)
            convertTextToSpeech(getString(R.string.permisos_no_concedidos))
        }
    }

    private fun requestPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    takePhotoWithCamera()
                }

                else -> requestPremissionCameraLauncher.launch(Manifest.permission.CAMERA)
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
            val text: TextView = findViewById(R.id.texto_resultado)
            text.text = getString(R.string.permisos_no_concedidos)
            convertTextToSpeech(getString(R.string.permisos_no_concedidos))
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

    private val startForActivityRecognition = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            if (data != null) {
                val imagenPlaceholder: ImageView = findViewById(R.id.imagePlaceholder)
                imagenPlaceholder.setImageURI(data)
                functionality(data)
            }
        }
    }

    abstract fun functionality(data: Uri)

    fun convertTextToSpeech(text: String) {
        textToSpeech = TextToSpeech(this, { status ->
            if (status == TextToSpeech.SUCCESS) {
                // El TextToSpeech se inicializó correctamente, ahora se puede llamar al método speak()
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                // Hubo un error al inicializar el TextToSpeech
            }
        })
    }

}