package com.ticket.foru.Fragment;

import static com.ticket.foru.Utils.Constant.getKilometers;
import static com.ticket.foru.Utils.Constant.getUserId;
import static com.ticket.foru.Utils.Constant.getUserLatitude;
import static com.ticket.foru.Utils.Constant.getUserLongitude;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ticket.foru.Model.Item;
import com.example.ticket4u.R;
import com.ticket.foru.User.DetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PreferedItemFragment extends Fragment {

   static public ArrayList<Item> myItemArrayList =new ArrayList<Item>();
    CategoryAdapter categoryAdapter;
    private Dialog loadingDialog;
    RecyclerView listrecylerView;
    ArrayList<String> favouriteItemList=new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prefered_item, container, false);
        //loading dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        listrecylerView=view.findViewById(R.id.listrecylerView);
        listrecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getActivity().setTitle("Preferred items");

        return view;
    }

    @Override
    public void onStart() {
        getFavouriteList();
        getAllData();
        super.onStart();
    }

    public void getFavouriteList(){
        favouriteItemList.clear();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(getContext()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void getAllData(){
        loadingDialog.show();
        myItemArrayList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Double distance = 0.0;
                    try {
                        String latt = dataSnapshot1.child("latitude").getValue(String.class);
                        String longt = dataSnapshot1.child("longitude").getValue(String.class);
                        distance = getKilometers(Double.parseDouble(latt),Double.parseDouble(longt),
                                Double.parseDouble(getUserLatitude(getContext()))
                                ,Double.parseDouble(getUserLongitude(getContext())));
                    }catch (NumberFormatException e){
                    }catch (NullPointerException e){
                    }catch (Exception e){
                    }
                    if(favouriteItemList.contains(dataSnapshot1.child("ItemId").getValue(String.class))){
                        myItemArrayList.add(new Item(
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
                                ,dataSnapshot1.child("latitude").getValue(String.class)
                                ,dataSnapshot1.child("longitude").getValue(String.class)
                                ,""+distance

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
    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ImageViewHoler> {

        public CategoryAdapter(){

        }
        @NonNull
        @Override
        public CategoryAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(getContext()).inflate(R.layout.item_list,parent,false);
            return  new CategoryAdapter.ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {

            holder.fav_icon.setVisibility(View.GONE);

            Picasso.with(getContext())
                    .load(myItemArrayList.get(position).getPic())
                    .placeholder(R.drawable.progress_animation)
                    .fit()
                    .centerCrop()
                    .into(holder.cat_image);

            holder.name.setText(myItemArrayList.get(position).getName());
            holder.price.setText("Price "+myItemArrayList.get(position).getOriginalPrice()+" ₪");
            holder.quantity.setText("Quantity "+myItemArrayList.get(position).getQuantity());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                              startActivity(new Intent(getContext(), DetailActivity.class)
                              .putExtra("index",position)
                                      .putExtra("status",true));
                }
            });

            holder.distance.setVisibility(View.VISIBLE);
            holder.distance.setText("" + myItemArrayList.get(position).getDistance()+" KM");

        }

        @Override
        public int getItemCount() {
            return myItemArrayList.size();
        }

        public class ImageViewHoler extends RecyclerView.ViewHolder {
            TextView name,price,quantity,distance;
            ImageView cat_image,fav_icon;
            CardView cardView;
            public ImageViewHoler(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.name);
                price=itemView.findViewById(R.id.price);
                quantity=itemView.findViewById(R.id.quantity);
                fav_icon=itemView.findViewById(R.id.fav_icon);
                distance=itemView.findViewById(R.id.distance);
                cat_image=itemView.findViewById(R.id.imageView);
                cardView=itemView.findViewById(R.id.card);
            }
        }
    }
}