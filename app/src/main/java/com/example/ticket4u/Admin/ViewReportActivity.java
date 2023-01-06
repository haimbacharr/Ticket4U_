package com.example.ticket4u.Admin;

import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ticket4u.MainActivity;
import com.example.ticket4u.Model.Item;
import com.example.ticket4u.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewReportActivity extends AppCompatActivity {
    private Dialog loadingDialog;
    TextView report_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        report_text=findViewById(R.id.report_text);
        //loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        getData();
    }
    public void getData(){
        loadingDialog.dismiss();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items").child(getIntent().getStringExtra("itemId")).child("Report");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String report=  dataSnapshot.child("itemReport").getValue(String.class);
              if(report==null){
                  report_text.setText("no report found");
              }
              else {
                  report_text.setText(report);
              }

                loadingDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.logout:
                setAdminLoginStatus(ViewReportActivity.this,false);
                setUserLoginStatus(ViewReportActivity.this,false);
                startActivity(new Intent(ViewReportActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}