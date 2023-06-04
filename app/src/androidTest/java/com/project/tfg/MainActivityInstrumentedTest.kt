package com.project.tfg

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        activityScenario.close()
    }

    @Test
    fun testOnCreate() {
        activityScenario.onActivity { activity ->
            // Comprobamos que se ha inflado el layout
            assertNotNull(activity.binding.root)

            // Comprobamos que se ha creado la instancia del NavController
            assertNotNull(activity.findNavController(R.id.nav_host_fragment_activity_main))

            // Comprobamos que se ha configurado el BottomNavigationView con el NavController
            assertNotNull(activity.binding.navView)
        }
    }

    @Test
    fun testMainActivityNavigation() {
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_registros)).perform(click())
        onView(withId(R.id.navigation_registros)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_funcionalidades)).perform(click())
        onView(withId(R.id.navigation_funcionalidades)).check(matches(isDisplayed()))
    }
}