package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.mlkit.vision.label.ImageLabeler
import com.project.tfg.R
import com.project.tfg.ui.BaseActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*


class EscenaActivity : BaseActivity() {
    private lateinit var labeler: ImageLabeler
    override fun onCreate(savedInstanceState: Bundle?) {
        isSharingImage = intent?.action == Intent.ACTION_SEND
        if(intent.getStringExtra("IMAGE_URL") != null) {
            isSharingImage = true
        }
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_escenas)

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


        //VERSION 2.0: HACE USO DE MICROSOFT AZURE COGNITIVE SERVICES
        /*val thread = Thread {
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

        thread.start()*/

        /*val thread = Thread {
            try {
                //Prepara el cliente con la imagen a enviar en bytes
                val client = OkHttpClient()

                //Obtiene el lenguaje del dispositivo para hacer la petición en el idioma adecuado
                val currentLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resources.configuration.locales.get(0)
                } else {
                    resources.configuration.locale
                }
                val deviceLanguage: String = currentLocale.getLanguage()
                val url: String = if (deviceLanguage == "es") {
                    "https://see-it-proxy-api.vercel.app/analizar_imagen?language=es"
                }else {
                    "https://see-it-proxy-api.vercel.app/analizar_imagen?language=en"
                }

                val inputStream = contentResolver.openInputStream(data)
                val bytes = inputStream?.readBytes()
                val mediaType = "image/*".toMediaTypeOrNull()
                val requestBody = bytes?.let {
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("imagen", "image.jpg", it.toRequestBody(mediaType))
                        .build()
                }
                inputStream?.close()

                //Configura la request y hace la petición
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody!!)
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
                        HtmlCompat.FROM_HTML_MODE_COMPACT)
                    text.text = formattedResult
                    convertTextToSpeech(formattedResult.toString())

                    guardarRegistro(data, result)
                }

            } catch (e: Exception) {
                val text: TextView = findViewById(R.id.texto_resultado)
                runOnUiThread {
                    text.text = getString(R.string.scene_description_error)
                }
                convertTextToSpeech(getString(R.string.scene_description_error))
            }
        }

        thread.start()*/
         */

        val thread = Thread {
            try {
                //Obtener descripción de Microsoft Azure Cognititve Services
                obtainDescription(data)

            } catch (e: Exception) {
                val text: TextView = findViewById(R.id.texto_resultado)
                runOnUiThread {
                    text.text = getString(R.string.scene_description_error)
                }
                convertTextToSpeech(getString(R.string.scene_description_error))
            }
        }

        thread.start()
    }

    private fun obtainDescription(data: Uri) {
        //Prepara el cliente con la imagen a enviar en bytes
        val client = OkHttpClient()

        //Obtiene el lenguaje del dispositivo para hacer la petición en el idioma adecuado
        val currentLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }

        //Elige la URL de la que obtendrá los datos según el idioma del dispositivo
        val deviceLanguage: String = currentLocale.getLanguage()
        val url: String = if (deviceLanguage == "es") {
            "https://see-it-proxy-api.vercel.app/analizar_imagen?language=es"
        }else {
            "https://see-it-proxy-api.vercel.app/analizar_imagen?language=en"
        }

        val inputStream = contentResolver.openInputStream(data)
        val bytes = inputStream?.readBytes()
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestBody = bytes?.let {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imagen", "image.jpg", it.toRequestBody(mediaType))
                .build()
        }
        inputStream?.close()

        //Configura la request y hace la petición
        val request = Request.Builder()
            .url(url)
            .post(requestBody!!)
            .build()
        val response = client.newCall(request).execute()
        readResponse(data, response)
    }

    private fun readResponse(data: Uri, response: Response) {
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

        //Muestra el resultado en pantalla
        showResult(data, objectsNames, tagsNames, description)
    }

    private fun showResult(data: Uri, objectsNames: ArrayList<String>, tagsNames: ArrayList<String>, description: String) {
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
                HtmlCompat.FROM_HTML_MODE_COMPACT)
            text.text = formattedResult
            convertTextToSpeech(formattedResult.toString())

            saveRecord(data, result)
        }
    }

    private fun saveRecord(uri: Uri, result: String) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance("gs://seeit-4fe0d.appspot.com/").getReference("$userUid/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // La imagen se ha subido exitosamente a Firebase Storage
                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { uri ->
                    // Obtiene la URL de descarga de la imagen
                    val imageUrl = uri.toString()

                    // Guarda la URL de la imagen en Firebase Realtime Database
                    val database = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/")
                    val ref = database.getReference("$userUid/escena")
                    val data = HashMap<String, String>()
                    data["image_url"] = imageUrl
                    data["text_result"] = result
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
        val formattedResult = HtmlCompat.fromHtml(textResult!!, HtmlCompat.FROM_HTML_MODE_COMPACT)
        text.text = formattedResult
        convertTextToSpeech(formattedResult.toString())
    }
}