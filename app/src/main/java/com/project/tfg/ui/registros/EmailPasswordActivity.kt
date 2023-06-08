package com.project.tfg.ui.registros

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.tfg.R
import org.jetbrains.annotations.TestOnly

class EmailPasswordActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var textToSpeech: TextToSpeech

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        // Initialize Firebase Auth
        auth = Firebase.auth
        if (auth.currentUser != null) {
            finish()
        }

        setContentView(R.layout.email_password_activity)

        convertTextToSpeech(getString(R.string.login_register_advice))

        val createAccountBtn: Button = findViewById(R.id.register_btn)
        val signInBtn: Button = findViewById(R.id.login_btn)

        signInBtn.setOnClickListener {
            val email: String = findViewById<EditText>(R.id.email_input).text.toString()
            val password: String = findViewById<EditText>(R.id.password_input).text.toString()
            signIn(email, password)
        }

        createAccountBtn.setOnClickListener {
            val email: String = findViewById<EditText>(R.id.email_input).text.toString()
            val password: String = findViewById<EditText>(R.id.password_input).text.toString()
            createAccount(email, password)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
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


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            finish()
        }
    }

    fun createAccount(email: String, password: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }

        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        finish()
                    }
                } else {
                    val passwordError = findViewById<TextView>(R.id.password_input_error)
                    if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.") {
                        passwordError.setText(getString(R.string.email_in_use))
                        convertTextToSpeech(getString(R.string.email_in_use))
                    }else {
                        passwordError.setText(getString(R.string.register_error))
                        convertTextToSpeech(getString(R.string.register_error))
                    }
                }
            }
    }

    fun signIn(email: String, password: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }

        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        finish()
                    }
                }else {
                    val passwordError = findViewById<TextView>(R.id.password_input_error)
                    if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.") {
                        passwordError.setText(getString(R.string.password_incorrect))
                        convertTextToSpeech(getString(R.string.password_incorrect))
                    } else if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.") {
                        passwordError.setText(getString(R.string.email_not_exists))
                        convertTextToSpeech(getString(R.string.email_not_exists))
                    } else {
                        passwordError.setText(getString(R.string.login_error))
                        convertTextToSpeech(getString(R.string.login_error))
                    }
                }
            }
    }

    fun validateForm(): Boolean {
        var valid = true

        val email: String = findViewById<EditText>(R.id.email_input).text.toString()
        val password: String = findViewById<EditText>(R.id.password_input).text.toString()
        val emailError = findViewById<TextView>(R.id.email_input_error)
        val passwordError = findViewById<TextView>(R.id.password_input_error)

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (TextUtils.isEmpty(email)) {
            emailError.setText(getString(R.string.email_empty))
            valid = false
        } else if (!email.matches(emailPattern.toRegex())) {
            emailError.setText(getString(R.string.email_not_valid))
            valid = false
        } else {
            emailError.setText("")
        }

        if (TextUtils.isEmpty(password)) {
            passwordError.setText(getString(R.string.password_empty))
            valid = false
        } else if (password.length < 6) {
            passwordError.setText(getString(R.string.password_too_short))
            valid = false
        } else {
            passwordError.setText("")
        }

        if (!valid) {
            convertTextToSpeech(getString(R.string.error_advice))
        }

        return valid
    }

    fun convertTextToSpeech(text: String) {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // El TextToSpeech se inicializó correctamente, ahora se puede llamar al método speak()
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                // Hubo un error al inicializar el TextToSpeech
            }
        }
    }
}