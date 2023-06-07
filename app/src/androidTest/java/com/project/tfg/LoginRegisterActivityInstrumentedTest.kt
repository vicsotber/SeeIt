package com.project.tfg

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginRegisterActivityInstrumentedTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {
        AccessibilityChecks.enable()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        auth = Firebase.auth
        FirebaseAuth.getInstance().signOut()
    }

    @After
    fun tearDown() {
        val user = auth.currentUser
        if(user?.email!="test_registros@test.com"){
            user?.delete()
        }else{
            FirebaseAuth.getInstance().signOut()
        }
        activityScenario.close()
    }

    @Test
    fun testOnCreate() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())
            onView(withId(R.id.email_input))
                .check(matches(isDisplayed()))
            onView(withId(R.id.password_input))
                .check(matches(isDisplayed()))
            onView(withId(R.id.login_btn))
                .check(matches(isDisplayed()))
            onView(withId(R.id.register_btn))
                .check(matches(isDisplayed()))
    }

    @Test
    fun testCreateAccountWithValidEmailAndPassword() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

            val email = "test@test.com"
            val password = "123456"

            onView(withId(R.id.email_input)).perform(typeText(email), closeSoftKeyboard())
            onView(withId(R.id.password_input)).perform(typeText(password), closeSoftKeyboard())
            onView(withId(R.id.register_btn)).perform(scrollTo(), click())
            //Thread.sleep(250000)
            Thread.sleep(2000)

            // Comprobar que se haya creado la cuenta exitosamente y que la actividad haya finalizado
            onView(withId(R.id.delete_account_btn))
                .check(matches(isDisplayed()))
            onView(withId(R.id.logout_btn))
                .check(matches(isDisplayed()))
            assertTrue(FirebaseAuth.getInstance().currentUser != null)
    }

    @Test
    fun testLoginWithValidEmailAndPassword() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

        val email = "test_registros@test.com"
        val password = "Test1234@Test"

        onView(withId(R.id.email_input)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password_input)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.login_btn)).perform(scrollTo(), click())
        //Thread.sleep(250000)
        Thread.sleep(2000)

        // Comprobar que se ha iniciado sesión correctamente
        onView(withId(R.id.delete_account_btn))
            .check(matches(isDisplayed()))
        onView(withId(R.id.logout_btn))
            .check(matches(isDisplayed()))
        assertTrue(FirebaseAuth.getInstance().currentUser != null)
    }

    @Test
    fun testAlreadyExistingAccount() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

        val email = "test2@test.com"
        val password = "123456"

        onView(withId(R.id.email_input)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password_input)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.register_btn)).perform(scrollTo(), click())
        //Thread.sleep(250000)
        Thread.sleep(2000)

        // Comprobar que ha aparecido el mensaje de que ya existe una cuenta con ese correo
        onView(withId(R.id.password_input_error))
            .check(matches(ViewMatchers.withText(R.string.email_in_use)))
    }

    @Test
    fun testAccountNotFound() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

        val email = "test@test.com"
        val password = "123456"

        onView(withId(R.id.email_input)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password_input)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.login_btn)).perform(scrollTo(), click())
        //Thread.sleep(250000)
        Thread.sleep(2000)

        // Comprobar que ha aparecido el mensaje de cuenta no existente
        onView(withId(R.id.password_input_error))
            .check(matches(ViewMatchers.withText(R.string.email_not_exists)))
    }

    @Test
    fun testLoginWithInvalidEmailAndPassword() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

        onView(withId(R.id.email_input)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.password_input)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.register_btn)).perform(scrollTo(), click())
        Thread.sleep(2000)

        // Comprobar que han aparecido los mensajes de error por email y contraseña vacíos
        onView(withId(R.id.email_input_error))
            .check(matches(ViewMatchers.withText(R.string.email_empty)))
        onView(withId(R.id.password_input_error))
            .check(matches(ViewMatchers.withText(R.string.password_empty)))

        val email = "test"
        val password = "1"

        onView(withId(R.id.email_input)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password_input)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.register_btn)).perform(scrollTo(), click())
        Thread.sleep(2000)

        // Comprobar que han aparecido los mensajes de error por email inválido y contraseña muy corta
        onView(withId(R.id.email_input_error))
            .check(matches(ViewMatchers.withText(R.string.email_not_valid)))
        onView(withId(R.id.password_input_error))
            .check(matches(ViewMatchers.withText(R.string.password_too_short)))
    }
}