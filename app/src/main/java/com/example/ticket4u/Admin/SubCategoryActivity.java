package com.example.ticket4u.Admin;

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
import android.widget.TextView;

import com.example.ticket4u.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubCategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayAdapter arrayAdapter;
    ArrayList<String> stringArrayList =new ArrayList<String>();
    private Dialog loadingDialog;
    String category="";
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        /////loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        category=getIntent().getStringExtra("name");
         myRef=  FirebaseDatabase.getInstance().getReference().child("Category").child(category).child("SubCategory");
        recyclerView=findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        arrayAdapter =new ArrayAdapter();
        recyclerView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        getData();
        super.onStart();
    }

    public void getData(){
        loadingDialog.show();
        stringArrayList=new ArrayList<String>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(!stringArrayList.contains(dataSnapshot1.child("SubName").getValue(String.class))){
                        stringArrayList.add(dataSnapshot1.child("SubName").getValue(String.class));
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addCategory(View view) {

        startActivity(new Intent(this,AddCategoryActivity.class)
                .putExtra("name",category));
    }

    public class ArrayAdapter extends RecyclerView.Adapter<ArrayAdapter.ImageViewHoler> {

        public ArrayAdapter(){

        }
        @NonNull
        @Override
        public ArrayAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(SubCategoryActivity.this).inflate(R.layout.item_category,parent,false);
            return  new ArrayAdapter.ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ArrayAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {


            holder.name.setText(stringArrayList.get(position));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CharSequence[] options = {"Delete", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(SubCategoryActivity.this);
                    builder.setTitle("Select option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Delete")) {
                              DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("Category").child(category).child("SubCategory").child(stringArrayList.get(position));
                              databaseReference.removeValue();
                                        dialog.dismiss();
                                getData();
                            } else if (options[item].equals("Cancel")) {
                                       dialog.dismiss();
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
            CardView cardView;
            public ImageViewHoler(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.name);
                cardView=itemView.findViewById(R.id.card);
            }
        }
    }
}