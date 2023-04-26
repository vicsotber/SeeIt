package com.project.tfg.ui.registros

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.project.tfg.R
import com.project.tfg.databinding.FragmentRegistrosBinding


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                eliminarDatosUsuario()
            }
        } else {
            // Si el usuario no está logueado, inflar el diseño 'fragment_registros'
            binding.root.removeAllViews()
            layoutInflater.inflate(R.layout.fragment_registros, binding.root)

            val accessBtn: View? = view?.findViewById(R.id.access_btn)
            accessBtn?.setOnClickListener {
                val emailPassword = Intent(this.context, EmailPasswordActivity::class.java)
                val LOGIN_REQUEST_CODE = 10000
                startActivityForResult(emailPassword, LOGIN_REQUEST_CODE)
            }
        }
    }

    private fun signOut()  {
        FirebaseAuth.getInstance().signOut()
        convertTextToSpeech(getString(R.string.logout_correct))
        updateViews()
    }

    private fun eliminarDatosUsuario() {
        val user = auth.currentUser
        val userUid = user?.uid
        user?.delete()
            ?.addOnSuccessListener {
                val ref = FirebaseDatabase.getInstance("https://seeit-4fe0d-default-rtdb.europe-west1.firebasedatabase.app/").getReference("$userUid")
                ref.removeValue()
                    .addOnSuccessListener {
                        convertTextToSpeech(getString(R.string.delete_correct))
                        updateViews()
                    }
                    .addOnFailureListener { error ->
                    }
            }
            ?.addOnFailureListener { e ->
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