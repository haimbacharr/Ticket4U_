package com.ticket.foru;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class testLoginFireBase {
    @Mock
    FirebaseAuth firebaseAuth;

    @Mock
    FirebaseUser firebaseUser;

    @Mock
    Task<AuthResult> task;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /* Input: 1. empty email
              2. empty password
       Expected: True */
    @Test
    public void testEmptyFields() {
        final String email = "";
        final String password = "";
        boolean isEmpty = true;
        if (!email.isEmpty()) {
            isEmpty = false;
        }
        if (!password.isEmpty()) {
            isEmpty = false;
        }
        assertTrue(isEmpty);

    }

    /* Input: 1. empty email
              2. valid password
       Expected: True */
    @Test
    public void testEmptyEmailField() {
        final String email = "";
        final String password = "123456";
        boolean isEmpty = true;
        if (!email.isEmpty()) {
            isEmpty = false;
        }
        assertTrue(isEmpty);

    }

    /* Input: 1. valid email
              2. empty password
       Expected: True */
    @Test
    public void testEmptyPasswordField() {
        final String email = "moshe@gmail.com";
        final String password = "";
        boolean isEmpty = true;
        if (!password.isEmpty()) {
            isEmpty = false;
        }
        assertTrue(isEmpty);

    }

    /* Input: 1. valid email
              2. השךןג password
       Expected: False */
    @Test
    public void testPasswordAndEmailFields() {
        final String email = "moshe@gmail.com";
        final String password = "123456";
        boolean isEmpty = true;
        if (!password.isEmpty() && !email.isEmpty() ) {
            isEmpty = false;
        }
        assertFalse(isEmpty);

    }




    /* Input: moshe@gmail.com
       Expected: True     */
    @Test
    public void testValidEmail() throws InterruptedException {
        final String email = "moshe@gmail.com";
        final String pattern = "^(.+)@(.+)$";
        final Pattern emailRegex = Pattern.compile(pattern);
        final Matcher matcher = emailRegex.matcher(email);
        boolean isValid = matcher.matches();
        assertTrue(isValid);
    }


    /* Input: ggg
       Expected: False     */
    @Test
    public void testUnvalidEmail() throws InterruptedException {
        final String email = "ggg";
        final String pattern = "^(.+)@(.+)$";
        final Pattern emailRegex = Pattern.compile(pattern);
        final Matcher matcher = emailRegex.matcher(email);
        boolean isValid = matcher.matches();
        assertFalse(isValid);
    }

    @Test
    public void testEmailAndWrongPassword() throws InterruptedException {
        final String email = "user100@gmail.com";
        final String password = "user100222";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fail("The email address and password is incorrect, but the task was successful");
                        } else {
                            assertTrue(true);
                        }
                        latch.countDown();
                    }
                });
        latch.await(5, TimeUnit.SECONDS);
    }
}



