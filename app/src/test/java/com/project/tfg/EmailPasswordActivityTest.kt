package com.project.tfg

import android.text.SpannableStringBuilder
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.nhaarman.mockitokotlin2.whenever
import com.project.tfg.ui.registros.EmailPasswordActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(EmailPasswordActivity::class)
class EmailPasswordActivityTest {

    private lateinit var emailPasswordActivity: EmailPasswordActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    @Before
    fun setUp() {
        this.emailPasswordActivity = PowerMockito.mock(EmailPasswordActivity::class.java)
        this.auth = mock(FirebaseAuth::class.java)
        this.user = mock(FirebaseUser::class.java)

        whenever(this.emailPasswordActivity.auth).thenReturn(this.auth)
        //doNothing().`when`(emailPasswordActivity).convertTextToSpeech(anyString())
        //whenever(this.emailPasswordActivity.textToSpeech).thenReturn(null)
    }


    /**Should not create an account when password is too short*/
    @Test
    fun createAccountWhenPasswordIsTooShort() {
// Set up
        val email = "test@test.com"
        val password = "12345"
        val passwordError = mock(TextView::class.java)

        // Mocking
        `when`(emailPasswordActivity.findViewById<TextView>(R.id.password_input_error)).thenReturn(
            passwordError
        )
        `when`(emailPasswordActivity.validateForm()).thenReturn(false)

        // Execution
        emailPasswordActivity.createAccount(email, password)

        // Verification
        /*verify(
            passwordError,
            times(1)
        ).setText(emailPasswordActivity.getString(R.string.password_too_short))
        verify(emailPasswordActivity, times(1)).convertTextToSpeech(
            emailPasswordActivity.getString(
                R.string.password_too_short
            )
        )*/
        verify(emailPasswordActivity.auth, never()).createUserWithEmailAndPassword(email, password)
    }

    /**Should create an account when email and password are valid and not in use*/
    @Test
    fun createAccountWhenEmailAndPasswordAreValidAndNotInUse() {
        val email = "test@example.com"
        val password = "password"

// Mock the Firebase Auth result
        val authResult = mock(AuthResult::class.java)
        val task = mock(Task::class.java)
        whenever(task.isSuccessful).thenReturn(true)
        whenever(task.result).thenReturn(authResult)

// Mock the Firebase Auth createUserWithEmailAndPassword method
        whenever(this.auth.createUserWithEmailAndPassword(email, password)).thenReturn(task as Task<AuthResult>?)

        verify(this.emailPasswordActivity, times(1)).validateForm()

// Call the createAccount method with valid email and password
        this.emailPasswordActivity.createAccount(email, password)

// Verify that the Firebase Auth createUserWithEmailAndPassword method was called with the correct email and password
        verify(this.auth, times(1)).createUserWithEmailAndPassword(email, password)

// Verify that the activity finished after creating the account
        verify(this.emailPasswordActivity, times(1)).finish()
    }

    /**Should not create an account when email and password fields are empty*/
    @Test
    fun createAccountWhenEmailAndPasswordFieldsAreEmpty() {
        // Set up
        val emailInput = mock(TextView::class.java)
        val passwordInput = mock(TextView::class.java)
        val emailInputError = mock(TextView::class.java)
        val passwordInputError = mock(TextView::class.java)

        `when`(this.emailPasswordActivity.findViewById<TextView>(R.id.email_input)).thenReturn(
            emailInput
        )
        `when`(this.emailPasswordActivity.findViewById<TextView>(R.id.password_input)).thenReturn(
            passwordInput
        )
        `when`(this.emailPasswordActivity.findViewById<TextView>(R.id.email_input_error)).thenReturn(
            emailInputError
        )
        `when`(this.emailPasswordActivity.findViewById<TextView>(R.id.password_input_error)).thenReturn(
            passwordInputError
        )

        `when`(emailInput.text).thenReturn("")
        `when`(passwordInput.text).thenReturn("")

        // Test
        this.emailPasswordActivity.createAccount("", "")

        // Verify
        verify(
            emailInputError,
            times(1)
        ).setText(this.emailPasswordActivity.getString(R.string.email_empty))
        verify(
            passwordInputError,
            times(1)
        ).setText(this.emailPasswordActivity.getString(R.string.password_empty))
    }

