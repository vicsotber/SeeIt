package com.project.tfg.ui.funcionalidades

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.mlkit.vision.label.ImageLabeler
import com.project.tfg.BuildConfig
import com.project.tfg.R
import com.project.tfg.ui.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

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
        val thread = Thread {
            try {
                //Prepara el cliente con la imagen a enviar en bytes
                val client = OkHttpClient()
                val mediaType = "application/octet-stream".toMediaTypeOrNull()
                val inputStream = contentResolver.openInputStream(data)
                val bytes = inputStream?.readBytes()
                val requestBody = bytes?.toRequestBody(mediaType)
                inputStream?.close()

                //Obtiene el lenguaje del dispositivo para hacer la petición en el idioma adecuado
                val currentLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resources.configuration.locales.get(0)
                } else {
                    resources.configuration.locale
                }
                val deviceLanguage: String = currentLocale.getLanguage()
                val url: String = if (deviceLanguage == "es") {
                    "https://seeit.cognitiveservices.azure.com/computervision/imageanalysis:analyze?api-version=2022-10-12-preview&features=Description,Objects,Tags&language=es"
                }else {
                    "https://seeit.cognitiveservices.azure.com/computervision/imageanalysis:analyze?api-version=2022-10-12-preview&features=Description,Objects,Tags&language=en"
                }

                //Obtiene la Api KEY de gradle.properties
                val apiKey: String = BuildConfig.AZURE_API_KEY

                //Configura la request y hace la petición
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody!!)
                    .addHeader("Ocp-Apim-Subscription-Key", apiKey)
                    .addHeader("Content-Type", "application/octet-stream")
                    .build()
                val response = client.newCall(request).execute()

                //Lee el JSON resultado y obtiene la descripción, objetos y etiquetas
                val gson = Gson()
                val jsonResponse = response.body?.string()
                val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)

                val description = jsonObject.getAsJsonObject("descriptionResult")
                    .getAsJsonArray("values").get(0).asJsonObject.get("text").asString
                val tags = jsonObject.getAsJsonObject("tagsResult")
                    .getAsJsonArray("values").toList()
                val tagsNames: ArrayList<String> = ArrayList<String>()
                for (i in tags.indices) {
                    tagsNames.add(tags[i].asJsonObject.get("name").asString)
                }
                val objects = jsonObject.getAsJsonObject("objectsResult")
                    .getAsJsonArray("values").toList()
                val objectsNames: ArrayList<String> = ArrayList<String>()
                for (i in objects.indices) {
                    objectsNames.add(tags[i].asJsonObject.get("name").asString)
                }

                //Presenta el resultado en pantalla
                runOnUiThread {
                    val text: TextView = findViewById(R.id.texto_resultado)
                    val descriptionMessage = getString(R.string.escena_resultado_descripcion)
                    val objectsMessage = getString(R.string.escena_resultado_objetos)
                    val tagsMessage = getString(R.string.escena_resultado_etiquetas)
                    val result:String = if (objectsNames.size != 0) {
                        "<b>$descriptionMessage</b> $description<br/><br/>" +
                                "<b>$objectsMessage</b> $objectsNames<br/><br/>" +
                                "<b>$tagsMessage</b> $tagsNames"

                    }else {
                        "<b>$descriptionMessage</b> $description<br/><br/>" +
                                "<b>$tagsMessage</b> $tagsNames"
                    }
                    val formattedResult = HtmlCompat.fromHtml(result,
                        HtmlCompat.FROM_HTML_MODE_LEGACY)
                    text.text = formattedResult
                    convertTextToSpeech(formattedResult.toString())
                }

            } catch (e: Exception) {
                val text: TextView = findViewById(R.id.texto_resultado)
                runOnUiThread {
                    text.text = getString(R.string.scene_description_error)
                }
                convertTextToSpeech(getString(R.string.scene_description_error))
            }
        }

        thread.start()


        //Versión prototipo que obtiene las etiquetas de la imagen usando MLKit y una descripción mediante OpenAI

//        val imagen: InputImage = InputImage.fromFilePath(this, data)
         /*labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

         labeler.process(imagen)
             .addOnSuccessListener { labels ->
                 val text: TextView = findViewById(R.id.texto_resultado)
                 val labelsName: MutableList<String> = mutableListOf()
                 for (label in labels) {
                     labelsName.add(label.text)
                 }

                 text.setText(labelsName.toString())

                 convertTextToSpeech(labelsName.toString())

                 val thread = Thread {
                     try {
                         val service = OpenAiService(OpenAI API KEY)
                         val completionRequest = CompletionRequest.builder()
                             .prompt("Describe la imagen usando las siguientes etiquetas: " + labelsName.toString())
                             .model("text-davinci-003")
                             .maxTokens(4000)
                             .build()
                         val descripcion = service.createCompletion(completionRequest).choices.get(0).text
                         runOnUiThread {
                             text.setText(descripcion)
                         }

                     } catch (e: Exception) {
                         e.printStackTrace()
                     }
                 }

                 thread.start()
             }
             .addOnFailureListener { _ ->
                 // Task failed with an exception
                 // ...
             }*/
    }
}