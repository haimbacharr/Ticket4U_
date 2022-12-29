package com.example.ticket4u.Admin;


import static com.example.ticket4u.Utils.Constant.setUserEmail;
import static com.example.ticket4u.Utils.Constant.setUserId;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUsername;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticket4u.Model.Category;
import com.example.ticket4u.R;
import com.example.ticket4u.User.UpdateProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayAdapter arrayAdapter;
    ArrayList<Category> stringArrayList =new ArrayList<Category>();

    DatabaseReference myRef;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        /////loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
         myRef=  FirebaseDatabase.getInstance().getReference().child("Category");

        recyclerView=findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }
    @Override
    protected void onStart() {
        getData();
        super.onStart();
    }

    public void getData(){
        loadingDialog.show();
           stringArrayList=new ArrayList<Category>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(!stringArrayList.contains(dataSnapshot1.child("Name").getValue(String.class))){
                        stringArrayList.add(new Category(dataSnapshot1.child("Name").getValue(String.class)
                                ,dataSnapshot1.child("Image").getValue(String.class)));
                    }

                }
                arrayAdapter =new ArrayAdapter();
                recyclerView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addCategory(View view) {

        startActivity(new Intent(this,AddCategoryActivity.class)
                .putExtra("name","empty"));
    }

    public class ArrayAdapter extends RecyclerView.Adapter<ArrayAdapter.ImageViewHoler> {

        public ArrayAdapter(){

        }
        @NonNull
        @Override
        public ArrayAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(CategoryActivity.this).inflate(R.layout.item_main_category,parent,false);
            return  new ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ArrayAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {

            Picasso.with(CategoryActivity.this)
                    .load(stringArrayList.get(position).getImage())
                    .placeholder(R.drawable.progress_animation)
                    .fit()
                    .centerCrop()
                    .into(holder.cat_image);

            holder.name.setText(stringArrayList.get(position).getName());
             holder.cardView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     final CharSequence[] options = {"Delete","View Sub Category", "Cancel"};
                     AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                     builder.setTitle("Select option");
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int item) {
                             if (options[item].equals("Delete")) {
                                 DatabaseReference databaseReference =   FirebaseDatabase.getInstance().getReference("Category").child(stringArrayList.get(position).getName());
                                 databaseReference.removeValue();
                                 dialog.dismiss();
                                 getData();
                             } else if (options[item].equals("Cancel")) {
                                  dialog.dismiss();
                             }
                             else if (options[item].equals("View Sub Category")) {
                                 startActivity(new Intent(CategoryActivity.this,SubCategoryActivity.class)
                                         .putExtra("name",stringArrayList.get(position).getName()));
                             }
                         }
                     });
                     builder.show();
                 }
             });




        }

        @Override
        public int getItemCount() {
            return stringArrayList.size();
        }

        public class ImageViewHoler extends RecyclerView.ViewHolder {
            TextView name;
            ImageView cat_image;
            CardView cardView;
            public ImageViewHoler(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.name);
                cat_image=itemView.findViewById(R.id.cat_image);
                cardView=itemView.findViewById(R.id.card);
            }
        }
    }
}