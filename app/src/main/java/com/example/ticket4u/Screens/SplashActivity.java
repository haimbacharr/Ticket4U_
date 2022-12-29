package com.example.ticket4u.Screens;

import static com.example.ticket4u.Utils.Constant.getAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.getUserLoginStatus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ticket4u.Admin.AdminActivity;
import com.example.ticket4u.Admin.AdminMainActivity;
import com.example.ticket4u.MainActivity;
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