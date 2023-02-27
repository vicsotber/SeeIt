package com.project.tfg.ui.funcionalidades

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.project.tfg.R
import com.project.tfg.ui.BaseActivity

class EscenaActivity : BaseActivity() {
    private lateinit var labeler: ImageLabeler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_escenas)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::labeler.isInitialized) {
            labeler.close()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::labeler.isInitialized) {
            labeler.close()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::labeler.isInitialized) {
            labeler.close()
        }
    }

    override fun functionality(data: Uri) {
        val imagen: InputImage = InputImage.fromFilePath(this, data)

        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(imagen)
            .addOnSuccessListener { labels ->
                val text: TextView = findViewById(R.id.texto_resultado)
                var labelsName: MutableList<String> = mutableListOf()
                for (label in labels) {
                    labelsName.add(label.text)
                }

                text.setText(labelsName.toString())

                convertTextToSpeech(labelsName.toString())

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }
}