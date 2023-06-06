package com.project.tfg

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RegistrosFragmentInstrumentedTest {
    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private lateinit var auth: FirebaseAuth

/*    @Before
    fun setUp() {
        FirebaseAuth.getInstance().signOut()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        auth = Firebase.auth
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

        val email = "test_registros@test.com"
        val password = "Test1234@Test"

        onView(withId(R.id.email_input)).perform(
            ViewActions.typeText(email),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.password_input)).perform(
            ViewActions.typeText(password),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.login_btn)).perform(ViewActions.scrollTo(), click())
        //Thread.sleep(
        // 250000)
        Thread.sleep(2000)
    }*/

    @Before
    fun setUp() {
        FirebaseAuth.getInstance().signOut()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        auth = Firebase.auth

        val email = "test_registros@test.com"
        val password = "Test1234@Test"

        auth.signInWithEmailAndPassword(email, password)
        Thread.sleep(2000)
    }

    @After
    fun tearDown() {
        FirebaseAuth.getInstance().signOut()
        activityScenario.close()
    }

    @Test
    fun checkElementsOnScreen() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.logout_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.delete_account_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun logOutTest() {
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.logout_btn)).perform(click())
        onView(withId(R.id.access_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.access_advice)).check(matches(withText(R.string.access_advice)))
        assertNull(FirebaseAuth.getInstance().currentUser)
    }

    @Test
    fun deleteAccountTest() {
        onView(withId(R.id.navigation_funcionalidades)).perform(click())
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

        val email = "test_registros_delete@test.com"
        val password = "Test1234@Test"

        onView(withId(R.id.email_input)).perform(
            ViewActions.typeText(email),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.password_input)).perform(
            ViewActions.typeText(password),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.register_btn)).perform(ViewActions.scrollTo(), click())
        Thread.sleep(5000)

        onView(withId(R.id.delete_account_btn)).perform(click())
        //Thread.sleep(300000)
        Thread.sleep(2000)

        onView(withId(R.id.access_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.access_advice)).check(matches(withText(R.string.access_advice)))
        assertNull(FirebaseAuth.getInstance().currentUser)

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.fetchSignInMethodsForEmail("test_registros_delete@test.com")
            .addOnCompleteListener { task: Task<SignInMethodQueryResult> ->
                assertTrue(task.result.signInMethods!!.isEmpty())
            }
    }

    @Test
    fun checkRecordText() {
        onView(withId(R.id.navigation_registros)).perform(click())
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Encuentra el GridLayout que contiene las imágenes
        val gridLayout = device.findObject(By.clazz("android.widget.GridLayout"))
        // Encuentra el número total de columnas en el GridLayout
        val numColumnas = gridLayout.childCount
        // Encuentra la posición de la imagen específica en el GridLayout
        val filaObjetivo = 0 // Fila deseada (comienza en 0)
        val columnaObjetivo = 0 // Columna deseada (comienza en 0)
        val posicionObjetivo = filaObjetivo * numColumnas + columnaObjetivo
        // Encuentra la imagen específica utilizando la posición en el GridLayout
        val imagenEspecifica = gridLayout.children[posicionObjetivo]
        // Haz clic en la imagen específica
        imagenEspecifica.click()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("\"No es necesario hacer cosas\nextraordinarias"))))
    }

    @Test
    fun checkRecordTranslation() {
        onView(withId(R.id.navigation_registros)).perform(click())
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Encuentra el GridLayout que contiene las imágenes
        val gridLayout = device.findObject(By.clazz("android.widget.GridLayout"))
        // Encuentra el número total de columnas en el GridLayout
        val numColumnas = gridLayout.childCount
        // Encuentra la posición de la imagen específica en el GridLayout
        val filaObjetivo = 0 // Fila deseada (comienza en 0)
        val columnaObjetivo = 2 // Columna deseada (comienza en 0)
        val posicionObjetivo = filaObjetivo * numColumnas + columnaObjetivo
        // Encuentra la imagen específica utilizando la posición en el GridLayout
        val imagenEspecifica = gridLayout.children[posicionObjetivo]
        // Haz clic en la imagen específica
        imagenEspecifica.click()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Texto reconocido: \"No es necesario hacer cosas extraordinarias"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Texto traducido: \"It is not necessary to do extraordinary things"))))
    }

    @Test
    fun checkRecordScene() {
        onView(withId(R.id.navigation_registros)).perform(click())
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Encuentra el GridLayout que contiene las imágenes
        val gridLayout = device.findObject(By.clazz("android.widget.GridLayout"))
        // Encuentra el número total de columnas en el GridLayout
        val numColumnas = gridLayout.childCount
        // Encuentra la posición de la imagen específica en el GridLayout
        val filaObjetivo = 0 // Fila deseada (comienza en 0)
        val columnaObjetivo = 1 // Columna deseada (comienza en 0)
        val posicionObjetivo = filaObjetivo * numColumnas + columnaObjetivo
        // Encuentra la imagen específica utilizando la posición en el GridLayout
        val imagenEspecifica = gridLayout.children[posicionObjetivo]
        // Haz clic en la imagen específica
        imagenEspecifica.click()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Descripción de la imagen: un plato con fruta"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objetos detectados en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Etiquetas detectadas para esta imagen:"))))
    }
}