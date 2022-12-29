package com.example.ticket4u.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ticket4u.Fragment.LoginFragment;
import com.example.ticket4u.Fragment.RegisterFragment;
import com.example.ticket4u.R;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        showLoginScreen();
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