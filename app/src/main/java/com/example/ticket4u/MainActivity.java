package com.example.ticket4u;

import static com.example.ticket4u.Utils.Constant.ShowMessageDialogWithOkBtn;
import static com.example.ticket4u.Utils.Constant.getUserLoginStatus;
import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ticket4u.Fragment.HomeFragment;
import com.example.ticket4u.Fragment.LoginFragment;
import com.example.ticket4u.User.AboutActivity;
import com.example.ticket4u.User.AccountActivity;
import com.example.ticket4u.User.AddItemActivity;
import com.example.ticket4u.User.PreferedItemActivity;
import com.example.ticket4u.User.SelectCategoryActivity;
import com.example.ticket4u.User.UpdateProfileActivity;
import com.example.ticket4u.User.UserItemActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView =findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toogle=new ActionBarDrawerToggle(this,drawer, (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar),R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new HomeFragment()).commit();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id._logout:
                        setAdminLoginStatus(MainActivity.this,false);
                        setUserLoginStatus(MainActivity.this,false);
                        finish();
                        startActivity(getIntent());
                        break;
                    case R.id._home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new HomeFragment()).commit();
                        break;
                        case R.id._login:
                            startActivity(new Intent(MainActivity.this, AccountActivity.class));
                            break;
                            case R.id._about:
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    case R.id.add_ticket:
                        startActivity(new Intent(MainActivity.this, SelectCategoryActivity.class));
                        break;
                    case R.id.profile_screen:
                        startActivity(new Intent(MainActivity.this, UpdateProfileActivity.class));
                        break;
                    case R.id.personal_item:
                        startActivity(new Intent(MainActivity.this, UserItemActivity.class));
                        break;
                    case R.id.Prefeard_item:
                        startActivity(new Intent(MainActivity.this, PreferedItemActivity.class));
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main1, menu);
        MenuItem item = menu.findItem(R.id.logout);
        MenuItem item1 = menu.findItem(R.id.login);
        // if not user login
        if(!getUserLoginStatus(MainActivity.this)){
            item.setVisible(false);//
            item1.setVisible(true);

                hideOption();

        }
        // if user  login
        else {
            item.setVisible(true);//
            item1.setVisible(false);



        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.logout:
                setAdminLoginStatus(MainActivity.this,false);
                setUserLoginStatus(MainActivity.this,false);
                finish();
                startActivity(getIntent());
                return true;
            case R.id.login:
                startActivity(new Intent(MainActivity.this, AccountActivity.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void hideOption(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        Menu menu =navigationView.getMenu();

        MenuItem profile = menu.findItem(R.id.profile_screen);
        profile.setVisible(false);
        MenuItem addticket = menu.findItem(R.id.add_ticket);
        addticket.setVisible(false);
        MenuItem personal_item = menu.findItem(R.id.personal_item);
        personal_item.setVisible(false);
        MenuItem Prefeard_item = menu.findItem(R.id.Prefeard_item);
        Prefeard_item.setVisible(false);
        MenuItem _logout = menu.findItem(R.id._logout);
        _logout.setVisible(false);
    }

}