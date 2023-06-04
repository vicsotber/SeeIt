package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
        if(intent.getStringExtra("IMAGE_URL") != null) {
            isSharingImage = true
        }
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
        }else if(intent.getStringExtra("IMAGE_URL") != null) {
            val imageUrl = intent.getStringExtra("IMAGE_URL")
            val textResult = intent.getStringExtra("TEXT_RESULT")
            loadRecord(imageUrl, textResult)
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
                saveRecord(data, visionText)

            }
            .addOnFailureListener {
                val text: TextView = findViewById(R.id.texto_resultado)
                text.text = getString(R.string.text_recognition_error)
                convertTextToSpeech(getString(R.string.text_recognition_error))
            }
    }

    private fun saveRecord(uri: Uri, visionText: Text) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance("gs://seeit-4fe0d.appspot.com/").getReference("$userUid/texto/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // La imagen se ha subido exitosamente a Firebase Storage
                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { uri ->
                    // Obtiene la URL de descarga de la imagen
                    val imageUrl = uri.toString()

                    // Guarda la URL de la imagen en Firebase Realtime Database
                    val database = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/")
                    val ref = database.getReference("$userUid/texto")
                    val data = HashMap<String, String>()
                    data["image_url"] = imageUrl
                    data["text_result"] = visionText.text
                    val newRef = ref.push()
                    newRef.setValue(data)
                }
                    .addOnFailureListener { exception ->
                        // Ocurrió un error al obtener la URL de descarga de la imagen
                    }
            }
                .addOnFailureListener { exception ->
                    // Ocurrió un error al subir la imagen a Firebase Storage
                }

        }
    }

    private fun loadRecord(imageUrl: String?, textResult: String?) {
        val imagenPlaceholder: ImageView = findViewById(R.id.imagePlaceholder)
        Glide.with(this).load(imageUrl).into(imagenPlaceholder)
        val text: TextView = findViewById(R.id.texto_resultado)
        text.setText(textResult)

        if (textResult != null) {
            convertTextToSpeech(textResult)
        }
    }

}