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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket4u.MainActivity;
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
    DatabaseReference myRef;//used as instance to get data from the firebase.

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

        category=getIntent().getStringExtra("name");// get the name of the category passed from the CategoryActivity.
        myRef=  FirebaseDatabase.getInstance().getReference().child("Category").child(category).child("SubCategory");/* reference to a specific location in the
        Firebase Realtime Database, In this case, it points to the "SubCategory" child node. */

        recyclerView=findViewById(R.id.recylerView);
        /* The LinearLayoutManager is responsible for positioning the item views in a linear list. */
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        arrayAdapter =new ArrayAdapter();
        recyclerView.setAdapter(arrayAdapter);

        String newTitle = "Admin->" + category;
        setTitle(newTitle); // set new title
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
        stringArrayList=new ArrayList<String>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /* loop that add sub categories to the stringArrayList */
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(!stringArrayList.contains(dataSnapshot1.child("SubName").getValue(String.class))){
                        stringArrayList.add(dataSnapshot1.child("SubName").getValue(String.class));
                    }
                    arrayAdapter.notifyDataSetChanged(); /* The notifyDataSetChanged method of the arrayAdapter is called to notify
                    the adapter that the data has changed and it needs to update the UI to reflect the changes.
                    This will trigger the arrayAdapter to refresh the list of "sub categories" displayed in the UI. */
                }
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
                .putExtra("name",category));
    }

    public class ArrayAdapter extends RecyclerView.Adapter<ArrayAdapter.ImageViewHoler> {

        /* Constructor */
        public ArrayAdapter(){}

        /* onCreateViewHolder(): called when a new ViewHolder is needed.
        This method creates a new View for the list item and wraps it in a ArrayAdapter.ImageViewHolder object. */
        @NonNull
        @Override
        public ArrayAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(SubCategoryActivity.this).inflate(R.layout.item_category,parent,false);
            return  new ArrayAdapter.ImageViewHoler(v);
        }

        /* onBindViewHolder(): called to display the data at the specified position.
        This method sets the data for the item at the given position.
        It also sets an OnClickListener for the item's CardView,
        which displays an AlertDialog asking the user 2 options: Delete sub category  or cancel. */
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

                            /* If the user selects "Delete", the code deletes the item from the database and dismisses the dialog */
                            if (options[item].equals("Delete")) {
                              DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("Category").child(category).child("SubCategory").child(stringArrayList.get(position));
                              databaseReference.removeValue(); // delete the data at a specific location in the database.
                              dialog.dismiss();//close the dialog and return to the SubCategory activity.
                              getData();// update the list of categories.
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();// display the dialog to the user.
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
            CardView cardView;
            public ImageViewHoler(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.name);
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
                setAdminLoginStatus(SubCategoryActivity.this,false);
                setUserLoginStatus(SubCategoryActivity.this,false);
                startActivity(new Intent(SubCategoryActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}