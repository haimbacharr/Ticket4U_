package com.example.ticket4u.Admin;

import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ticket4u.MainActivity;
import com.example.ticket4u.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
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
                setAdminLoginStatus(AdminActivity.this,false);
                setUserLoginStatus(AdminActivity.this,false);
                startActivity(new Intent(AdminActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void viewCategory(View view) {
        startActivity(new Intent(this,CategoryActivity.class));
    }

    public void viewItems(View view) {
        startActivity(new Intent(this,AdminMainActivity.class));
    }

    public void viewReports(View view) {
        startActivity(new Intent(this,ReportActivity.class));

    }
}