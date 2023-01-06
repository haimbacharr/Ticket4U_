package com.example.ticket4u.Admin;

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

import com.example.ticket4u.MainActivity;
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

        /* loading dialog */
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        listrecylerView=findViewById(R.id.listrecylerView);

        /* The LinearLayoutManager is responsible for positioning the item views in a linear list. */
        listrecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        categoryAdapter=new CategoryAdapter();
        listrecylerView.setAdapter(categoryAdapter);
        getAllData(); // retrieve the data for the items in the RecyclerView.
    }

    public void getAllData(){
        loadingDialog.show();// The loadingDialog is displayed while the data is being fetched.
        itemArrayList.clear();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items"); /* reference to a specific location in the
        Firebase Realtime Database, In this case, it points to the "Items" child node. */

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /* The code iterating over each child snapshot with a for loop.
                Inside the loop, it gets a reference to the "Report" child of the "Items" node in the database,
                using the value of the "ItemId" child of the current snapshot as a key. */
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items").child(dataSnapshot1.child("ItemId").getValue(String.class)).child("Report");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String report=  dataSnapshot.child("itemReport").getValue(String.class);
                            if(report!=null){
                                itemArrayList.add(new Item(
                                        dataSnapshot1.child("Name").getValue(String.class)
                                        ,dataSnapshot1.child("ItemImage").getValue(String.class)
                                        ,dataSnapshot1.child("Description").getValue(String.class)
                                        ,dataSnapshot1.child("Quantity").getValue(String.class)
                                        ,dataSnapshot1.child("OriginalPrice").getValue(String.class)
                                        ,dataSnapshot1.child("Category").getValue(String.class)
                                        ,dataSnapshot1.child("SubCategory").getValue(String.class)
                                        ,dataSnapshot1.child("UserId").getValue(String.class)
                                        ,dataSnapshot1.child("ItemId").getValue(String.class)
                                        ,dataSnapshot1.child("AskingPrice").getValue(String.class)
                                        ,dataSnapshot1.child("Date").getValue(String.class)
                                ));

                                categoryAdapter.notifyDataSetChanged(); /* The notifyDataSetChanged method is used
                                to indicate that the data set of the adapter has changed and
                                the RecyclerView should update itself to reflect the new data. */
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }

                categoryAdapter.notifyDataSetChanged();
                loadingDialog.dismiss(); // close loading dialog.
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
            View v= LayoutInflater.from(ReportActivity.this).inflate(R.layout.item_list,parent,false);
            return  new CategoryAdapter.ImageViewHoler(v);
        }


        /* onBindViewHolder(): called to display the data at the specified position.
        This method sets the data for the item at the given position.
        It also sets an OnClickListener for the item's CardView,
        which displays an AlertDialog asking the user 3 options: Delete item, view sub category or cancel. */
        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {
            holder.fav_icon.setVisibility(View.GONE);
            Picasso.with(ReportActivity.this)
                    .load(itemArrayList.get(position).getPic())
                    .placeholder(R.drawable.progress_animation)
                    .fit()
                    .centerCrop()
                    .into(holder.cat_image);

            holder.name.setTypeface(null, Typeface.BOLD);// set the name text to be bold.
            holder.name.setText(itemArrayList.get(position).getName());
            holder.price.setText("Price: "+itemArrayList.get(position).getOriginalPrice()+"$");  // display the price of the item.
            holder.quantity.setText("Quantity: "+itemArrayList.get(position).getQuantity()); // display the quantity of the item.


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ReportActivity.this, ViewReportActivity.class);
                    intent.putExtra("itemId", itemArrayList.get(position).getItemId());
                    intent.putExtra("name", itemArrayList.get(position).getName());//pass the name of the reported item to the ViewReportActivity.
                    startActivity(intent);
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
                setAdminLoginStatus(ReportActivity.this,false);
                setUserLoginStatus(ReportActivity.this,false);
                startActivity(new Intent(ReportActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}