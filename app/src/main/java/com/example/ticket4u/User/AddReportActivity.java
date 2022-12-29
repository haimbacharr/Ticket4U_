package com.example.ticket4u.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ticket4u.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddReportActivity extends AppCompatActivity {
        EditText et_report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        et_report=findViewById(R.id.et_report);
    }

    public void saveReport(View view) {

        if(et_report.getText().toString().isEmpty()){
            et_report.setError("required");
        }
        else {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items").child(getIntent().getStringExtra("id")).child("Report");
             databaseReference.child("itemReport").setValue(et_report.getText().toString());
            Toast.makeText(AddReportActivity.this, "report submited", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}