package com.project.tfg.ui.funcionalidades

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.project.tfg.R
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CamaraActivity : AppCompatActivity() {
    private var cameraProvider: ProcessCameraProvider? = null

    private lateinit var imageCapture: ImageCapture
    private lateinit var viewFinder: PreviewView
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)

        viewFinder = findViewById(R.id.viewFinder)

        // Crear el directorio de salida para las fotos
        outputDirectory = getOutputDirectory()

        // Crear el executor para la cámara
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Initialize the camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // CameraProvider is now guaranteed to be available
            cameraProvider = cameraProviderFuture.get()

            // Configurar la cámara
            startCamera()
        }, ContextCompat.getMainExecutor(this))

        val btnHacerFoto: Button = findViewById(R.id.button)
        btnHacerFoto.setOnClickListener {
            takePhoto()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }

    private fun startCamera() {
        // Obtener el proveedor de la cámara
        val cameraProvider = cameraProvider ?: return

        // Crear una instancia de Preview
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

        // Crear una instancia de ImageCapture
        imageCapture = ImageCapture.Builder()
            .build()

        // Unir la instancia de Preview y ImageCapture a la cámara
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        } catch (exc: Exception) {
            Log.e(TAG, "Error al unir la cámara", exc)
        }
    }

    private fun takePhoto() {
        // Crear un archivo para almacenar la imagen
        val photoFile = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

        // Crear un objeto OutputFileOptions que especifica la ubicación de la imagen
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Tomar la foto utilizando la instancia de ImageCapture
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Devolver la URI de la imagen a la actividad principal
                    val resultIntent = Intent()
                    resultIntent.data = Uri.fromFile(photoFile)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    // Mostrar un mensaje de error al usuario
                    Log.e(TAG, "Error al guardar la imagen", exception)
                }
            }
        )
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    companion object {
        private const val TAG = "CameraActivity"
    }
}