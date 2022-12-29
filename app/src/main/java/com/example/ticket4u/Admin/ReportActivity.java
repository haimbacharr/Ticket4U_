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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ticket4u.Model.Item;
import com.example.ticket4u.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    public ArrayList<Item> itemArrayList =new ArrayList<Item>();
    CategoryAdapter categoryAdapter;
    private Dialog loadingDialog;
    RecyclerView listrecylerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        //loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        listrecylerView=findViewById(R.id.listrecylerView);
        listrecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getAllData();
    }

    public void getAllData(){
        loadingDialog.show();
        itemArrayList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    itemArrayList.add(new Item(
                            dataSnapshot1.child("Name").getValue(String.class)
                            ,dataSnapshot1.child("ItemImage").getValue(String.class)
                            ,dataSnapshot1.child("Description").getValue(String.class)
                            ,dataSnapshot1.child("Quantity").getValue(String.class)
                            ,dataSnapshot1.child("Price").getValue(String.class)
                            ,dataSnapshot1.child("Category").getValue(String.class)
                            ,dataSnapshot1.child("SubCategory").getValue(String.class)
                            ,dataSnapshot1.child("UserId").getValue(String.class),
                            dataSnapshot1.child("ItemId").getValue(String.class)
                    ));
                }
                categoryAdapter=new CategoryAdapter();
                listrecylerView.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ImageViewHoler> {

        public CategoryAdapter(){

        }
        @NonNull
        @Override
        public CategoryAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(ReportActivity.this).inflate(R.layout.item_list,parent,false);
            return  new CategoryAdapter.ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {


            holder.fav_icon.setVisibility(View.GONE);





            Picasso.with(ReportActivity.this)
                    .load(itemArrayList.get(position).getPic())
                    .placeholder(R.drawable.progress_animation)
                    .fit()
                    .centerCrop()
                    .into(holder.cat_image);


            holder.name.setText(itemArrayList.get(position).getName());
            holder.price.setText(itemArrayList.get(position).getPrice());
            holder.quantity.setText(itemArrayList.get(position).getQuantity());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Do you want to see the item report?");

                    // add the buttons
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                                   startActivity(new Intent(ReportActivity.this,ViewReportActivity.class)
                                   .putExtra("itemId",itemArrayList.get(position).getItemId()));
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });




        }

        @Override
        public int getItemCount() {
            return itemArrayList.size();
        }

        public class ImageViewHoler extends RecyclerView.ViewHolder {
            TextView name,price,quantity;
            ImageView cat_image,fav_icon;
            CardView cardView;
            public ImageViewHoler(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.name);
                price=itemView.findViewById(R.id.price);
                quantity=itemView.findViewById(R.id.quantity);
                fav_icon=itemView.findViewById(R.id.fav_icon);
                cat_image=itemView.findViewById(R.id.imageView);
                cardView=itemView.findViewById(R.id.card);
            }
        }
    }
}