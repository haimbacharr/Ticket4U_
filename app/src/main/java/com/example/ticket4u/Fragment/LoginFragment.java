package com.example.ticket4u.Fragment;

import static com.example.ticket4u.Utils.Constant.*;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.ticket4u.Admin.AdminActivity;
import com.example.ticket4u.MainActivity;
import com.example.ticket4u.R;
import com.example.ticket4u.User.AccountActivity;
import com.example.ticket4u.User.ForgotPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginFragment extends Fragment {

    private EditText etLoginEmail, etLoginPassword;
    TextView tv_new_register,tv_forgot_password;
    private Dialog loadingDialog;
    private FirebaseAuth firebaseAuth;

    DatabaseReference myRef,databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Admin");
        firebaseAuth = FirebaseAuth.getInstance();
        tv_forgot_password=view.findViewById(R.id.tv_forgot_password);
        /////loading dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        etLoginEmail =view.findViewById(R.id.et_login_email);
        etLoginPassword = view.findViewById(R.id.et_login_password);
        tv_new_register=view.findViewById(R.id.tv_new_register);
        Button btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                String email = etLoginEmail.getText().toString();
                String password = etLoginPassword.getText().toString();
                // call the validate function and then request
                if (validate(email, password)) requestLogin(email, password);
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ForgotPasswordActivity.class)); //open reset password page
            }
        });

        tv_new_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {((AccountActivity)getActivity()).showSignUpScreen(); //open register page
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private boolean validate(String email, String password) { //check if email and password is valid
        if (email.isEmpty()) etLoginEmail.setError("Enter email!");
        else if (!email.contains("@")||!email.contains(".")) etLoginEmail.setError("Enter valid email!");
        else if (password.isEmpty()) etLoginPassword.setError("Enter password!");
        else if (password.length()<6) etLoginPassword.setError("Password must be at least 6 characters!");
        else return true;
        return false;
    }

    private void requestLogin(String email, String password) { //check login details
        loadingDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get the admin email and password from the firebase
                String dbmail = dataSnapshot.child("AdminEmail").getValue().toString(); //check the values in firebase for admin
                String dbpass = dataSnapshot.child("AdminPassword").getValue().toString();

                // validate the email and password
                if (email.equals(dbmail) && password.equals(dbpass)) { //check if admin
                    // open the admin dashboard screen
                    loadingDialog.dismiss();
                    setAdminLoginStatus(getContext(),true);
                    startActivity(new Intent(getContext(), AdminActivity.class)); // open admin
                    getActivity().finish();
                }
                else { //check if user
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) { //wrong details
                                loadingDialog.dismiss();
                                Toast.makeText(getContext(), "wrong mail or password" + task.getException(), Toast.LENGTH_LONG).show();
                            } else if (task.isSuccessful()) { //email and password = OK
                                getData(); //get the data for this user from db.
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getData(){
        final String user_m=etLoginEmail.getText().toString().trim();
        myRef=FirebaseDatabase.getInstance().getReference().child("User");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(user_m.equals(dataSnapshot1.child("Mail").getValue(String.class))) {
                        setUserId(getContext(),dataSnapshot1.child("UserId").getValue(String.class));
                        setUsername(getContext(),dataSnapshot1.child("Name").getValue(String.class));
                        setUserNumber(getContext(),dataSnapshot1.child("PhoneNumber").getValue(String.class));
                        setUserCity(getContext(),dataSnapshot1.child("City").getValue(String.class));
                        setUserLoginStatus(getContext(), true); //turn on the login status
                        setUserEmail(getContext(),etLoginEmail.getText().toString().trim());
                        setUserInterest(getContext(),dataSnapshot1.child("Category").getValue(String.class));
                        setUserLatitude(getContext(),dataSnapshot1.child("Latitude").getValue(String.class));
                        setUserLongitude(getContext(),dataSnapshot1.child("Longitude").getValue(String.class));
                        loadingDialog.dismiss();
                        openHomeActivity(); //go to home screen (user)
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void openHomeActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
}