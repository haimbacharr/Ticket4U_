package com.ticket.foru.User;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ticket.foru.Fragment.LoginFragment;
import com.ticket.foru.Fragment.RegisterFragment;
import com.example.ticket4u.R;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        if(getIntent().getStringExtra("screen").equals("login")){
            showLoginScreen();
        }
        else {
            showSignUpScreen();
        }

    }
    // function to load login fragment in activity
    public void showLoginScreen(){
        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new LoginFragment()).commit();
    }
    // function to call signup fragment in activity
    public void showSignUpScreen(){
        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new RegisterFragment()).commit();
    }
}