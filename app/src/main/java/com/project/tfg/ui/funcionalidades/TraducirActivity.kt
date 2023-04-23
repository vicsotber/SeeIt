package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
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
                    val imagenPlaceholder: ImageView = findViewById(R.id.imagePlaceholder)
                    imagenPlaceholder.setImageURI(imageUri)
                    functionality(imageUri)
                }
            }
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
            translateText(data)
        }
    }

    private fun translateText(data: Uri) {
        val imagen: InputImage = InputImage.fromFilePath(this, data)

        val spinnerSource = findViewById<Spinner>(R.id.spinner_source_language)
        val sourceLanguage = spinnerSource.selectedItem as String

        val spinnerTarget = findViewById<Spinner>(R.id.spinner_target_language)
        val targetLanguage = spinnerTarget.selectedItem as String

        //Realiza el reconocimiento de texto
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(imagen)
            .addOnSuccessListener { visionText ->
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
                        translator.translate(visionText.text)
                            .addOnSuccessListener { translatedText ->
                                traduccionResultado.setText(translatedText)
                                val targetLocale = Locale(getLanguageCode(targetLanguage))
                                textToSpeech.setLanguage(targetLocale)
                                textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                            .addOnFailureListener { exception ->
                                traduccionResultado.text = getString(R.string.text_translation_error)
                                convertTextToSpeech(getString(R.string.text_translation_error))
                            }
                    }
                    .addOnFailureListener { exception ->
                        traduccionResultado.text = getString(R.string.text_translation_error)
                        convertTextToSpeech(getString(R.string.text_translation_error))
                    }

            }
            .addOnFailureListener { _ ->
                traduccionResultado.text = getString(R.string.text_recognition_error)
                convertTextToSpeech(getString(R.string.text_recognition_error))
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

}