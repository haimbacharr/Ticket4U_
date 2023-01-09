package com.example.ticket4u.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.example.ticket4u.Utils.Constant.getUserId;
import static com.example.ticket4u.Utils.Constant.setUsername;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ticket4u.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UpdateProfileFragment extends Fragment {
    private EditText et_register_country, et_register_Address,et_register_city,et_user_number,
        et_user_name;
    DatabaseReference myRef;
    private Dialog loadingDialog;
    ImageView imageView;
    StorageReference mRef;
    private Uri imgUri =null;
    Button btn_update;
    ArrayList<String> stringArrayList=new ArrayList<String>();
    Spinner spinner;
    ArrayAdapter arrayAdapter;
    String category;
    int selectIndex=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        mRef= FirebaseStorage.getInstance().getReference("profile_images");
        et_user_name=view.findViewById(R.id.et_user_name);
        imageView=view.findViewById(R.id.updateUserPic);
        et_user_number=view.findViewById(R.id.et_user_number);
        et_register_country=view.findViewById(R.id.et_register_country);
        et_register_Address =view.findViewById(R.id.et_register_address);
        et_register_city=view.findViewById(R.id.et_register_city);
        /////loading dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        spinner =view.findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //listener for select category
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                category =  stringArrayList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        loadProfile(); //read from firebase
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            } //like register page, for change picture
        });

        btn_update=view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
        return view;
    }

    public void getData(String userCategory){
        stringArrayList.clear();
        stringArrayList.add("General");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){ //add category from db to arraylist
                    if(!stringArrayList.contains(dataSnapshot1.child("Name").getValue(String.class))){
                        stringArrayList.add(dataSnapshot1.child("Name").getValue(String.class));
                        if(userCategory.equals(dataSnapshot1.child("Name").getValue(String.class))){ //if we found the category
                            selectIndex=stringArrayList.size()-1; //update the index of our chosen category
                        }
                    }
                }
                loadingDialog.dismiss();
                arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,stringArrayList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spinner.setAdapter(arrayAdapter);
                spinner.setSelection(selectIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadProfile(){
        loadingDialog.show();
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference().child("User").child(getUserId(getContext()));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                et_user_name.setText(dataSnapshot.child("Name").getValue(String.class));
                et_register_country.setText(dataSnapshot.child("Country").getValue(String.class));
                et_register_city.setText(dataSnapshot.child("City").getValue(String.class));
                et_register_Address.setText(dataSnapshot.child("Address").getValue(String.class));
                et_user_number.setText(dataSnapshot.child("PhoneNumber").getValue(String.class));

                Picasso.with(getContext())
                        .load(dataSnapshot.child("UserImage").getValue(String.class))
                        .placeholder(R.drawable.progress_animation)
                        .fit()
                        .centerCrop()
                        .into(imageView);
                getData(dataSnapshot.child("Category").getValue(String.class)); //load category inside spinner
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // get the extension of file
    private String getFileEx(Uri uri){
        ContentResolver cr=getContext().getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void updateProfile() {
        loadingDialog.show();
        myRef=  FirebaseDatabase.getInstance().getReference().child("User").child(getUserId(getContext()));
        if(imgUri!=null){ //if there is update also in picture
            StorageReference storageReference = mRef.child(System.currentTimeMillis() + "." + getFileEx(imgUri));
            storageReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            myRef.child("Name").setValue(et_user_name.getText().toString());
                            myRef.child("Country").setValue(et_register_country.getText().toString());
                            myRef.child("City").setValue(et_register_city.getText().toString());
                            myRef.child("Address").setValue(et_register_Address.getText().toString());
                            myRef.child("Category").setValue(category);
                            myRef.child("PhoneNumber").setValue(et_user_number.getText().toString());
                            myRef.child("UserImage").setValue(downloadUrl.toString());
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(),"profile updated", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });

        }
        else { //update just in regular fields without picture
            myRef.child("Name").setValue(et_user_name.getText().toString());
            myRef.child("Country").setValue(et_register_country.getText().toString());
            myRef.child("City").setValue(et_register_city.getText().toString());
            myRef.child("Address").setValue(et_register_Address.getText().toString());
            myRef.child("Category").setValue(category);
            myRef.child("PhoneNumber").setValue(et_user_number.getText().toString());
            setUsername(getContext(),et_user_name.getText().toString());
            Toast.makeText(getContext(),"profile updated", Toast.LENGTH_LONG).show();
            loadingDialog.dismiss();
        }
    }

    public void selectImageFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider. MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    public  void addImage(){
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            selectImageFromGallery();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        Uri uri = Uri.fromParts("package",getContext().getPackageName(), null);
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
}