package com.project.tfg

import android.speech.tts.TextToSpeech
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nhaarman.mockitokotlin2.whenever
import com.project.tfg.ui.registros.EmailPasswordActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class EmailPasswordActivityTest3 {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailPasswordActivity: EmailPasswordActivity

    @Before
    fun setup() {
        emailPasswordActivity = Robolectric.buildActivity(EmailPasswordActivity::class.java).create().get()
        FirebaseApp.initializeApp(emailPasswordActivity.applicationContext)
        auth = FirebaseAuth.getInstance()
        emailPasswordActivity.textToSpeech = Mockito.mock(TextToSpeech::class.java)
    }

    @After
    fun tearDown() {
        // Hacer cualquier limpieza necesaria después de cada prueba
    }

    @Test
    fun createAccount_validEmailAndPassword_success() {
        // Configuración de la prueba
        val email = "test@example.com"
        val password = "password123"

        // Ejecución de la función a probar
        emailPasswordActivity.createAccount(email, password)

        // Verificación de los resultados esperados
        Mockito.verify(auth).createUserWithEmailAndPassword(email, password)
        Mockito.verify(auth.currentUser).let { Mockito.verify(emailPasswordActivity).finish() }
    }

    @Test
    fun createAccount_invalidEmailOrPassword_returnWithoutFinishing() {
        // Configuración de la prueba
        val email = "invalid_email"
        val password = "password"

        // Ejecución de la función a probar
        emailPasswordActivity.createAccount(email, password)

        // Verificación de los resultados esperados
        Mockito.verify(auth, Mockito.never()).createUserWithEmailAndPassword(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString()
        )
        Mockito.verify(emailPasswordActivity, Mockito.never()).finish()
    }

    @Test
    fun createAccount_emailInUse_showEmailInUseError() {
        // Configuración de la prueba
        val email = "test@example.com"
        val password = "password123"
        val task = Mockito.mock(Task::class.java)
        val exception = FirebaseAuthUserCollisionException("PRUEBA","Error")

        // Ejecución de la función a probar
        emailPasswordActivity.createAccount(email, password)

        // Verificación de los resultados esperados
        val passwordError = Mockito.mock(TextView::class.java)
        Mockito.verify(passwordError).setText("Email in use")
        Mockito.verify(emailPasswordActivity).convertTextToSpeech("Email in use")
    }

    @Test
    fun signIn_validEmailAndPassword_success() {
        // Configuración de la prueba
        val email = "test_registros@test.com"
        val password = "Test1234@Test"

        // Ejecución de la función a probar
        emailPasswordActivity.signIn(email, password)

        // Verificación de los resultados esperados
        //Mockito.verify(auth).signInWithEmailAndPassword(email, password)
        assertEquals("test_registros@test.com", emailPasswordActivity.auth.currentUser?.email)
    }

    /*@Test
    fun signIn_invalidEmailOrPassword_returnWithoutFinishing() {
        // Configuración de la prueba
        val email = "invalid_email"
        val password = "password"

        // Ejecución de la función a probar
        yourClass.signIn(email, password)

        // Verificación de los resultados esperados
        Mockito.verify(auth, Mockito.never()).signInWithEmailAndPassword(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString()
        )
        Mockito.verify(yourClass, Mockito.never()).finish()
    }

    @Test
    fun signIn_invalidCredentials_showPasswordIncorrectError() {
        // Configuración de la prueba
        val email = "test@example.com"
        val password = "password123"
        val task = Mockito.mock(Task::class.java)
        val exception = FirebaseAuthInvalidCredentialsException("Error")

        Mockito.`when`(auth.signInWithEmailAndPassword(email, password))
            .thenReturn(task)
        Mockito.`when`(task.isSuccessful).thenReturn(false)
        Mockito.`when`(task.exception).thenReturn(exception)
        Mockito.`when`(yourClass.getString(R.string.password_incorrect)).thenReturn("Password incorrect")

        // Ejecución de la función a probar
        yourClass.signIn(email, password)

        // Verificación de los resultados esperados
        val passwordError = Mockito.mock(TextView::class.java)
        Mockito.verify(passwordError).setText("Password incorrect")
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech("Password incorrect")
    }

    @Test
    fun signIn_invalidUser_showEmailNotExistsError() {
        // Configuración de la prueba
        val email = "test@example.com"
        val password = "password123"
        val task = Mockito.mock(Task::class.java)
        val exception = FirebaseAuthInvalidUserException("Error")

        Mockito.`when`(auth.signInWithEmailAndPassword(email, password))
            .thenReturn(task)
        Mockito.`when`(task.isSuccessful).thenReturn(false)
        Mockito.`when`(task.exception).thenReturn(exception)
        Mockito.`when`(yourClass.getString(R.string.email_not_exists)).thenReturn("Email not exists")

        // Ejecución de la función a probar
        yourClass.signIn(email, password)

        // Verificación de los resultados esperados
        val passwordError = Mockito.mock(TextView::class.java)
        Mockito.verify(passwordError).setText("Email not exists")
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech("Email not exists")
    }

    @Test
    fun signIn_unknownError_showLoginError() {
        // Configuración de la prueba
        val email = "test@example.com"
        val password = "password123"
        val task = Mockito.mock(Task::class.java)
        val exception = Exception("Unknown error")

        Mockito.`when`(auth.signInWithEmailAndPassword(email, password))
            .thenReturn(task)
        Mockito.`when`(task.isSuccessful).thenReturn(false)
        Mockito.`when`(task.exception).thenReturn(exception)
        Mockito.`when`(yourClass.getString(R.string.login_error)).thenReturn("Login error")

        // Ejecución de la función a probar
        yourClass.signIn(email, password)

        // Verificación de los resultados esperados
        val passwordError = Mockito.mock(TextView::class.java)
        Mockito.verify(passwordError).setText("Login error")
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech("Login error")
    }

    @Test
    fun validateForm_validEmailAndPassword_returnTrue() {
        // Configuración de la prueba
        val email = "test@example.com"
        val password = "password123"
        val editTextEmail = Mockito.mock(EditText::class.java)
        val editTextPassword = Mockito.mock(EditText::class.java)
        val emailError = Mockito.mock(TextView::class.java)
        val passwordError = Mockito.mock(TextView::class.java)
        val errorAdvice = "Error advice"

        Mockito.`when`(editTextEmail.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextEmail.text.toString()).thenReturn(email)
        Mockito.`when`(editTextPassword.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextPassword.text.toString()).thenReturn(password)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.email_input)).thenReturn(editTextEmail)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.password_input)).thenReturn(editTextPassword)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.email_input_error)).thenReturn(emailError)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.password_input_error)).thenReturn(passwordError)
        Mockito.`when`(yourClass.getString(R.string.error_advice)).thenReturn(errorAdvice)

        // Ejecución de la función a probar
        val result = yourClass.validateForm()

        // Verificación de los resultados esperados
        assertTrue(result)
        Mockito.verify(emailError).setText("")
        Mockito.verify(passwordError).setText("")
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech(errorAdvice)
    }

    @Test
    fun validateForm_invalidEmail_returnFalseAndShowEmailErrors() {
        // Configuración de la prueba
        val email = "invalid_email"
        val password = "password123"
        val editTextEmail = Mockito.mock(EditText::class.java)
        val editTextPassword = Mockito.mock(EditText::class.java)
        val emailError = Mockito.mock(TextView::class.java)
        val passwordError = Mockito.mock(TextView::class.java)
        val emailEmptyError = "Email empty"
        val emailNotValidError = "Email not valid"
        val errorAdvice = "Error advice"

        Mockito.`when`(editTextEmail.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextEmail.text.toString()).thenReturn(email)
        Mockito.`when`(editTextPassword.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextPassword.text.toString()).thenReturn(password)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.email_input)).thenReturn(editTextEmail)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.password_input)).thenReturn(editTextPassword)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.email_input_error)).thenReturn(emailError)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.password_input_error)).thenReturn(passwordError)
        Mockito.`when`(yourClass.getString(R.string.email_empty)).thenReturn(emailEmptyError)
        Mockito.`when`(yourClass.getString(R.string.email_not_valid)).thenReturn(emailNotValidError)
        Mockito.`when`(yourClass.getString(R.string.error_advice)).thenReturn(errorAdvice)

        // Ejecución de la función a probar
        val result = yourClass.validateForm()

        // Verificación de los resultados esperados
        assertFalse(result)
        Mockito.verify(emailError).setText(emailNotValidError)
        Mockito.verify(passwordError, Mockito.never()).setText(ArgumentMatchers.anyString())
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech(errorAdvice)
    }

    @Test
    fun validateForm_invalidPassword_returnFalseAndShowPasswordErrors() {
        // Configuración de la prueba
        val email = "test@example.com"
        val password = "123"
        val editTextEmail = Mockito.mock(EditText::class.java)
        val editTextPassword = Mockito.mock(EditText::class.java)
        val emailError = Mockito.mock(TextView::class.java)
        val passwordError = Mockito.mock(TextView::class.java)
        val passwordEmptyError = "Password empty"
        val passwordTooShortError = "Password too short"
        val errorAdvice = "Error advice"

        Mockito.`when`(editTextEmail.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextEmail.text.toString()).thenReturn(email)
        Mockito.`when`(editTextPassword.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextPassword.text.toString()).thenReturn(password)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.email_input)).thenReturn(editTextEmail)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.password_input)).thenReturn(editTextPassword)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.email_input_error)).thenReturn(emailError)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.password_input_error)).thenReturn(passwordError)
        Mockito.`when`(yourClass.getString(R.string.password_empty)).thenReturn(passwordEmptyError)
        Mockito.`when`(yourClass.getString(R.string.password_too_short)).thenReturn(passwordTooShortError)
        Mockito.`when`(yourClass.getString(R.string.error_advice)).thenReturn(errorAdvice)

        // Ejecución de la función a probar
        val result = yourClass.validateForm()

        // Verificación de los resultados esperados
        assertFalse(result)
        Mockito.verify(emailError, Mockito.never()).setText(ArgumentMatchers.anyString())
        Mockito.verify(passwordError).setText(passwordTooShortError)
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech(errorAdvice)
    }

    @Test
    fun validateForm_invalidEmailAndPassword_returnFalseAndShowEmailAndPasswordErrors() {
        // Configuración de la prueba
        val email = "invalid_email"
        val password = "123"
        val editTextEmail = Mockito.mock(EditText::class.java)
        val editTextPassword = Mockito.mock(EditText::class.java)
        val emailError = Mockito.mock(TextView::class.java)
        val passwordError = Mockito.mock(TextView::class.java)
        val emailEmptyError = "Email empty"
        val emailNotValidError = "Email not valid"
        val passwordEmptyError = "Password empty"
        val passwordTooShortError = "Password too short"
        val errorAdvice = "Error advice"

        Mockito.`when`(editTextEmail.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextEmail.text.toString()).thenReturn(email)
        Mockito.`when`(editTextPassword.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextPassword.text.toString()).thenReturn(password)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.email_input)).thenReturn(editTextEmail)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.password_input)).thenReturn(editTextPassword)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.email_input_error)).thenReturn(emailError)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.password_input_error)).thenReturn(passwordError)
        Mockito.`when`(yourClass.getString(R.string.email_empty)).thenReturn(emailEmptyError)
        Mockito.`when`(yourClass.getString(R.string.email_not_valid)).thenReturn(emailNotValidError)
        Mockito.`when`(yourClass.getString(R.string.password_empty)).thenReturn(passwordEmptyError)
        Mockito.`when`(yourClass.getString(R.string.password_too_short)).thenReturn(passwordTooShortError)
        Mockito.`when`(yourClass.getString(R.string.error_advice)).thenReturn(errorAdvice)

        // Ejecución de la función a probar
        val result = yourClass.validateForm()

        // Verificación de los resultados esperados
        assertFalse(result)
        Mockito.verify(emailError).setText(emailNotValidError)
        Mockito.verify(passwordError).setText(passwordTooShortError)
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech(errorAdvice)
    }

    @Test
    fun validateForm_emptyEmailAndPassword_returnFalseAndShowEmailAndPasswordErrors() {
        // Configuración de la prueba
        val email = ""
        val password = ""
        val editTextEmail = Mockito.mock(EditText::class.java)
        val editTextPassword = Mockito.mock(EditText::class.java)
        val emailError = Mockito.mock(TextView::class.java)
        val passwordError = Mockito.mock(TextView::class.java)
        val emailEmptyError = "Email empty"
        val emailNotValidError = "Email not valid"
        val passwordEmptyError = "Password empty"
        val passwordTooShortError = "Password too short"
        val errorAdvice = "Error advice"

        Mockito.`when`(editTextEmail.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextEmail.text.toString()).thenReturn(email)
        Mockito.`when`(editTextPassword.text).thenReturn(Mockito.mock(CharSequence::class.java))
        Mockito.`when`(editTextPassword.text.toString()).thenReturn(password)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.email_input)).thenReturn(editTextEmail)
        Mockito.`when`(yourClass.findViewById<EditText>(R.id.password_input)).thenReturn(editTextPassword)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.email_input_error)).thenReturn(emailError)
        Mockito.`when`(yourClass.findViewById<TextView>(R.id.password_input_error)).thenReturn(passwordError)
        Mockito.`when`(yourClass.getString(R.string.email_empty)).thenReturn(emailEmptyError)
        Mockito.`when`(yourClass.getString(R.string.email_not_valid)).thenReturn(emailNotValidError)
        Mockito.`when`(yourClass.getString(R.string.password_empty)).thenReturn(passwordEmptyError)
        Mockito.`when`(yourClass.getString(R.string.password_too_short)).thenReturn(passwordTooShortError)
        Mockito.`when`(yourClass.getString(R.string.error_advice)).thenReturn(errorAdvice)

        // Ejecución de la función a probar
        val result = yourClass.validateForm()

        // Verificación de los resultados esperados
        assertFalse(result)
        Mockito.verify(emailError).setText(emailEmptyError)
        Mockito.verify(passwordError).setText(passwordEmptyError)
        Mockito.verify(yourClass.textToSpeech).convertTextToSpeech(errorAdvice)
    }*/
}