package com.ticket.foru.User;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket4u.R;
import com.ticket.foru.Utils.PermissionsUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ticket.foru.Fragment.SelectCategoryFragment;
import com.ticket.foru.Utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {
    private EditText et_item_name,et_item_asking_price,et_item_original_price, et_item_quantity, et_description,et_item_date;

    DatabaseReference myRef;
    private Dialog loadingDialog;
    ImageView imageView;
    StorageReference mRef;
    private Uri imgUri =null;
    DatePickerDialog datePicker;
    final Calendar myCalendar= Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        mRef= FirebaseStorage.getInstance().getReference("item_images");

        et_item_name=findViewById(R.id.et_item_name);
        imageView=findViewById(R.id.itemPic);
        et_item_original_price=findViewById(R.id.et_item_original_price);
        et_item_asking_price=findViewById(R.id.et_item_asking_price);
        et_item_quantity=findViewById(R.id.et_item_quantity);
        et_description=findViewById(R.id.et_description);
        et_item_date=findViewById(R.id.et_item_date);
        /////loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                String myFormat="MM/dd/yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                et_item_date.setText(dateFormat.format(myCalendar.getTime()));
            }
        };

        et_item_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                datePicker =  new DatePickerDialog(AddItemActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });
    }

    public void saveRecord(View view) {
        loadingDialog.show();
        try {
            String id =createFavId().substring(0,8);
            myRef=  FirebaseDatabase.getInstance().getReference("Items").child(id);
            myRef.child("Name").setValue(et_item_name.getText().toString());
            myRef.child("ItemId").setValue(id);
            myRef.child("Category").setValue(SelectCategoryFragment.CATEGORY);
            myRef.child("SubCategory").setValue(SelectCategoryFragment.SUBCATEGORY);
            myRef.child("UserId").setValue(Constant.getUserId(AddItemActivity.this));
            myRef.child("AskingPrice").setValue(et_item_asking_price.getText().toString());
            myRef.child("OriginalPrice").setValue(et_item_original_price.getText().toString());
            myRef.child("Date").setValue(et_item_date.getText().toString());
            myRef.child("Quantity").setValue(et_item_quantity.getText().toString());
            myRef.child("Description").setValue(et_description.getText().toString());
            myRef.child("Sold").setValue("not");
            myRef.child("Number").setValue(Constant.getUserNumber(AddItemActivity.this));
            myRef.child("City").setValue(Constant.getUserCity(AddItemActivity.this));
            myRef.child("latitude").setValue(Constant.getUserLatitude(AddItemActivity.this));
            myRef.child("longitude").setValue(Constant.getUserLongitude(AddItemActivity.this));

            if(imgUri == null) {
                // Set default image URL in Realtime Database
                String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/ticket4u-570ee.appspot.com/o/item_images%2Fitem_default-image.jpg?alt=media&token=7ab703fb-ad8e-4f6b-bb8c-6ebc07ee4683";
                myRef.child("ItemImage").setValue(defaultImageUrl);
            }
            else {
                StorageReference storageReference = mRef.child(System.currentTimeMillis() + "." + getFileEx(imgUri));
                storageReference.putFile(imgUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();
                                myRef.child("ItemImage").setValue(downloadUrl.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismiss();
                                Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        });
            }
            loadingDialog.dismiss();
            String TOPIC = "/topics/"+ SelectCategoryFragment.CATEGORY; //topic has to match what the receiver subscribed to
            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();
            String title = "New Ticket";
            String message = "There is new ticket in the system from category "+ SelectCategoryFragment.CATEGORY;
            try {
                notifcationBody.put("title", title);
                notifcationBody.put("message", message);
                notification.put("to", TOPIC);
                notification.put("priority", "high");
                notification.put("data", notifcationBody);
            } catch (JSONException e) {
                Log.e(TAG, "onCreate: " + e.getMessage());
            }
            Notification(notification);

            Toast.makeText(AddItemActivity.this,"item Add successful",Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            loadingDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void Notification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                response -> Log.i(TAG, "onResponse: " + response.toString()),
                error -> {
                    Toast.makeText(AddItemActivity.this, "Request error", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onErrorResponse: Didn't work");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=AAAAozUXzBk:APA91bFYDIfqVeIW23gfPuz2CUUk_Gx0tZhMHCpLsjLbsXyGUM2TSGZrQhQBcRjvzfkHYvHYPoIy6ahfDkml5yahcaMCI07FO-ajk0FK_CvylP8nWKEZHtbtr0pT5OcKT_o-zKQJN-_i");
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public String createFavId() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public void addPicture(View view)
    {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePicture.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePicture, 2);
                        }
                    } else if (options[item].equals("Choose from Gallery")) {
                        addImage();
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
    }

    public void selectImageFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider. MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    public  void addImage(){
        if (!PermissionsUtil.hasPermissions(AddItemActivity.this)) {
            ActivityCompat.requestPermissions(AddItemActivity.this, PermissionsUtil.permissions(),
                    451);
        }else{
            selectImageFromGallery();
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",AddItemActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            imgUri = data.getData();
            imageView.setImageURI(imgUri);
        }
        if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();

            File imgFile = new File(getCacheDir(), UUID.randomUUID() + ".jpg");

            try {
                imgFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(imgFile);
                fos.write(dataBAOS);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imgUri = Uri.fromFile(imgFile);
            imageView.setImageURI(imgUri);
        }
    }
    // get the extension of file
    private String getFileEx(Uri uri){
        ContentResolver cr=AddItemActivity.this.getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}