package com.project.tfg.ui.registros

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.project.tfg.R
import com.project.tfg.databinding.FragmentRegistrosBinding
import com.project.tfg.ui.funcionalidades.CategoriaEspecificaActivity
import com.project.tfg.ui.funcionalidades.EscenaActivity
import com.project.tfg.ui.funcionalidades.TextoActivity
import com.project.tfg.ui.funcionalidades.TraducirActivity


class RegistrosFragment : Fragment() {

    private var _binding: FragmentRegistrosBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateViews()
    }

    override fun onResume() {
        super.onResume()
        updateViews()
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        _binding = null
    }

    private val startForActivityRecognition = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        updateViews()
    }

    private fun updateViews() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Si el usuario está logueado, inflar el diseño 'registros'
            binding.root.removeAllViews()
            layoutInflater.inflate(R.layout.registros, binding.root)

            val logoutBtn: View? = view?.findViewById(R.id.logout_btn)
            logoutBtn?.setOnClickListener {
                signOut()
            }

            val deleteBtn: View? = view?.findViewById(R.id.delete_account_btn)
            deleteBtn?.setOnClickListener {
                deleteUserData()
            }

            loadRecords()

        } else {
            // Si el usuario no está logueado, inflar el diseño 'fragment_registros'
            binding.root.removeAllViews()
            layoutInflater.inflate(R.layout.fragment_registros, binding.root)

            val accessBtn: View? = view?.findViewById(R.id.access_btn)
            accessBtn?.setOnClickListener {
                val emailPassword = Intent(this.context, EmailPasswordActivity::class.java)
                startForActivityRecognition.launch(emailPassword)
            }
        }
    }

    private fun signOut()  {
        FirebaseAuth.getInstance().signOut()
        convertTextToSpeech(getString(R.string.logout_correct))
        updateViews()
    }

    private fun deleteUserData() {
        val user = auth.currentUser
        val userUid = user?.uid
        val storageRef = FirebaseStorage.getInstance("gs://seeit-4fe0d.appspot.com/").getReference("$userUid")
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                // Obtiene una lista de las subcarpetas del usuario
                val subfolders = listResult.prefixes

                // Recorre cada subcarpeta
                subfolders.forEach { subfolderRef ->
                    // Utiliza el método listAll() para obtener la lista de imágenes dentro de la subcarpeta
                    subfolderRef.listAll()
                        .addOnSuccessListener { subfolderListResult ->
                            // Obtiene una lista de las imágenes dentro de la subcarpeta
                            val images = subfolderListResult.items

                            // Elimina cada imagen de forma iterativa
                            images.forEach { imageRef ->
                                imageRef.delete()
                            }
                        }
                }

                //Elimina los datos de la RealtimeDatabase
                val ref = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/").getReference("$userUid")
                ref.removeValue()
                    .addOnSuccessListener {
                        //Elimina al usuario
                        user?.delete()
                            ?.addOnSuccessListener {
                                convertTextToSpeech(getString(R.string.delete_correct))
                                updateViews()
                            }
                            ?.addOnFailureListener { e ->
                            }
                    }
                    .addOnFailureListener { e ->
                    }
            }
            .addOnFailureListener { e ->
                // Ocurrió un error al obtener las referencias a las imágenes del usuario
            }
    }

    private fun loadRecords() {
        val gridView = view?.findViewById<GridLayout>(R.id.image_grid)
        gridView?.removeAllViews()
        val user = auth.currentUser
        val userUid = user?.uid

        val database = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/")
        var ref = database.getReference("$userUid/texto")
        readDatabase(ref, "Texto")

        ref = database.getReference("$userUid/escena")
        readDatabase(ref, "Escena")

        ref = database.getReference("$userUid/traducir")
        readDatabase(ref, "Traducir")

        ref = database.getReference("$userUid/categorias")
        readDatabase(ref, "Categorias")
    }

    private fun readDatabase(ref: DatabaseReference, tipo: String) {
        val gridView = view?.findViewById<GridLayout>(R.id.image_grid)

        // Listener a la referencia para obtener los datos de cada registro
        ref.get().addOnSuccessListener { snapshot ->
            // Iterar a través de todos los registros y agregar vistas de imagen al contenedor
            for (registroSnapshot in snapshot.children) {
                // Obtener la URL de la imagen del registro actual
                val imageUrl = registroSnapshot.child("image_url").value.toString()
                val imageView = ImageView(requireContext())
                val params = GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                )
                params.setMargins(8, 8, 8, 8)
                imageView.layoutParams = params
                val desiredWidth = resources.getDimensionPixelSize(R.dimen.image_width)
                val desiredHeight = resources.getDimensionPixelSize(R.dimen.image_height)
                Glide.with(requireContext())
                    .load(imageUrl)
                    .override(desiredWidth, desiredHeight)
                    .into(imageView)
                gridView?.columnCount = 3
                gridView?.addView(imageView)

                when (tipo) {
                    "Escena" -> {
                        imageView.setOnClickListener {
                            val intent = Intent(requireContext(), EscenaActivity::class.java)
                            intent.putExtra("IMAGE_URL", imageUrl)
                            intent.putExtra("TEXT_RESULT", registroSnapshot.child("text_result").value.toString())
                            startActivity(intent)
                        }
                    }
                    "Texto" -> {
                        imageView.setOnClickListener {
                            val intent = Intent(requireContext(), TextoActivity::class.java)
                            intent.putExtra("IMAGE_URL", imageUrl)
                            intent.putExtra("TEXT_RESULT", registroSnapshot.child("text_result").value.toString())
                            startActivity(intent)
                        }
                    }
                    "Traducir" -> {
                        imageView.setOnClickListener {
                            val intent = Intent(requireContext(), TraducirActivity::class.java)
                            intent.putExtra("IMAGE_URL", imageUrl)
                            intent.putExtra("TEXT_RECOGNIZED", registroSnapshot.child("text_recognized").value.toString())
                            intent.putExtra("TEXT_TRANSLATED", registroSnapshot.child("text_translated").value.toString())
                            startActivity(intent)
                        }
                    }
                    else -> {
                        imageView.setOnClickListener {
                            val intent = Intent(requireContext(), CategoriaEspecificaActivity::class.java)
                            intent.putExtra("IMAGE_URL", imageUrl)
                            intent.putExtra("TEXT_RESULT", registroSnapshot.child("text_result").value.toString())
                            startActivity(intent)
                        }
                    }
                }
            }
        }.addOnFailureListener{ exception ->
            Log.e("firebase", "Error getting data", exception)
        }
    }

    private fun convertTextToSpeech(text: String) {
        textToSpeech = TextToSpeech(this.context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // El TextToSpeech se inicializó correctamente, ahora se puede llamar al método speak()
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                // Hubo un error al inicializar el TextToSpeech
            }
        }
    }
}