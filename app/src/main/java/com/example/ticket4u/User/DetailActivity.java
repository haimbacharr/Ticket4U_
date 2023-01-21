package com.example.ticket4u.User;

import static com.example.ticket4u.Fragment.HomeFragment.itemArrayList;
import static com.example.ticket4u.Utils.Constant.getUserId;
import static com.example.ticket4u.Utils.Constant.getUserLoginStatus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ticket4u.Map.MapsActivity;
import com.example.ticket4u.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private EditText et_item_name,et_item_price, et_item_quantity, et_description,et_user_name
            ,et_item_asking_price,et_item_date,et_category,et_sub_category,et_number,et_user_address;

    private Dialog loadingDialog;
    ImageView imageView;
    int index;
    String longitude="",latitude="";
    String dial;
    Button btn_like_dislike;
    boolean status=false;
    String address="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        et_item_name=findViewById(R.id.et_item_name);
        imageView=findViewById(R.id.itemPic);
        et_item_price=findViewById(R.id.et_item_price);
        et_item_quantity=findViewById(R.id.et_item_quantity);
        et_description=findViewById(R.id.et_description);
        et_user_name=findViewById(R.id.et_user_name);
        et_item_asking_price=findViewById(R.id.et_item_asking_price);
        et_item_date=findViewById(R.id.et_item_date);
        et_sub_category=findViewById(R.id.et_sub_category);
        et_category=findViewById(R.id.et_category);
        et_number=findViewById(R.id.et_user_number);
        btn_like_dislike=findViewById(R.id.btn_like_dislike);
        et_user_address=findViewById(R.id.et_user_address);
        status=getIntent().getBooleanExtra("status",false);
        index=getIntent().getIntExtra("index",-1);
        /////loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Picasso.with(this)
                .load(itemArrayList.get(index).getPic())
                .placeholder(R.drawable.progress_animation)
                .fit()
                .centerCrop()
                .into(imageView);

        et_item_name.setText(itemArrayList.get(index).getName());
        et_item_price.setText(itemArrayList.get(index).getOriginalPrice());
        et_item_quantity.setText(itemArrayList.get(index).getQuantity());
        et_description.setText(itemArrayList.get(index).getDescription());
        et_item_date.setText(itemArrayList.get(index).getDate());
        et_item_asking_price.setText(itemArrayList.get(index).getAskingPrice());
        et_sub_category.setText(itemArrayList.get(index).getSubCategory());
        et_category.setText(itemArrayList.get(index).getCategory());
        et_number.setText(itemArrayList.get(index).getNumber());
        getUserRecord(itemArrayList.get(index).getUserId());

        if(status){
            btn_like_dislike.setText("Dislike");
        }
        else {
            btn_like_dislike.setText("Like");
        }
        if(getUserLoginStatus(this)){
            btn_like_dislike.setVisibility(View.VISIBLE);
        }
        else {
            btn_like_dislike.setVisibility(View.GONE);
        }

        btn_like_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_like_dislike.getText().toString().equals("Dislike")){
                    btn_like_dislike.setText("Like");
                    removeToFav();
                }
                else {
                    btn_like_dislike.setText("Dislike");
                    addToFav();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getUserRecord(String id){
        loadingDialog.show();
       DatabaseReference myRef=  FirebaseDatabase.getInstance().getReference().child("User").child(id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                et_user_name.setText(dataSnapshot.child("Name").getValue(String.class));
               longitude= dataSnapshot.child("Longitude").getValue(String.class);
                latitude= dataSnapshot.child("Latitude").getValue(String.class);
                address=address+dataSnapshot.child("Country").getValue(String.class)+","
                        +dataSnapshot.child("Address").getValue(String.class)+","
                        +dataSnapshot.child("City").getValue(String.class);
                et_user_address.setText(address);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openMap(View view) {
         startActivity(new Intent(this, MapsActivity.class)
                 .putExtra("latitude",latitude)
                 .putExtra("longitude",longitude));
    }

    public void addReport(View view) {
          if(itemArrayList.get(index).getUserId().equals(getUserId(this))){
              Toast.makeText(DetailActivity.this, "you can not add report ", Toast.LENGTH_SHORT).show();
          }
          else {
              startActivity(new Intent(this,AddReportActivity.class)
                      .putExtra("id",itemArrayList.get(index).getItemId()));
              finish();
          }

    }

    public void addToFav(){
        String itemId=itemArrayList.get(index).getItemId();
        DatabaseReference  myRef=  FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(DetailActivity.this)).child(itemId);
        myRef.child("itemId").setValue(itemId);
    }
    public void removeToFav(){
        String itemId=itemArrayList.get(index).getItemId();
        DatabaseReference  myRef=  FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(DetailActivity.this)).child(itemId);
        myRef.removeValue();
    }

    public void addCall(View view) {
        dial="tel:" +itemArrayList.get(index).getNumber();
        makePhoneCall();
    }

    private void makePhoneCall(){
        if(ContextCompat.checkSelfPermission(DetailActivity.this,
                android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DetailActivity.this,
                    new String[]{  android.Manifest.permission.CALL_PHONE},1);
        }
        else {
          showAlert();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==requestCode){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }
        }
    }

    public void showAlert(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Call Alert");
        adb.setMessage("Do you want to make call "+et_user_name.getText().toString()+" ?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
        });
        adb.show();
    }
}