package com.project.tfg;


import static org.junit.Assert.assertFalse;

import com.project.tfg.ui.registros.EmailPasswordActivity;

import org.junit.Test;


public class EmailPasswordActivityTest4 {
    @Test
    public void testActivityBehavior() {
        EmailPasswordActivity activity = new EmailPasswordActivity();

        String email = "invalidEmail";
        String password = "password";

        assertFalse(activity.validateForm());
    }
}
