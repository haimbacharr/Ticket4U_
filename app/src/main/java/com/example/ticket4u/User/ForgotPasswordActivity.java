package com.example.ticket4u.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticket4u.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email;
    ImageView imageView1;
    Button resetbutton;
    ViewGroup container;
    TextView goback,textView;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email=findViewById(R.id.editText);
        resetbutton=findViewById(R.id.button);
        goback=findViewById(R.id.goback);
        textView=findViewById(R.id.textsuces);
        container=findViewById(R.id.linearLayout);
        progressBar=findViewById(R.id.progressBar);
        firebaseAuth=FirebaseAuth.getInstance();
        imageView1=findViewById(R.id.icomimage);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eemail =   email.getText().toString();

                if (TextUtils.isEmpty(eemail)){

                    Toast.makeText(ForgotPasswordActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
                }

                else {

                    textView.setVisibility(View.GONE);
                    imageView1.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    // call the function send password of firebase this will send the passsword to your email address
                    firebaseAuth.sendPasswordResetEmail(eemail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(ForgotPasswordActivity.this, "Email Sent Successfully !", Toast.LENGTH_SHORT).show();
                                        imageView1.setImageResource(R.drawable.green_email);
                                        textView.setVisibility(View.VISIBLE);
                                        textView.setTextColor(Color.parseColor("#11A10c"));
                                        email.setText("");
                                        resetbutton.setEnabled(false);
                                        resetbutton.setBackgroundColor(Color.parseColor("#b5261c"));
                                        resetbutton.setTextColor(Color.parseColor("#cccccc"));

                                    }else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                                        textView.setText(error);
                                        textView.setTextColor(getResources().getColor(R.color.peach));
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });


                }}
        });

    }
}