package com.ticket.foru.User;

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
import com.squareup.picasso.Picasso;
import com.ticket.foru.Fragment.UserItemFragment;
import com.ticket.foru.Utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class EditItemActivity extends AppCompatActivity {
    private EditText et_item_name,et_item_original_price, et_item_quantity, et_description,et_item_asking_price,et_item_date;

    DatabaseReference myRef;
    private Dialog loadingDialog;
    ImageView imageView;
    StorageReference mRef;
    private Uri imgUri =null;
    int position=0;
    DatePickerDialog datePicker;
    final Calendar myCalendar= Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        mRef= FirebaseStorage.getInstance().getReference("item_images");
        position= Constant.INDEX;
        et_item_name=findViewById(R.id.et_item_name);
        imageView=findViewById(R.id.itemPic);
        et_item_original_price=findViewById(R.id.et_item_original_price);
        et_item_quantity=findViewById(R.id.et_item_quantity);
        et_description=findViewById(R.id.et_description);
        et_item_date=findViewById(R.id.et_item_date);
        et_item_asking_price=findViewById(R.id.et_item_asking_price);
        /////loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        et_item_name.setText(UserItemFragment.itemArrayList.get(position).getName());
        et_item_original_price.setText(UserItemFragment.itemArrayList.get(position).getOriginalPrice());
        et_item_asking_price.setText(UserItemFragment.itemArrayList.get(position).getAskingPrice());
        et_item_date.setText(UserItemFragment.itemArrayList.get(position).getDate());
        et_description.setText(UserItemFragment.itemArrayList.get(position).getDescription());
        et_item_quantity.setText(UserItemFragment.itemArrayList.get(position).getQuantity());
        Picasso.with(this)
                .load(UserItemFragment.itemArrayList.get(position).getPic())
                .placeholder(R.drawable.progress_animation)
                .fit()
                .centerCrop()
                .into(imageView);

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
                datePicker =  new DatePickerDialog(EditItemActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });
    }

    public void updateRecord(View view) {
        loadingDialog.show();

        myRef= FirebaseDatabase.getInstance().getReference("Items").child(UserItemFragment.itemArrayList.get(position).getItemId());
        myRef.child("Name").setValue(et_item_name.getText().toString());
        myRef.child("AskingPrice").setValue(et_item_asking_price.getText().toString());
        myRef.child("OriginalPrice").setValue(et_item_original_price.getText().toString());
        myRef.child("Date").setValue(et_item_date.getText().toString());
        myRef.child("Quantity").setValue(et_item_quantity.getText().toString());
        myRef.child("Description").setValue(et_description.getText().toString());

        if(imgUri==null && imageView ==null) {
            // Set default image URL in Realtime Database
            String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/ticket4u-570ee.appspot.com/o/item_images%2Fitem_default-image.jpg?alt=media&token=7ab703fb-ad8e-4f6b-bb8c-6ebc07ee4683";
            myRef.child("ItemImage").setValue(defaultImageUrl);
        }else{
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
                            Toast.makeText(EditItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        loadingDialog.dismiss();
        Toast.makeText(EditItemActivity.this,"item updated successful",Toast.LENGTH_LONG).show();
        finish();
    }

    public void addPicture(View view)
    {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
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
        if (!PermissionsUtil.hasPermissions(EditItemActivity.this)) {
            ActivityCompat.requestPermissions(EditItemActivity.this, PermissionsUtil.permissions(),
                    451);
        }else{
            selectImageFromGallery();
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",EditItemActivity.this.getPackageName(), null);
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
        ContentResolver cr=EditItemActivity.this.getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}