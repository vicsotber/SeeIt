package com.project.tfg

import android.text.SpannableStringBuilder
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import com.project.tfg.ui.registros.EmailPasswordActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric

class EmailPasswordActivityTest5 {

    private lateinit var emailPasswordActivity: EmailPasswordActivity

    @Before
    fun setUp() {
        emailPasswordActivity = Robolectric.setupActivity(EmailPasswordActivity::class.java)
    }


    /**Should return false when the email is empty*/
    @Test
    fun validateFormWhenEmailIsEmpty() {
        val emailInput = emailPasswordActivity.findViewById<EditText>(R.id.email_input)
        val passwordInput = emailPasswordActivity.findViewById<EditText>(R.id.password_input)
        val emailError = emailPasswordActivity.findViewById<TextView>(R.id.email_input_error)
        val passwordError = emailPasswordActivity.findViewById<TextView>(R.id.password_input_error)

        emailInput.text = SpannableStringBuilder("")
        passwordInput.text = SpannableStringBuilder("password")

        val result = emailPasswordActivity.validateForm()

        assertFalse(result)
        assertEquals(emailError.text, emailPasswordActivity.getString(R.string.email_empty))
        assertEquals(passwordError.text, "")

    }
}