package com.example.ticket4u.Admin;


import static com.example.ticket4u.Utils.Constant.setAdminLoginStatus;
import static com.example.ticket4u.Utils.Constant.setUserLoginStatus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket4u.MainActivity;
import com.example.ticket4u.Model.Category;
import com.example.ticket4u.R;
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

    DatabaseReference myRef; //used as instance to get data from the firebase.
    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        /* loading dialog */
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        myRef=  FirebaseDatabase.getInstance().getReference().child("Category"); /* reference to a specific location in the
        Firebase Realtime Database, In this case, it points to the "Category" child node. */

        recyclerView=findViewById(R.id.recylerView);
        /* The LinearLayoutManager is responsible for positioning the item views in a linear list. */
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    /* part of the life cycle of an activity */
    @Override
    protected void onStart() {
        getData();
        super.onStart();
    }

    /* The getData() method is called to fetch data from the database. This data is then used to populate the RecyclerView. */
    public void getData(){
        loadingDialog.show();// The loadingDialog is displayed while the data is being fetched.
        stringArrayList=new ArrayList<Category>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /* loop that add children to the categories */
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

    /* when press on '+' icon will open new activity */
    public void addCategory(View view) {
        startActivity(new Intent(this,AddCategoryActivity.class)
                .putExtra("name","empty"));
    }

    public class ArrayAdapter extends RecyclerView.Adapter<ArrayAdapter.ImageViewHoler> {

        /* Constructor */
        public ArrayAdapter(){}

        /* onCreateViewHolder(): called when a new ViewHolder is needed.
        This method creates a new View for the list item and wraps it in a ArrayAdapter.ImageViewHolder object. */
        @NonNull
        @Override
        public ArrayAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(CategoryActivity.this).inflate(R.layout.item_main_category,parent,false);
            return  new ImageViewHoler(v);
        }


        /* onBindViewHolder(): called to display the data at the specified position.
        This method sets the data for the item at the given position.
        It also sets an OnClickListener for the item's CardView,
        which displays an AlertDialog asking the user 3 options: Delete item, view sub category or cancel. */
        @Override
        public void onBindViewHolder(@NonNull final ArrayAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {
            Picasso.with(CategoryActivity.this)
                    .load(stringArrayList.get(position).getImage()) //used to load the image from the URL.
                    .placeholder(R.drawable.progress_animation) //used to specify a placeholder image to be displayed while the image is loading.
                    .fit()  //used to resize the image to fit the dimensions of the ImageView
                    .centerCrop() //used to crop the image to the center of the ImageView
                    .into(holder.cat_image); //used to set the image in the ImageView

            holder.name.setText(stringArrayList.get(position).getName());

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     final CharSequence[] options = {"Delete","View Sub Category", "Cancel"}; // three options will display when click on the category.
                     AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                     builder.setTitle("Select option"); // Title of the alert dialog.
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int item) {
                             if (options[item].equals("Delete")) {
                                 DatabaseReference databaseReference =   FirebaseDatabase.getInstance().getReference("Category").child(stringArrayList.get(position).getName());
                                 databaseReference.removeValue(); // delete the "category" node and its children(sub directories).
                                 dialog.dismiss(); //close the dialog and return to the Category activity.
                                 getData(); // update the list of categories.
                             } else if (options[item].equals("Cancel")) {
                                  dialog.dismiss();
                             }
                             else if (options[item].equals("View Sub Category")) { // open new activity with the selected category.
                                 startActivity(new Intent(CategoryActivity.this,SubCategoryActivity.class)
                                         .putExtra("name",stringArrayList.get(position).getName()));
                             }
                         }
                     });
                     builder.show(); // display the dialog to the user.
                 }
             });
        }

        /* getItemCount(): returns the number of items in the data set. */
        @Override
        public int getItemCount() {
            return stringArrayList.size();
        }

        /* inner class called ImageViewHolder that extends RecyclerView.ViewHolder and represents a view holder for the list item.
        It holds the item's views, such as the name, price, and image views. */
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
                setAdminLoginStatus(CategoryActivity.this,false);
                setUserLoginStatus(CategoryActivity.this,false);
                startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}