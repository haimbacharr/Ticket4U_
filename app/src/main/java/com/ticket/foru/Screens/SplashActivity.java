package com.ticket.foru.Screens;

import static com.ticket.foru.Utils.Constant.getAdminLoginStatus;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ticket.foru.Admin.AdminActivity;
import com.ticket.foru.MainActivity;
import com.example.ticket4u.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                   if(getAdminLoginStatus(SplashActivity.this)) {
                        startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                        finish();
                    }
                    else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}