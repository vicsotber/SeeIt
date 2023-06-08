package com.project.tfg.ui.registros

import android.speech.tts.TextToSpeech
import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockitokotlin2.whenever
import com.project.tfg.R
import org.junit.Test
import org.mockito.Mockito.*

class RegistrosFragmentTest {

    @Test
    fun signOutUserAndUpdateViews() {
        val mockAuth = mock(FirebaseAuth::class.java)

        val mockTextToSpeech = mock(TextToSpeech::class.java)

        val fragment = RegistrosFragment()
        fragment.auth = mockAuth
        fragment.textToSpeech = mockTextToSpeech

        whenever(FirebaseAuth.getInstance()).thenReturn(mockAuth)
        doNothing().`when`(mockAuth).signOut()

        fragment.signOut()

        verify(mockAuth, times(1)).signOut()

        verify(mockTextToSpeech, times(1)).speak(
            eq(fragment.getString(R.string.logout_correct)),
            eq(TextToSpeech.QUEUE_FLUSH),
            eq(null),
            eq(null)
        )
        
        verify(fragment, times(1)).updateViews()
    }

}