package com.example.ticket4u.Admin;

import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ticket4u.MainActivity;
import com.example.ticket4u.R;
import com.example.ticket4u.Utils.PermissionsUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/* This activity control the adding of new categories */
public class AddCategoryActivity extends AppCompatActivity {
    EditText et_catrgory; //edit text for getting the name of the new category.
    ImageView imageView;
    StorageReference mRef; //used as instance to get data from the firebase.
    String subCategory="";
    private Uri imgUri =null;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        et_catrgory=findViewById(R.id.et_catrgory);
        imageView=findViewById(R.id.image);

        /* loading dialog */
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mRef= FirebaseStorage.getInstance().getReference("category_images");
        subCategory=getIntent().getStringExtra("name");// get the name of the category passed from the CategoryActivity.
        String newTitle = "Admin->Add Category";
        setTitle(newTitle); // set new title

        /* when press on image location will add new image */
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getStringExtra("name").equals("empty")){
                addImage();
                }
            }
        });
        if(getIntent().getStringExtra("name").equals("empty")){
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.upload_icon));
        }
        else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        }
    }

    /* this method calls when click on save button
    Saving a record to the Firebase Realtime Database.
    The record represents a category and consists of a category name and possibly an image.
    The record is saved as a child of the "Category" node in the Realtime Database. */
    public void saveCategoryRecord(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if(et_catrgory.getText().toString().isEmpty()){
                et_catrgory.setError("required");//will display error sign with text "required"
            }
            else  if(getIntent().getStringExtra("name").equals("empty")){
                if(imgUri==null){
                    Toast.makeText(AddCategoryActivity.this,"image is required", Toast.LENGTH_SHORT).show();
                }
                else {
                    addRecord();
                }
            }
            else { //save the record as child of "Category" node in the Realtime database.
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Category")
                        .child(getIntent().getStringExtra("name")).child("SubCategory").child(et_catrgory.getText().toString());
                databaseReference.child("SubName").setValue(et_catrgory.getText().toString());
                finish(); //finishes the current Activity and removes it from the back stack.
            }
        }
    }

    /* This method adding a record to a Firebase Realtime Database.
     It does this by uploading an image to Firebase Storage
     and then saving the image's URL and a category name in the Realtime Database. */
    public void addRecord(){
        loadingDialog.show();
        StorageReference storageReference = mRef.child(System.currentTimeMillis() + "." + getFileEx(imgUri));
        storageReference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Category").child(et_catrgory.getText().toString());
                        databaseReference.child("Name").setValue(et_catrgory.getText().toString());
                        databaseReference.child("Image").setValue(downloadUrl.toString());
                        loadingDialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismiss();
                        Toast.makeText(AddCategoryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });

    }

    /* This method is used to select an image from the device external storage */
    public void selectImageFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider. MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    /* This code is checking for permissions to access the device's external storage and,
     if granted, allows the user to select an image from the device's gallery.
     If the permissions are permanently denied, a settings dialog is shown to the user. */
    public  void addImage(){
        if (!PermissionsUtil.hasPermissions(AddCategoryActivity.this)) {
            ActivityCompat.requestPermissions(AddCategoryActivity.this, PermissionsUtil.permissions(),
                    451);
        }else{
            selectImageFromGallery();
        }
    }

    /* This method will open the settings for the app when called.
     It does this by creating an Intent with the action Settings.ACTION_APPLICATION_DETAILS_SETTINGS.
     This action will open the settings for a specific app when given the package name of the app. */
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",AddCategoryActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            imgUri  = data.getData();
            imageView.setImageURI(imgUri);
        }
    }

    // get the extension of file
    private String getFileEx(Uri uri){
        ContentResolver cr=AddCategoryActivity.this.getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
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
                setAdminLoginStatus(AddCategoryActivity.this,false);
                setUserLoginStatus(AddCategoryActivity.this,false);
                startActivity(new Intent(AddCategoryActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}