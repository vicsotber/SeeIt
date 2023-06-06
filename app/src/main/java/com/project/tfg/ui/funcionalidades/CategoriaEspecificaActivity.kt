package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import com.project.tfg.R
import com.project.tfg.ui.BaseActivity

open class CategoriaEspecificaActivity : BaseActivity() {
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
                    val imagenPlaceholder: ImageView = findViewById(R.id.image_placeholder)
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
        categoryRecognitionMethod(data, "")
    }

    open fun categoryRecognitionMethod(data: Uri, tfliteFileName: String) {
        val localModel = LocalModel.Builder()
            .setAssetFilePath(tfliteFileName)
            .build()

        val customImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0.5f)
            .setMaxResultCount(5)
            .build()
        labeler = ImageLabeling.getClient(customImageLabelerOptions)

        val imageBitmap: Bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data))
        val normalizedImage = normalizeBitmap(imageBitmap)

        val imagen: InputImage = InputImage.fromBitmap(normalizedImage, 0)

        labeler.process(imagen)
            .addOnSuccessListener { labels ->
                val text: TextView = findViewById(R.id.texto_resultado)

                if(labels.size == 0) {
                    text.text = getString(R.string.object_unknown)
                    convertTextToSpeech(getString(R.string.object_unknown))
                    saveRecord(data, getString(R.string.object_unknown))
                }else {
                    showCategoryResult(data, labels)
                }
            }
            .addOnFailureListener { e ->
                val text: TextView = findViewById(R.id.texto_resultado)
                text.text = getString(R.string.category_labelling_error)
                convertTextToSpeech(getString(R.string.category_labelling_error))
            }
    }

    private fun normalizeBitmap(bitmap: Bitmap): Bitmap {
        val INPUT_IMAGE_WIDTH = 224
        val INPUT_IMAGE_HEIGHT = 224
        val normalizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_IMAGE_WIDTH, INPUT_IMAGE_HEIGHT, true)
        // Normalize pixel values between 0 and 1
        val normalizedImage = normalizedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val pixels = IntArray(INPUT_IMAGE_WIDTH * INPUT_IMAGE_HEIGHT)
        normalizedImage.getPixels(pixels, 0, INPUT_IMAGE_WIDTH, 0, 0, INPUT_IMAGE_WIDTH, INPUT_IMAGE_HEIGHT)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val red = Color.red(pixel) / 255.0f
            val green = Color.green(pixel) / 255.0f
            val blue = Color.blue(pixel) / 255.0f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pixels[i] = Color.rgb(red, green, blue)
            }
        }

        normalizedImage.setPixels(pixels, 0, INPUT_IMAGE_WIDTH, 0, 0, INPUT_IMAGE_WIDTH, INPUT_IMAGE_HEIGHT)
        return normalizedImage
    }

    private fun showCategoryResult(uri: Uri, labels: List<ImageLabel>) {
        val text: TextView = findViewById(R.id.texto_resultado)
        val objectDetected = labels[0].text
        val confidence = (labels[0].confidence * 100)
        val result:String =
            "<b>${getString(R.string.category_result_object)}</b> $objectDetected<br/><br/>" +
                    "<b>${getString(R.string.category_result_confidence)}</b> $confidence%<br/><br/>"
        val formattedResult = HtmlCompat.fromHtml(result,
            HtmlCompat.FROM_HTML_MODE_COMPACT)
        text.text = formattedResult
        convertTextToSpeech(formattedResult.toString())

        saveRecord(uri, result)
    }

    private fun saveRecord(uri: Uri, visionText: String) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance("gs://seeit-4fe0d.appspot.com/").getReference("$userUid/categorias/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // La imagen se ha subido exitosamente a Firebase Storage
                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { uri ->
                    // Obtiene la URL de descarga de la imagen
                    val imageUrl = uri.toString()

                    // Guarda la URL de la imagen en Firebase Realtime Database
                    val database = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/")
                    val ref = database.getReference("$userUid/categorias")
                    val data = HashMap<String, String>()
                    data["image_url"] = imageUrl
                    data["text_result"] = visionText
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
        val imagenPlaceholder: ImageView = findViewById(R.id.image_placeholder)
        Glide.with(this).load(imageUrl).into(imagenPlaceholder)
        val text: TextView = findViewById(R.id.texto_resultado)
        val formattedResult = HtmlCompat.fromHtml(textResult!!, HtmlCompat.FROM_HTML_MODE_COMPACT)
        text.text = formattedResult
        convertTextToSpeech(formattedResult.toString())
    }
}