package com.project.tfg

import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import com.project.tfg.ui.funcionalidades.CategoriasActivity
import com.project.tfg.ui.funcionalidades.EscenaActivity
import com.project.tfg.ui.funcionalidades.TextoActivity
import com.project.tfg.ui.funcionalidades.TraducirActivity
import org.junit.*

@RunWith(AndroidJUnit4::class)
class FuncionalidadesFragmentInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        AccessibilityChecks.enable()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun checkElementsOnScreen() {
        onView(withId(androidx.appcompat.R.id.action_bar)).check(matches(hasDescendant(withText(R.string.title_funcionalidades))))
        onView(withId(R.id.categorias_view)).check(matches(isDisplayed()))
        onView(withId(R.id.texto_view)).check(matches(isDisplayed()))
        onView(withId(R.id.escena_view)).check(matches(isDisplayed()))
        onView(withId(R.id.traducir_view)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyElementsInLandscape() {
        activityRule.scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onView(withId(R.id.categorias_view)).check(matches(isDisplayed()))
        onView(withId(R.id.texto_view)).check(matches(isDisplayed()))
        onView(withId(R.id.escena_view)).check(matches(isDisplayed()))
        onView(withId(R.id.traducir_view)).check(matches(isDisplayed()))
        onView(withId(androidx.appcompat.R.id.action_bar)).check(matches(hasDescendant(withText(R.string.title_funcionalidades))))
    }

    @Test
    fun imageView_hasCorrectContentDescription() {
        var imageView = onView(withId(R.id.categorias_view))

        imageView.check(matches(isDisplayed()))
        imageView.check(matches(withContentDescription(R.string.descripción_categorias)))

        imageView = onView(withId(R.id.escena_view))
        imageView.check(matches(isDisplayed()))
        imageView.check(matches(withContentDescription(R.string.descripción_escenas)))

        imageView = onView(withId(R.id.traducir_view))
        imageView.check(matches(isDisplayed()))
        imageView.check(matches(withContentDescription(R.string.descripción_traduccion)))

        imageView = onView(withId(R.id.texto_view))
        imageView.check(matches(isDisplayed()))
        imageView.check(matches(withContentDescription(R.string.descripción_texto)))
    }

    @Test
    fun categoriasViewClick_startsCategoriasActivity() {
        onView(withId(R.id.categorias_view)).perform(click())
        intended(hasComponent(CategoriasActivity::class.java.name))
    }

    @Test
    fun textoViewClick_startsTextoActivity() {
        onView(withId(R.id.texto_view)).perform(click())
        intended(hasComponent(TextoActivity::class.java.name))
    }

    @Test
    fun escenaViewClick_startsEscenaActivity() {
        onView(withId(R.id.escena_view)).perform(click())
        intended(hasComponent(EscenaActivity::class.java.name))
    }

    @Test
    fun traduccionViewClick_startsTraducirActivity() {
        onView(withId(R.id.traducir_view)).perform(click())
        intended(hasComponent(TraducirActivity::class.java.name))
    }
}