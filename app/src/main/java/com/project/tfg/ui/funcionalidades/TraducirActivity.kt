package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.project.tfg.R
import com.project.tfg.ui.BaseActivity
import java.util.*


class TraducirActivity : BaseActivity() {
    private lateinit var recognizer: TextRecognizer
    private lateinit var translator: Translator
    private lateinit var traduccionResultado: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        isSharingImage = intent?.action == Intent.ACTION_SEND
        if(intent.getStringExtra("IMAGE_URL") != null) {
            isSharingImage = true
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traducir)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_traduccion)

        traduccionResultado = findViewById(R.id.texto_resultado)

        val intent = intent
        val action = intent.action
        val type = intent.type
        if (Intent.ACTION_SEND == action && type != null) {
            if (type.startsWith("image/")) {
                val imageUri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri?
                if (imageUri != null) {
                    val imagenPlaceholder: ImageView = findViewById(R.id.image_placeholder)
                    imagenPlaceholder.setImageURI(imageUri)
                    functionality(imageUri)
                }
            }
        }else if(intent.getStringExtra("IMAGE_URL") != null) {
            val imageUrl = intent.getStringExtra("IMAGE_URL")
            val textRecognized = intent.getStringExtra("TEXT_RECOGNIZED")
            val textTranslated = intent.getStringExtra("TEXT_TRANSLATED")
            loadRecord(imageUrl, textRecognized, textTranslated)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::translator.isInitialized) {
            translator.close()
        }
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::translator.isInitialized) {
            translator.close()
        }
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::translator.isInitialized) {
            translator.close()
        }
        if (::recognizer.isInitialized) {
            recognizer.close()
        }
    }

    override fun functionality(data: Uri) {
        val button = findViewById<Button>(R.id.boton_traducir)
        button.setOnClickListener {
            val waiting = getString(R.string.traduccion_esperando)
            traduccionResultado.setText(waiting)
            convertTextToSpeech(waiting)
            recognizeText(data)
        }
    }


    private fun recognizeText(data: Uri) {
        val imagen: InputImage = InputImage.fromFilePath(this, data)

        //Realiza el reconocimiento de texto
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(imagen)
            .addOnSuccessListener { visionText ->
                downloadTranslationModelsIfNeeded(data, visionText)
            }
            .addOnFailureListener {
                traduccionResultado.text = getString(R.string.text_recognition_error)
                convertTextToSpeech(getString(R.string.text_recognition_error))
            }
    }


    private fun downloadTranslationModelsIfNeeded(data:Uri, visionText: Text) {
        val spinnerSource = findViewById<Spinner>(R.id.spinner_source_language)
        val sourceLanguage = spinnerSource.selectedItem as String

        val spinnerTarget = findViewById<Spinner>(R.id.spinner_target_language)
        val targetLanguage = spinnerTarget.selectedItem as String

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(getLanguageCode(sourceLanguage))
            .setTargetLanguage(getLanguageCode(targetLanguage))
            .build()
        translator = Translation.getClient(options)

        //Descarga los modelos de traducción si es necesario a través de wifi
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                //Realiza la traducción del texto
                translateText(data, visionText)
            }
            .addOnFailureListener { exception ->
                traduccionResultado.text = getString(R.string.text_translation_error)
                convertTextToSpeech(getString(R.string.text_translation_error))
            }
    }

    private fun getLanguageCode(language: String): String {
        return when (language) {
            "Inglés", "English" -> TranslateLanguage.ENGLISH
            "Francés", "French" -> TranslateLanguage.FRENCH
            "Alemán", "German" -> TranslateLanguage.GERMAN
            "Español", "Spanish" -> TranslateLanguage.SPANISH
            "Gallego", "Galician" -> TranslateLanguage.GALICIAN
            "Catalán", "Catalan" -> TranslateLanguage.CATALAN
            "Italiano", "Italian" -> TranslateLanguage.ITALIAN
            "Neerlandés", "Dutch" -> TranslateLanguage.DUTCH
            "Árabe", "Arabic" -> TranslateLanguage.ARABIC
            "Checo", "Czech" -> TranslateLanguage.CZECH
            "Portugués", "Portuguese" -> TranslateLanguage.PORTUGUESE

            else -> TranslateLanguage.ENGLISH // Por defecto, utiliza inglés
        }
    }

    private fun translateText(data:Uri, visionText: Text) {
        val spinnerTarget = findViewById<Spinner>(R.id.spinner_target_language)
        val targetLanguage = spinnerTarget.selectedItem as String

        translator.translate(visionText.text)
            .addOnSuccessListener { translatedText ->
                traduccionResultado.setText(translatedText)
                val targetLocale = Locale(getLanguageCode(targetLanguage))
                textToSpeech.setLanguage(targetLocale)
                textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null)

                saveRecord(data, visionText, translatedText)
            }
            .addOnFailureListener { exception ->
                traduccionResultado.text = getString(R.string.text_translation_error)
                convertTextToSpeech(getString(R.string.text_translation_error))
            }
    }

    private fun saveRecord(uri: Uri, visionText: Text, translatedText: String) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance("gs://seeit-4fe0d.appspot.com/").getReference("$userUid/traducir/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // La imagen se ha subido exitosamente a Firebase Storage
                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { uri ->
                    // Obtiene la URL de descarga de la imagen
                    val imageUrl = uri.toString()

                    // Guarda la URL de la imagen en Firebase Realtime Database
                    val database = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/")
                    val ref = database.getReference("$userUid/traducir")
                    val data = HashMap<String, String>()
                    data["image_url"] = imageUrl
                    data["text_recognized"] = visionText.text
                    data["text_translated"] = translatedText
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

    private fun loadRecord(imageUrl: String?, textRecognized: String?, textTranslated: String?) {
        //Primero quita todos los elementos en pantalla que no son necesarios
        val spinnerTargetLanguage = findViewById<Spinner>(R.id.spinner_target_language)
        val spinnerSourceLanguage = findViewById<Spinner>(R.id.spinner_source_language)
        val buttonTraducir = findViewById<Button>(R.id.boton_traducir)
        val sourceLanguageText = findViewById<TextView>(R.id.source_language)
        val targetLanguageText = findViewById<TextView>(R.id.target_language)
        listOf(spinnerTargetLanguage, spinnerSourceLanguage, sourceLanguageText, targetLanguageText, buttonTraducir)
            .forEach { view ->
                view.visibility = View.GONE
            }

        //Posteriormente muestra la imagen, el texto reconocido en la imagen, y el texto traducido
        val imagenPlaceholder: ImageView = findViewById(R.id.image_placeholder)
        Glide.with(this).load(imageUrl).into(imagenPlaceholder)
        val text: TextView = findViewById(R.id.texto_resultado)

        val recognizedTextMessage = getString(R.string.texto_reconocido)
        val translatedTextMessage = getString(R.string.texto_traducido)
        val result:String = "<b>$recognizedTextMessage: </b> $textRecognized<br/><br/>" +
                    "<b>$translatedTextMessage: </b> $textTranslated<br/><br/>"
        val formattedResult = HtmlCompat.fromHtml(result, HtmlCompat.FROM_HTML_MODE_COMPACT)
        text.text = formattedResult
        convertTextToSpeech(formattedResult.toString())
    }

}