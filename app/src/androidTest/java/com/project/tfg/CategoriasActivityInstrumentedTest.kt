package com.project.tfg

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import com.google.firebase.auth.FirebaseAuth
import com.project.tfg.ui.funcionalidades.*
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoriasActivityInstrumentedTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        FirebaseAuth.getInstance().signOut()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        Intents.init()
    }

    @After
    fun tearDown() {
        activityScenario.close()
        Intents.release()
    }

    @Test
    fun testOnCreate() {
        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_birds_view))
            .check(matches(isDisplayed()))
        onView(withId(R.id.category_food_view))
            .check(matches(isDisplayed()))
        onView(withId(R.id.category_insects_view))
            .check(matches(isDisplayed()))
        onView(withId(R.id.category_plants_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testStartEachCategoryActivity() {
        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_birds_view)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(AvesCategoriaActivity::class.java.name))

        onView(withText("Cancelar")).perform(click())

        onView(withId(R.id.category_food_view)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ComidaCategoriaActivity::class.java.name))

        onView(withText("Cancelar")).perform(click())

        onView(withId(R.id.category_plants_view)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(PlantasCategoriaActivity::class.java.name))

        onView(withText("Cancelar")).perform(click())

        onView(withId(R.id.category_insects_view)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(InsectosCategoriaActivity::class.java.name))
    }

    @Test
    fun sharePictureWithInsectCategory() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.pressHome()
        val galleryApp = device.findObject(By.desc("Archivos"))
        galleryApp.click()
        Thread.sleep(5000)

        device.pressBack()

        val image: UiObject2 = device.findObject(By.text("insect_abeja.jpg"))
        image.click(5000)

        device.findObject(By.clickable(true).descContains("Compartir")).click()
        Thread.sleep(6000)
        device.findObject(By.text("Insectos")).click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun sharePictureWithFoodCategory() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.pressHome()
        val galleryApp = device.findObject(By.desc("Archivos"))
        galleryApp.click()
        Thread.sleep(5000)

        device.pressBack()

        val image: UiObject2 = device.findObject(By.text("comida_salmorejo.jpg"))
        image.click(5000)

        device.findObject(By.clickable(true).descContains("Compartir")).click()
        Thread.sleep(6000)
        device.findObject(By.text("Comida")).click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen: Salmorejo"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun sharePictureWithPlantCategory() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.pressHome()
        val galleryApp = device.findObject(By.desc("Archivos"))
        galleryApp.click()
        Thread.sleep(5000)

        device.pressBack()

        val image: UiObject2 = device.findObject(By.text("plant_ocotillo.jpg"))
        image.click(5000)

        device.findObject(By.clickable(true).descContains("Compartir")).click()
        Thread.sleep(6000)
        device.findObject(By.text("Plantas")).click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun sharePictureWithBirdCategory() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.pressHome() // Vuelve a la pantalla de inicio (opcional)
        val galleryApp = device.findObject(By.desc("Archivos"))
        galleryApp.click()
        Thread.sleep(5000)

        val image: UiObject2 = device.findObject(By.text("ave_azulejo.jpg"))
        image.click(5000)

        device.findObject(By.clickable(true).descContains("Compartir")).click()
        Thread.sleep(6000)
        device.findObject(By.text("Aves")).click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun analyzeImageFood() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_food_view)).perform(click())
        onView(withText("Galería")).perform(click())

        Thread.sleep(2000)

        val image: UiObject2 = device.findObject(By.text("comida_salmorejo.jpg"))
        image.click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen: Salmorejo"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun analyzeImageBird() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_birds_view)).perform(click())
        onView(withText("Galería")).perform(click())

        Thread.sleep(2000)

        val image: UiObject2 = device.findObject(By.text("ave_azulejo.jpg"))
        image.click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun analyzeImagePlant() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_plants_view)).perform(click())
        onView(withText("Galería")).perform(click())

        Thread.sleep(2000)

        val image: UiObject2 = device.findObject(By.text("plant_ocotillo.jpg"))
        image.click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun analyzeImageInsect() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_insects_view)).perform(click())
        onView(withText("Galería")).perform(click())

        Thread.sleep(2000)

        val image: UiObject2 = device.findObject(By.text("insect_abeja.jpg"))
        image.click()

        device.waitForIdle()
        Thread.sleep(5000)

        onView(withId(R.id.texto_resultado))
            .check(matches(isDisplayed()))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))
    }

    @Test
    fun testFoodRecordIsSaved() {
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
        Thread.sleep(2000)

        onView(withId(R.id.navigation_funcionalidades)).perform(click())
        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_food_view)).perform(click())
        onView(withText("Galería")).perform(click())

        // Esperamos un tiempo para dar tiempo a que la galería se abra
        Thread.sleep(2000)
        device.swipe(148, 980, 148, 980, 0)
        // Esperamos a que se nos devuelva a nuestra aplicación y a que realice el reconocimiento de texto
        device.waitForIdle()
        Thread.sleep(2000)

        device.pressBack()
        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        Thread.sleep(2000)

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
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen: Carbonara"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))

        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.delete_account_btn)).perform(click())

        Thread.sleep(5000)
    }

    @Test
    fun testBirdRecordIsSaved() {
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
        Thread.sleep(2000)

        onView(withId(R.id.navigation_funcionalidades)).perform(click())
        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_birds_view)).perform(click())
        onView(withText("Galería")).perform(click())

        // Esperamos un tiempo para dar tiempo a que la galería se abra
        Thread.sleep(2000)
        device.swipe(563, 752, 563, 752, 0)
        // Esperamos a que se nos devuelva a nuestra aplicación y a que realice el reconocimiento de texto
        device.waitForIdle()
        Thread.sleep(2000)

        device.pressBack()
        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        Thread.sleep(2000)

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
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))

        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.delete_account_btn)).perform(click())

        Thread.sleep(5000)
    }

    @Test
    fun testPlantRecordIsSaved() {
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
        Thread.sleep(2000)

        onView(withId(R.id.navigation_funcionalidades)).perform(click())
        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_plants_view)).perform(click())
        onView(withText("Galería")).perform(click())

        // Esperamos un tiempo para dar tiempo a que la galería se abra
        Thread.sleep(2000)
        device.swipe(351, 755, 351, 755, 0)
        // Esperamos a que se nos devuelva a nuestra aplicación y a que realice el reconocimiento de texto
        device.waitForIdle()
        Thread.sleep(2000)

        device.pressBack()
        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        Thread.sleep(2000)

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
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))

        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.delete_account_btn)).perform(click())

        Thread.sleep(5000)
    }

    @Test
    fun testInsectRecordIsSaved() {
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
        Thread.sleep(2000)

        onView(withId(R.id.navigation_funcionalidades)).perform(click())
        onView(withId(R.id.funcionalidad_categorias_img)).perform(click())
        onView(withId(R.id.category_insects_view)).perform(click())
        onView(withText("Galería")).perform(click())

        // Esperamos un tiempo para dar tiempo a que la galería se abra
        Thread.sleep(2000)
        device.swipe(147, 749, 147, 749, 0)
        // Esperamos a que se nos devuelva a nuestra aplicación y a que realice el reconocimiento de texto
        device.waitForIdle()
        Thread.sleep(2000)

        device.pressBack()
        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        Thread.sleep(2000)

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
            .check(matches(withText(containsString("Objeto de la categoría detectado en la imagen:"))))
        onView(withId(R.id.texto_resultado))
            .check(matches(withText(containsString("Porcentaje de confianza:"))))

        device.pressBack()
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.delete_account_btn)).perform(click())

        Thread.sleep(5000)
    }
}