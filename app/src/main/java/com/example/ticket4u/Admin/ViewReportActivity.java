package com.example.ticket4u.Admin;

import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket4u.MainActivity;
import com.example.ticket4u.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewReportActivity extends AppCompatActivity {
    private Dialog loadingDialog;
    TextView report_text;
    String reportedItem="";
    ArrayList<String> reportedArrayList =new ArrayList<String>();
    DatabaseReference myRef;//used as instance to get data from the firebase.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        report_text=findViewById(R.id.report_text);

        /* loading dialog */
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        reportedItem=getIntent().getStringExtra("name"); // get the name which pass from the ReportActivity.

        /* This code is creating a DatabaseReference object that points to "reported items" in the Firebase Realtime Database. */
        myRef=FirebaseDatabase.getInstance().getReference("Items").child(getIntent().getStringExtra("itemId")).child("Report");

        String newTitle = "Report->" + reportedItem;
        setTitle(newTitle);//set new title.
    }

    /* part of the life cycle of an activity */
    @Override
    protected void onStart() {
        getData();
        super.onStart();
    }

    /* The getData() method is called to fetch data from the database. This data is then used to populate the RecyclerView. */
    public void getData(){
        loadingDialog.show();// The loadingDialog is displayed while the data is being fetched.
        reportedArrayList=new ArrayList<String>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    /* create a menu in the top right corner of the activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* The functionality of the menu */
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