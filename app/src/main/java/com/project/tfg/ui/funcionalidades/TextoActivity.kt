package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.project.tfg.R
import com.project.tfg.ui.BaseActivity


class TextoActivity : BaseActivity() {
    private lateinit var recognizer: TextRecognizer
    override fun onCreate(savedInstanceState: Bundle?) {
        isSharingImage = intent?.action == Intent.ACTION_SEND
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_texto)

        val intent = intent
        val action = intent.action
        val type = intent.type
        if (Intent.ACTION_SEND == action && type != null) {
            if (type.startsWith("image/")) {
                val imageUri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri?
                if (imageUri != null) {
                    val imagenPlaceholder: ImageView = findViewById(R.id.imagePlaceholder)
                    imagenPlaceholder.setImageURI(imageUri)
                    functionality(imageUri)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun functionality(data:Uri) {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val imagen: InputImage = InputImage.fromFilePath(this, data)

        //Utiliza el TextRecognizer de MLKit y muestra el texto en pantalla
        recognizer.process(imagen)
            .addOnSuccessListener { visionText ->
                val text: TextView = findViewById(R.id.texto_resultado)
                text.setText(visionText.text)

                convertTextToSpeech(visionText.text)
                guardarRegistro(data, visionText)

            }
            .addOnFailureListener { _ ->
                val text: TextView = findViewById(R.id.texto_resultado)
                text.text = getString(R.string.text_recognition_error)
                convertTextToSpeech(getString(R.string.text_recognition_error))
            }
    }

    private fun guardarRegistro(uri: Uri, visionText: Text) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid != null) {
            val database = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/")
            val ref = database.getReference("$userUid/texto")
            val data = HashMap<String, String>()
            data["image_url"] = uri.toString()
            data["text_result"] = visionText.text
            Log.d("DB", database.toString())
            Log.d("DB", uri.toString())
            val newRef = ref.push()
            newRef.setValue(data)
        }
    }

}