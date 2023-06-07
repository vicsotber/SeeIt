package com.project.tfg
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TraducirActivityInstrumentedTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        AccessibilityChecks.enable()
        FirebaseAuth.getInstance().signOut()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        activityScenario.close()
    }


    @Test
    fun testOnCreate() {
        onView(withId(R.id.funcionalidad_traduccion_img)).perform(click())
        onView(withText("Galería"))
            .check(matches(isDisplayed()))
        onView(withText("Cámara"))
            .check(matches(isDisplayed()))
        onView(withText("Cancelar"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun analyzeImage() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        onView(withId(R.id.funcionalidad_traduccion_img)).perform(click())
        onView(withText("Galería")).perform(click())

        // Esperamos un tiempo para dar tiempo a que la galería se abra
        Thread.sleep(2000)

        // Seleccionamos la imagen con texto que tiene como título 'texto_test.jpg'
        val image: UiObject2 = device.findObject(By.text("texto_test.jpg"))
        image.click()
        // Esperamos a que se nos devuelva a nuestra aplicación
        device.waitForIdle()

        val sourceSpinner = onView(withId(R.id.spinner_source_language))
        // Realizar una selección en el Spinner
        sourceSpinner.perform(click())
        Thread.sleep(2000)
        onView(withText("Español")).perform(click())
        // Verificar que se haya seleccionado la opción correcta en el Spinner
        sourceSpinner.check(matches(withSpinnerText("Español")))

        val targetSpinner = onView(withId(R.id.spinner_target_language))
        // Realizar una selección en el Spinner
        targetSpinner.perform(click())
        Thread.sleep(2000)
        onView(withText("Inglés")).perform(click())
        // Verificar que se haya seleccionado la opción correcta en el Spinner
        targetSpinner.check(matches(withSpinnerText("Inglés")))

        onView(withId(R.id.boton_traducir)).perform(click())
        Thread.sleep(2000)
        // Crear un objeto UiScrollable que representa la pantalla desplazable
        val scrollable = UiScrollable(UiSelector().scrollable(true))
        // Desplazar la pantalla hacia abajo
        scrollable.scrollForward()
        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(R.string.traduccion_esperando)))
        Thread.sleep(300000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Suppose you are driving"))))
    }

    @Test
    fun sharePicture() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.pressHome() // Vuelve a la pantalla de inicio (opcional)
        val galleryApp = device.findObject(By.desc("Archivos"))
        galleryApp.click()
        Thread.sleep(5000)

        // Seleccionamos la imagen con texto que tiene como título 'texto_test.jpg'
        val image: UiObject2 = device.findObject(By.text("texto_test.jpg"))
        image.click(5000)

        device.findObject(By.clickable(true).descContains("Compartir")).click()
        Thread.sleep(6000)
        device.findObject(By.text("Traducir")).click()

        device.waitForIdle()
        Thread.sleep(5000)

        val sourceSpinner = onView(withId(R.id.spinner_source_language))
        // Realizar una selección en el Spinner
        sourceSpinner.perform(click())
        Thread.sleep(2000)
        onView(withText("Español")).perform(click())
        // Verificar que se haya seleccionado la opción correcta en el Spinner
        sourceSpinner.check(matches(withSpinnerText("Español")))

        val targetSpinner = onView(withId(R.id.spinner_target_language))
        // Realizar una selección en el Spinner
        targetSpinner.perform(click())
        Thread.sleep(2000)
        onView(withText("Inglés")).perform(click())
        // Verificar que se haya seleccionado la opción correcta en el Spinner
        targetSpinner.check(matches(withSpinnerText("Inglés")))

        onView(withId(R.id.boton_traducir)).perform(click())
        Thread.sleep(2000)
        // Crear un objeto UiScrollable que representa la pantalla desplazable
        val scrollable = UiScrollable(UiSelector().scrollable(true))
        // Desplazar la pantalla hacia abajo
        scrollable.scrollForward()
        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(R.string.traduccion_esperando)))
        Thread.sleep(300000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Suppose you are driving"))))

    }

    @Test
    fun testRecordIsSaved() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.access_btn)).perform(click())

        onView(withId(R.id.email_input)).perform(
            ViewActions.typeText("test@test.com"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.password_input)).perform(
            ViewActions.typeText("Test1234@Test"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.register_btn)).perform(ViewActions.scrollTo(), click())
        Thread.sleep(5000)

        onView(withId(R.id.navigation_funcionalidades)).perform(click())
        device.findObject(By.text("Traducción")).click()
        onView(withText("Galería")).perform(click())

        // Esperamos un tiempo para dar tiempo a que la galería se abra
        Thread.sleep(5000)

        // Seleccionamos la imagen con texto que tiene como título 'texto_test.jpg'
        device.swipe(147, 1193, 147, 1193, 0)
        // Esperamos a que se nos devuelva a nuestra aplicación
        device.waitForIdle()

        val sourceSpinner = onView(withId(R.id.spinner_source_language))
        // Realizar una selección en el Spinner
        sourceSpinner.perform(click())
        Thread.sleep(2000)
        onView(withText("Español")).perform(click())
        // Verificar que se haya seleccionado la opción correcta en el Spinner
        sourceSpinner.check(matches(withSpinnerText("Español")))

        val targetSpinner = onView(withId(R.id.spinner_target_language))
        // Realizar una selección en el Spinner
        targetSpinner.perform(click())
        Thread.sleep(2000)
        onView(withText("Inglés")).perform(click())
        // Verificar que se haya seleccionado la opción correcta en el Spinner
        targetSpinner.check(matches(withSpinnerText("Inglés")))

        onView(withId(R.id.boton_traducir)).perform(click())

        Thread.sleep(60000)

        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        Thread.sleep(5000)

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
        Thread.sleep(2000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Texto reconocido: \"No es necesario hacer cosas extraordinarias"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Texto traducido: \"It is not necessary to do extraordinary things"))))

        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.delete_account_btn)).perform(click())
    }
}