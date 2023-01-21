package com.example.ticket4u;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class testEmailIsRegisterInFireBase {
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

    /* Input: useryy@gmail.com
       Expected: True     */
    @Test
    public void testEmailExistsInFirebase() throws InterruptedException {
        String email = "useryy@gmail.com";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(getApplicationContext());
        DatabaseReference ref = firebaseDatabase.getReference();
        ref = ref.child("User");
        final CountDownLatch latch = new CountDownLatch(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean emailExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child("Mail").getValue(String.class).equals(email)) {
                        Log.d("testEmailExistInFireBase","This email is already in use, enter other email");
                        emailExists = true;
                        break;
                    }
                }
                if(!emailExists)
                    Log.d("testEmailExistInFireBase","This email is available");
                assertTrue(emailExists);
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fail("The test was cancelled due to a database error: " + databaseError.getMessage());
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    /* Input: user333@gmail.com
       Expected: False */
    @Test
    public void testEmailNotExistsInFirebase() throws InterruptedException {
        String email = "user333@gmail.com";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(getApplicationContext());
        DatabaseReference ref = firebaseDatabase.getReference();
        ref = ref.child("User");
        final CountDownLatch latch = new CountDownLatch(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean emailExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child("Mail").getValue(String.class).equals(email)) {
                        Log.d("testEmailExistInFireBase","This email is already in use, enter other email");
                        emailExists = true;
                        break;
                    }
                }
                if(!emailExists)
                    Log.d("testEmailExistInFireBase","This email is available");
                assertFalse(emailExists);
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fail("The test was cancelled due to a database error: " + databaseError.getMessage());
            }
        });
        latch.await(5, TimeUnit.SECONDS);
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

    /* Input: password1 = password123
              password2 = password123
       Expected: True     */
    @Test
    public void testMatchPasswords() {
        final String password1 = "password123";
        final String password2 = "password123";
        if (password1.equals(password2)) {
            assertTrue(true);
        } else {
            fail("The passwords do not match, we expected them to be matched");
        }
    }

    /* Input: password1 = password123
              password2 = password12
       Expected: False     */
    @Test
    public void testUnMatchPasswords() {
        final String password1 = "password123";
        final String password2 = "password12";
        if (password1.equals(password2)) {
            fail("The passwords are matched, we expected them to be unmatched");
        } else {
            assertFalse(false);

        }
    }
}



