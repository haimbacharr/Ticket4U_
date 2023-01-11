package com.example.ticket4u.Admin;

import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket4u.MainActivity;
import com.example.ticket4u.R;

/* This is the admin main acvitiy after we logged in as an admin */

public class AdminActivity extends AppCompatActivity {

    /* this method will display the main screen of the admin after he will login */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    /* This method will show the menu on the top left corner */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* This method will show the functionality of the menu we create */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.logout:
                setAdminLoginStatus(AdminActivity.this,false);
                setUserLoginStatus(AdminActivity.this,false);
                startActivity(new Intent(AdminActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* This method will start new activity with all of the categories we have in our system */
    public void viewCategory(View view) {
        startActivity(new Intent(this,CategoryActivity.class));
    }

    /* This method will start new activity with all of the items we have in our system */
    public void viewItems(View view) {
        startActivity(new Intent(this,AdminMainActivity.class));
    }

    /* This method will start new activity with all of the reported items we have in our system */
    public void viewReports(View view) {
        startActivity(new Intent(this,ReportActivity.class));
    }
}