    /**Should not create an account when email is already in use*/
    @Test
    fun createAccountWhenEmailIsAlreadyInUse() {
        val email = "test@example.com"
        val password = "password"

        val task = mock(Task::class.java) as Task<AuthResult>
        val exception = mock(FirebaseAuthUserCollisionException::class.java)

        whenever(this.auth.createUserWithEmailAndPassword(email, password)).thenReturn(task)
        whenever(task.isSuccessful).thenReturn(false)
        whenever(task.exception).thenReturn(exception)
        whenever(exception.toString()).thenReturn("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")

        val emailInput = mock(TextView::class.java)
        val passwordInputError = mock(TextView::class.java)

        whenever(this.emailPasswordActivity.findViewById<TextView>(R.id.email_input_error)).thenReturn(
            emailInput
        )
        whenever(this.emailPasswordActivity.findViewById<TextView>(R.id.password_input_error)).thenReturn(
            passwordInputError
        )

        // Test
        this.emailPasswordActivity.createAccount(email, password)

        verify(emailInput, times(1)).setText("Email is already in use.")
        verify(passwordInputError, times(1)).setText("")
    }

    /**Should not create an account when email is invalid*/
    @Test
    fun createAccountWhenEmailIsInvalid() {
        // Set up
        val emailNotValid = "Introduce una dirección de correo válida"
        val email = "invalidEmail"
        val password = "password"
        val emailInput = mock(EditText::class.java)
        val passwordInput = mock(EditText::class.java)
        val emailError = mock(TextView::class.java)
        val passwordError = mock(TextView::class.java)

        whenever(emailPasswordActivity.findViewById<EditText>(R.id.email_input)).thenReturn(
            emailInput
        )
        whenever(emailPasswordActivity.findViewById<EditText>(R.id.password_input)).thenReturn(
            passwordInput
        )
        whenever(emailPasswordActivity.findViewById<TextView>(R.id.email_input_error)).thenReturn(
            emailError
        )
        whenever(emailPasswordActivity.findViewById<TextView>(R.id.password_input_error)).thenReturn(
            passwordError
        )

        whenever(emailInput.text).thenReturn(SpannableStringBuilder(email))
        whenever(passwordInput.text).thenReturn(SpannableStringBuilder(password))

        // Test
        emailPasswordActivity.createAccount(email, password)

        // Verify
        verify(emailPasswordActivity).validateForm()
        verify(emailError, times(1)).setText(emailNotValid)
        verify(emailPasswordActivity, times(1)).convertTextToSpeech(emailNotValid)
        verify(auth, never()).createUserWithEmailAndPassword(email, password)
    }


    @Test
    fun testValidateFormWithValidData() {
        // Set up test data
        val email = "test@example.com"
        val password = "password"

        // Set email and password fields with valid data
        emailPasswordActivity.findViewById<EditText>(R.id.email_input).setText(email)
        emailPasswordActivity.findViewById<EditText>(R.id.password_input).setText(password)

        // Call validateForm() and assert that it returns true
        assertTrue(emailPasswordActivity.validateForm())
    }

    @Test
    fun testValidateFormWithInvalidEmail() {
        // Set up test data
        val email = "invalid_email"
        val password = "password"

        // Set email and password fields with invalid data
        emailPasswordActivity.findViewById<EditText>(R.id.email_input).setText(email)
        emailPasswordActivity.findViewById<EditText>(R.id.password_input).setText(password)

        // Call validateForm() and assert that it returns false
        assertFalse(emailPasswordActivity.validateForm())
    }

    @Test
    fun testValidateFormWithShortPassword() {
        // Set up test data
        val email = "test@example.com"
        val password = "12345"

        // Set email and password fields with invalid data
        emailPasswordActivity.findViewById<EditText>(R.id.email_input).setText(email)
        emailPasswordActivity.findViewById<EditText>(R.id.password_input).setText(password)

        // Call validateForm() and assert that it returns false
        assertFalse(emailPasswordActivity.validateForm())
    }

    @Test
    fun testCreateAccount() {
        // Set up test data
        val email = "test@example.com"
        val password = "password"

        // Set email and password fields with valid data
        emailPasswordActivity.findViewById<EditText>(R.id.email_input).setText(email)
        emailPasswordActivity.findViewById<EditText>(R.id.password_input).setText(password)

        // Call createAccount() with test data
        emailPasswordActivity.createAccount(email, password)

        // Add assertions to verify expected behavior
        // For example, you could assert that a new user account was created successfully
        // by checking that the current user is not null
        assertNotNull(emailPasswordActivity.auth.currentUser)
    }

    @Test
    fun testSignIn() {
        // Set up test data
        val email = "test@example.com"
        val password = "password"

        // Create a new user account with test data
        emailPasswordActivity.createAccount(email, password)

        // Set email and password fields with valid data
        emailPasswordActivity.findViewById<EditText>(R.id.email_input).setText(email)
        emailPasswordActivity.findViewById<EditText>(R.id.password_input).setText(password)

        // Call signIn() with test data
        emailPasswordActivity.signIn(email, password)

        // Add assertions to verify expected behavior
        // For example, you could assert that the user is signed in successfully
        // by checking that the current user is not null
        assertNotNull(emailPasswordActivity.auth.currentUser)
    }

}