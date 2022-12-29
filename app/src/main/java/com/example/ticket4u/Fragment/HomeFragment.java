package com.example.ticket4u.Fragment;

import static com.example.ticket4u.Utils.Constant.getUserId;
import static com.example.ticket4u.Utils.Constant.getUserLoginStatus;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ticket4u.Model.Category;
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
import java.util.UUID;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView,listrecylerView;
    ArrayAdapter arrayAdapter;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categoryArrayList =new ArrayList<Category>();
    DatabaseReference myRef;
    private Dialog loadingDialog;
    EditText search;
    ArrayList<String> favouriteItemList=new ArrayList<String>();
   public static ArrayList<Item> itemArrayList =new ArrayList<Item>();
    public  ArrayList<Item> itemArrayList1 =new ArrayList<Item>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        search=view.findViewById(R.id.search);
        //loading dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        myRef=  FirebaseDatabase.getInstance().getReference().child("Category");
        recyclerView=view.findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listrecylerView=view.findViewById(R.id.listrecylerView);
        listrecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }

            private void filter(String text) {
                ArrayList<Item> filterlist=new ArrayList<>();
                for(Item item: itemArrayList1){
                    if(item.getName().toLowerCase().contains(text.toLowerCase())||item.getCategory().toLowerCase().contains(text.toLowerCase())){
                        filterlist.add(item);
                    }
                }
                categoryAdapter.filteredList(filterlist);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        getFavouriteList();
        getData();
        getAllData();
        super.onStart();
    }
public void getAllData(){
    itemArrayList1.clear();
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Items");
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                itemArrayList1.add(new Item(
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
    public void getSpecificData(String name){
        loadingDialog.show();
        itemArrayList1.clear();
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(name.equals(dataSnapshot1.child("Category").getValue(String.class))){
                        itemArrayList1.add(new Item(
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
    public void getFavouriteList(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(getContext()));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                   favouriteItemList.add(dataSnapshot1.child("itemId").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getData(){
        loadingDialog.show();
        categoryArrayList=new ArrayList<Category>();
        categoryArrayList.add(new Category("All",""));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(!categoryArrayList.contains(dataSnapshot1.child("Name").getValue(String.class))){
                        categoryArrayList.add(new Category(dataSnapshot1.child("Name").getValue(String.class)
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
    public class ArrayAdapter extends RecyclerView.Adapter<ArrayAdapter.ImageViewHoler> {

        public ArrayAdapter(){

        }
        @NonNull
        @Override
        public ArrayAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(getContext()).inflate(R.layout.item_category_type,parent,false);
            return  new ArrayAdapter.ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ArrayAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {
                        if(categoryArrayList.get(position).getName().equals("All")){
                             holder.cat_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_border_all_24));
                        }
                        else {
                            Picasso.with(getContext())
                                    .load(categoryArrayList.get(position).getImage())
                                    .placeholder(R.drawable.progress_animation)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.cat_image);
                        }


            holder.name.setText(categoryArrayList.get(position).getName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                                if(categoryArrayList.get(position).getName().equals("All")){
                                    loadingDialog.show();
                                    getAllData();
                                }
                                else {

                                    getSpecificData(categoryArrayList.get(position).getName());
                                }
                }
            });




        }

        @Override
        public int getItemCount() {
            return categoryArrayList.size();
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


    public void addToFav(String itemId){
        DatabaseReference  myRef=  FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(getContext())).child(itemId);
        myRef.child("itemId").setValue(itemId);
    }
    public void removeToFav(String itemId){
        DatabaseReference  myRef=  FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(getContext())).child(itemId);
        myRef.removeValue();
    }


    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ImageViewHoler> {

        public CategoryAdapter(){
          itemArrayList=  itemArrayList1;
        }
        @NonNull
        @Override
        public CategoryAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(getContext()).inflate(R.layout.item_list,parent,false);
            return  new CategoryAdapter.ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {

            if(getUserLoginStatus(getContext())){
                     if(favouriteItemList.size()!=0){
                         if(favouriteItemList.contains(itemArrayList.get(position).getItemId())){
                             holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
                         }
                         else {
                             holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_favorite));
                         }
                     }
                     else {
                         holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_favorite));
                     }

            }
            else {
                 holder.fav_icon.setVisibility(View.GONE);
            }

            holder.fav_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                      if(holder.fav_icon.getDrawable().getConstantState() == getResources().getDrawable( R.drawable.favorite).getConstantState()){
                          removeToFav(itemArrayList.get(position).getItemId());
                          holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_favorite));
                      }else {
                        addToFav(itemArrayList.get(position).getItemId());
                          holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
                      }
                }
            });


                Picasso.with(getContext())
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
                               startActivity(new Intent(getContext(), DetailActivity.class)
                                       .putExtra("index",position));
                }
            });




        }
        public void filteredList(ArrayList<Item> filterlist) {
            itemArrayList=filterlist;
            notifyDataSetChanged();
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





    public String createFavId() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}