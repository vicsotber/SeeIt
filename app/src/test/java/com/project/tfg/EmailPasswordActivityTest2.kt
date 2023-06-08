package com.project.tfg

import android.widget.EditText
import com.project.tfg.ui.registros.EmailPasswordActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class EmailPasswordActivityTest2 {

    private lateinit var activity: EmailPasswordActivity

    @Before
    @Throws(Exception::class)
    fun setUp() {
        activity = Robolectric.buildActivity(EmailPasswordActivity::class.java)
            .create()
            .resume()
            .get()
    }

    //Write Unit Tests with Robolectric for the validateForm method
    //Write a test that validates the form with invalid email
    @Test
    fun testValidateFormWithInvalidEmail() {
        val email = "test@example"
        val password = "12345"

        activity.findViewById<EditText>(R.id.email_input).setText(email)
        activity.findViewById<EditText>(R.id.password_input).setText(password)

        assertFalse(activity.validateForm())
    }



}