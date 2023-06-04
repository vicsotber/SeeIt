package com.project.tfg

import android.content.DialogInterface
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import com.project.tfg.ui.DialogUtils
import org.junit.*
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class DialogUtilsInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkShowAlertDialog() {
        val dialogUtils = DialogUtils()

        // Llama al método showAlertDialog en un hilo de UI
        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                dialogUtils.showAlertDialog(
                    activity,
                    "Test Title",
                    "Test Message",
                    DialogInterface.OnClickListener { dialog, which -> },
                    DialogInterface.OnClickListener { dialog, which -> },
                    DialogInterface.OnClickListener { dialog, which -> }
                )
            }
        }

        // Verifica que el diálogo se está mostrando correctamente utilizando Espresso
        onView(withId(android.R.id.button1)).check(matches(withText(R.string.texto_dialog_opcion_galeria)))
        onView(withId(android.R.id.button2)).check(matches(withText(R.string.texto_dialog_opcion_camara)))
        onView(withId(android.R.id.button3)).check(matches(withText(R.string.texto_dialog_opcion_cancelar)))
        onView(withText("Test Title")).check(matches(isDisplayed()))
        onView(withText("Test Message")).check(matches(isDisplayed()))
    }

    private fun clickAlertDialogButton(buttonId: Int) {
        val dialogUtils = DialogUtils()
        var option1Clicked = false
        var option2Clicked = false
        var option3Clicked = false
        activityRule.scenario.onActivity { activity ->
            dialogUtils.showAlertDialog(
                activity,
                "Test Title",
                "Test Message",
                DialogInterface.OnClickListener { _, _ ->
                    // Option 1 clicked
                    option1Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 2 clicked
                    option2Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 3 clicked
                    option3Clicked = true
                }
            )
        }

        // Find and click the specified button
        onView(withId(buttonId)).perform(click())

        // Assert that the correct boolean value is true
        when (buttonId) {
            android.R.id.button1 -> assertTrue(option1Clicked)
            android.R.id.button2 -> assertTrue(option2Clicked)
            android.R.id.button3 -> assertTrue(option3Clicked)
        }
    }

    @Test
    fun checkClickGaleriaAlertDialog() {
        clickAlertDialogButton(android.R.id.button1)
    }

    @Test
    fun checkClickCamaraAlertDialog() {
        clickAlertDialogButton(android.R.id.button2)
    }

    @Test
    fun checkClickCancelarAlertDialog() {
        clickAlertDialogButton(android.R.id.button3)
    }


    /*@Test
    fun checkClickGaleriaAlertDialog() {
        val dialogUtils = DialogUtils()
        var option1Clicked = false
        var option2Clicked = false
        var option3Clicked = false

        activityRule.scenario.onActivity { activity ->
            dialogUtils.showAlertDialog(
                activity,
                "Test Title",
                "Test Message",
                DialogInterface.OnClickListener { _, _ ->
                    // Option 1 clicked
                    option1Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 2 clicked
                    option2Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 3 clicked
                    option3Clicked = true
                }
            )
        }

        // Find and click button 1
        onView(withId(android.R.id.button1)).perform(click())
        assertTrue(option1Clicked)
    }*/

    /*@Test
    fun checkClickCamaraAlertDialog() {
        val dialogUtils = DialogUtils()
        var option1Clicked = false
        var option2Clicked = false
        var option3Clicked = false

        activityRule.scenario.onActivity { activity ->
            dialogUtils.showAlertDialog(
                activity,
                "Test Title",
                "Test Message",
                DialogInterface.OnClickListener { _, _ ->
                    // Option 1 clicked
                    option1Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 2 clicked
                    option2Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 3 clicked
                    option3Clicked = true
                }
            )
        }

        // Find and click button 2
        onView(withId(android.R.id.button2)).perform(click())
        assertTrue(option2Clicked)
    }*/

    /*@Test
    fun checkClickCancelarAlertDialog() {
        val dialogUtils = DialogUtils()
        var option1Clicked = false
        var option2Clicked = false
        var option3Clicked = false

        activityRule.scenario.onActivity { activity ->
            dialogUtils.showAlertDialog(
                activity,
                "Test Title",
                "Test Message",
                DialogInterface.OnClickListener { _, _ ->
                    // Option 1 clicked
                    option1Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 2 clicked
                    option2Clicked = true
                },
                DialogInterface.OnClickListener { _, _ ->
                    // Option 3 clicked
                    option3Clicked = true
                }
            )
        }

        // Find and click button 3
        onView(withId(android.R.id.button3)).perform(click())
        assertTrue(option3Clicked)
    }*/
}