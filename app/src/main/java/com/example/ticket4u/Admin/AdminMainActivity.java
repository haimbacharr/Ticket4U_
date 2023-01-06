package com.example.ticket4u.Admin;

import static com.example.ticket4u.Utils.Constant.ShowMessageDialogWithOkBtn;
import static com.example.ticket4u.Utils.Constant.getUserLoginStatus;
import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ticket4u.Fragment.HomeFragment;
import com.example.ticket4u.MainActivity;
import com.example.ticket4u.Model.Item;
import com.example.ticket4u.R;
import com.example.ticket4u.User.DetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/* This activity starts when press on view items from the admin's main activity */
public class AdminMainActivity extends AppCompatActivity {
    public  ArrayList<Item> itemArrayList =new ArrayList<Item>();
    CategoryAdapter categoryAdapter;
    private Dialog loadingDialog;
    RecyclerView listrecylerView; // instance of the recycle view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        //loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        listrecylerView=findViewById(R.id.listrecylerView);

        /* The LinearLayoutManager is responsible for positioning the items in the RecyclerView.
         By setting the orientation to vertical, the items will be displayed top to bottom.*/
        listrecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getAllData();// retrieves some data and populates the RecyclerView with it.
    }

    /* This method retrieves data from a Firebase Realtime Database and populates an ArrayList with it. */
    public void getAllData(){
        loadingDialog.show(); // let the user know that some work is being done in the background
        itemArrayList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items"); /* get instance of
        Fire base and it returns a reference to the "Items" child node. */
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            /* This code is part of an event listener that listens for changes in data stored in a Firebase Realtime Database. */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //dataSnapshot represents data at a specific location in the database.
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    itemArrayList.add(new Item(
                            dataSnapshot1.child("Name").getValue(String.class)
                            ,dataSnapshot1.child("ItemImage").getValue(String.class)
                            ,dataSnapshot1.child("Description").getValue(String.class)
                            ,dataSnapshot1.child("Quantity").getValue(String.class)
                            ,dataSnapshot1.child("OriginalPrice").getValue(String.class)
                            ,dataSnapshot1.child("Category").getValue(String.class)
                            ,dataSnapshot1.child("SubCategory").getValue(String.class)
                            ,dataSnapshot1.child("UserId").getValue(String.class),
                            dataSnapshot1.child("ItemId").getValue(String.class)
                            , dataSnapshot1.child("AskingPrice").getValue(String.class)
                            ,dataSnapshot1.child("Date").getValue(String.class)
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

        /* Constructor */
        public CategoryAdapter(){}

        /* onCreateViewHolder(): called when a new ViewHolder is needed.
        This method creates a new View for the list item and wraps it in a CategoryAdapter.ImageViewHolder object. */
        @NonNull
        @Override
        public CategoryAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(AdminMainActivity.this).inflate(R.layout.item_list,parent,false);
            return  new CategoryAdapter.ImageViewHoler(v);
        }

        /* onBindViewHolder(): called to display the data at the specified position.
        This method sets the data for the item at the given position, such as its name, price, and image.
        It also sets an OnClickListener for the item's CardView,
        which displays an AlertDialog asking the user if they want to delete the item. */
        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {
            holder.fav_icon.setVisibility(View.GONE); // will not display the heart icon on the item.
            Picasso.with(AdminMainActivity.this)
                    .load(itemArrayList.get(position).getPic())// Loads an image from the given URL.
                    .placeholder(R.drawable.progress_animation)// Displays a placeholder image while the image is loading.
                    .fit()// Scales the image to fit the dimensions of the target ImageView.
                    .centerCrop()// Centers the image in the ImageView and scales it uniformly
                    .into(holder.cat_image); // Displays the image in the specified ImageView.

            holder.name.setTypeface(null, Typeface.BOLD);// set the name text to be bold.
            holder.name.setText(itemArrayList.get(position).getName());
            holder.price.setText("Price: "+itemArrayList.get(position).getOriginalPrice()+"$"); // display the price of the item.
            holder.quantity.setText("Quantity: "+itemArrayList.get(position).getQuantity()); // display the quantity of the item.


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminMainActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Do you want to delete?");

                    /* If the user clicks "Yes",
                    the item is deleted from the database using the FirebaseDatabase instance and the removeValue() method.  */
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("Items").child(itemArrayList.get(position).getItemId()).removeValue();
                            getAllData(); // method called to update the list of items.
                            dialog.dismiss(); // close the dialog and return to the admin main activity.
                        }
                    });
                    /* If the user clicks "No", the AlertDialog is dismissed. */
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    /* create and show the alert dialog */
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        /* getItemCount(): returns the number of items in the data set. */
        @Override
        public int getItemCount() {
            return itemArrayList.size();
        }

        /* inner class called ImageViewHolder that extends RecyclerView.ViewHolder and represents a view holder for the list item.
        It holds the item's views, such as the name, price, and image views. */
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
                setAdminLoginStatus(AdminMainActivity.this,false);
                setUserLoginStatus(AdminMainActivity.this,false);
                startActivity(new Intent(AdminMainActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}