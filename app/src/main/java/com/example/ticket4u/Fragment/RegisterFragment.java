package com.example.ticket4u.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticket4u.Model.Category;
import com.example.ticket4u.R;
import com.example.ticket4u.User.AccountActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RegisterFragment extends Fragment  {
    private EditText etRegisterEmail,et_user_name, etRegisterPassword, etRegisterConfirmPassword
           ,et_register_country,et_register_state=null,et_register_city=null,et_user_number;
    private FirebaseAuth firebaseAuth;
    DatabaseReference myRef;
    TextView tv_login;
    Button btnRegister;
    private Dialog loadingDialog;
    ImageView imageView;
    StorageReference mRef;
    private Uri imgUri;
    ArrayList<String> stringArrayList=new ArrayList<String>();
    Spinner spinner;
    ArrayAdapter arrayAdapter;
    String category;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_LOCATION = 1;
    protected LocationManager locationManager;
    String latitude="",longitude=""; //values for location

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        ActivityCompat.requestPermissions(getActivity(),
                                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // if gps off
            OnGPS();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // method to get the location
        getLastLocation();

        mRef= FirebaseStorage.getInstance().getReference("profile_images");
        imageView=view.findViewById(R.id.userPic);
        et_user_number=view.findViewById(R.id.et_user_number);
        et_register_country=view.findViewById(R.id.et_register_country);
        et_register_state=view.findViewById(R.id.et_register_state);
        et_register_city=view.findViewById(R.id.et_register_city);
        /////loading dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        firebaseAuth = FirebaseAuth.getInstance();
        etRegisterEmail = view.findViewById(R.id.et_register_email);
        etRegisterPassword = view.findViewById(R.id.et_register_password);
        etRegisterConfirmPassword = view.findViewById(R.id.et_register_confirm_password);
        et_user_name = view.findViewById(R.id.et_user_name);
        tv_login=view.findViewById(R.id.tv_login);
        spinner =view.findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //set the spinner for select interested category
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                category = stringArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() { //return to login page
            @Override
            public void onClick(View view) {
                ((AccountActivity)getActivity()).showLoginScreen();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 addImage();
            }
        });

        btnRegister = view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                String email = etRegisterEmail.getText().toString();
                String name = et_user_name.getText().toString();
                String password = etRegisterPassword.getText().toString();
                String confirm_password = etRegisterConfirmPassword.getText().toString();
                String user_number =et_user_number.getText().toString();
                String register_country =et_register_country.getText().toString(); //3 optional fields
                String register_state = et_register_state.getText().toString();
                String register_city = et_register_city.getText().toString();
                if (validate(email,name, password, confirm_password,user_number)) requestRegister(email, password);
            }
        });

        getData();
        return view;
    }

    private void getLastLocation() { //get the location and update the latitude and longitude of user.
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    latitude=location.getLatitude()+""; //update the location we get.
                    longitude=location.getLongitude()+"";
                   // Toast.makeText(getContext(), "Latitude:" + location.getLatitude() + ", Longitude:" +location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestNewLocationData() {
        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude=mLastLocation.getLatitude()+"";
            longitude=mLastLocation.getLongitude()+"";
           // Toast.makeText(getContext(), "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" +mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    };

    private void OnGPS() { //dialog for turn on gps.
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //if yes open location settings
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { //finish dialog.
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void getData(){
        loadingDialog.show();
        stringArrayList.clear();
        stringArrayList.add("General");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(!stringArrayList.contains(dataSnapshot1.child("Name").getValue(String.class))){
                        stringArrayList.add(dataSnapshot1.child("Name").getValue(String.class));
                    }
                }
                loadingDialog.dismiss();
                arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,stringArrayList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private boolean validate(String email, String name, String password, String confirm_password, String user_number) {
        if (email.isEmpty()) etRegisterEmail.setError("Enter email!");
        else if (user_number.isEmpty()) et_user_number.setError("Enter phone number!");
        else if (name.isEmpty()) et_user_name.setError("Enter name!");
        else if (!email.contains("@")||!email.contains(".")) etRegisterEmail.setError("Enter valid email!");
        else if (password.isEmpty()) etRegisterPassword.setError("Enter password!");
        else if (password.length()<6) etRegisterPassword.setError("Password must be at least 6 characters!");
        else if (confirm_password.isEmpty()) etRegisterConfirmPassword.setError("Enter password!");
        else if (!password.equals(confirm_password)) etRegisterConfirmPassword.setError("Password not matched!");
        else return true;
        return false;
    }

    private void requestRegister(String email, String password) {
        loadingDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getCreateUserWithEmailOnClickListener(email));
    }

    private OnCompleteListener<AuthResult> getCreateUserWithEmailOnClickListener(String email) {
        return task -> {
            if (task.isSuccessful()) {
                add();
            } else {
                loadingDialog.dismiss();
                Toast.makeText(getContext(),"Registration failed!",Toast.LENGTH_LONG).show();
            }
        };
    }

    private void add() {
        getLastLocation();
        String id = firebaseAuth.getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("User").child(id);
        myRef.child("Name").setValue(et_user_name.getText().toString());
        myRef.child("UserId").setValue(id);
        myRef.child("Mail").setValue(etRegisterEmail.getText().toString());
        myRef.child("Country").setValue(et_register_country.getText().toString());
        myRef.child("City").setValue(et_register_city.getText().toString());
        myRef.child("State").setValue(et_register_state.getText().toString());
        myRef.child("Category").setValue(category);
        myRef.child("PhoneNumber").setValue(et_user_number.getText().toString());
        myRef.child("Latitude").setValue(latitude);
        myRef.child("Longitude").setValue(longitude);
        if(imgUri == null) {
            // Set default image URL in Realtime Database
            String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/ticket4u-bd3b6.appspot.com/o/profile_images%2Fdefault-image.jpg?alt=media&token=fdc5e3aa-4fa2-44ad-8940-9ed400662cff";
            myRef.child("UserImage").setValue(defaultImageUrl);
        }
        // Check if imgUri is not null -> set real image.
        else{
            // Upload image to Firebase Storage
            StorageReference storageReference = mRef.child(System.currentTimeMillis() + "." + getFileEx(imgUri));
            storageReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get download URL for image
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            // Set "UserImage" child in Realtime Database to download URL
                            myRef.child("UserImage").setValue(downloadUrl.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        loadingDialog.dismiss();
        Toast.makeText(getContext(),"Registration successful",Toast.LENGTH_LONG).show();
        ((AccountActivity)getActivity()).showLoginScreen();
    }

    public void selectImageFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    public void addImage(){
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
            imgUri = data.getData();
            imageView.setImageURI(imgUri);
        }
    }

    // get the extension of file
    private String getFileEx(Uri uri){
        ContentResolver cr=getContext().getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}