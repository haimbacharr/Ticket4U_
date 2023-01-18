package com.example.ticket4u;

import static android.content.ContentValues.TAG;
import static com.example.ticket4u.Utils.Constant.getUserLoginStatus;
import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ticket4u.Fragment.AboutFragment;
import com.example.ticket4u.Fragment.HomeFragment;
import com.example.ticket4u.Fragment.PreferedItemFragment;
import com.example.ticket4u.Fragment.SelectCategoryFragment;
import com.example.ticket4u.Fragment.UpdateProfileFragment;
import com.example.ticket4u.Fragment.UserItemFragment;
import com.example.ticket4u.User.AccountActivity;
import com.example.ticket4u.Utils.PermissionsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

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
                            startActivity(new Intent(MainActivity.this, AccountActivity.class)
                                    .putExtra("screen","login"));
                            break;
                            case R.id._logout_user:
                            startActivity(new Intent(MainActivity.this, AccountActivity.class)
                            .putExtra("screen","register"));
                            break;

                            case R.id._about:
                                getSupportFragmentManager().beginTransaction().replace(R.id.frag,new AboutFragment()).commit();
                        break;
                    case R.id.add_ticket:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new SelectCategoryFragment()).commit();
                        break;
                    case R.id.profile_screen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new UpdateProfileFragment()).commit();
                        break;
                    case R.id.personal_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new UserItemFragment()).commit();
                        break;
                    case R.id.Prefeard_item:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag,new PreferedItemFragment()).commit();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (!PermissionsUtil.hasPermissions(this)) {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PermissionsUtil.permissions(),
                451);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtil.permissionsGranted(grantResults)) {

        } else
            showSettingsDialog();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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
            hideOptionForLoginUser();

        }
        askNotificationPermission();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                    }
                });

        return true;
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
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
                startActivity(new Intent(MainActivity.this, AccountActivity.class)
                        .putExtra("screen","login"));

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
    public void hideOptionForLoginUser(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        Menu menu =navigationView.getMenu();
        MenuItem _login = menu.findItem(R.id._login);
        _login.setVisible(false);
        MenuItem _register = menu.findItem(R.id._logout_user);
        _register.setVisible(false);
    }
}