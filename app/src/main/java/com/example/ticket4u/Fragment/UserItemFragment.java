package com.example.ticket4u.Fragment;

import static com.example.ticket4u.Utils.Constant.INDEX;
import static com.example.ticket4u.Utils.Constant.getKilometers;
import static com.example.ticket4u.Utils.Constant.getUserLatitude;
import static com.example.ticket4u.Utils.Constant.getUserLongitude;

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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket4u.Model.Item;
import com.example.ticket4u.R;
import com.example.ticket4u.User.EditItemActivity;
import com.example.ticket4u.Utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserItemFragment extends Fragment {

    public static ArrayList<Item> itemArrayList =new ArrayList<Item>();
    CategoryAdapter categoryAdapter;
    private Dialog loadingDialog;
    RecyclerView listrecylerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_item, container, false);
        //loading dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        listrecylerView=view.findViewById(R.id.listrecylerView);
        listrecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onStart() {
        getAllData();
        super.onStart();
    }

    public void getAllData(){
        loadingDialog.show();
        itemArrayList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(Constant.getUserId(getContext()).equals(dataSnapshot1.child("UserId").getValue(String.class))) {
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
                        if (dataSnapshot1.child("Sold").getValue(String.class).equals("not")) {
                            itemArrayList.add(new Item(
                                    dataSnapshot1.child("Name").getValue(String.class)
                                    , dataSnapshot1.child("ItemImage").getValue(String.class)
                                    , dataSnapshot1.child("Description").getValue(String.class)
                                    , dataSnapshot1.child("Quantity").getValue(String.class)
                                    , dataSnapshot1.child("OriginalPrice").getValue(String.class)
                                    , dataSnapshot1.child("Category").getValue(String.class)
                                    , dataSnapshot1.child("SubCategory").getValue(String.class)
                                    , dataSnapshot1.child("UserId").getValue(String.class),
                                      dataSnapshot1.child("ItemId").getValue(String.class)
                                    , dataSnapshot1.child("AskingPrice").getValue(String.class)
                                    , dataSnapshot1.child("Date").getValue(String.class)
                                    ,dataSnapshot1.child("latitude").getValue(String.class)
                                    ,dataSnapshot1.child("longitude").getValue(String.class),
                                    ""+distance
                            ));
                        }
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
                    .load(itemArrayList.get(position).getPic())
                    .placeholder(R.drawable.progress_animation)
                    .fit()
                    .centerCrop()
                    .into(holder.cat_image);


            holder.name.setText(itemArrayList.get(position).getName());
            holder.price.setText("Price "+itemArrayList.get(position).getOriginalPrice()+" $");
            holder.quantity.setText("Quantity "+itemArrayList.get(position).getQuantity());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final CharSequence[] options = {"Update","Delete","Mark As Sold?", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Select option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Delete")) {
                                FirebaseDatabase.getInstance().getReference("Items").child(itemArrayList.get(position).getItemId()).removeValue();
                                getAllData();
                                dialog.dismiss();
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            } else if (options[item].equals("Update")) {
                                INDEX =position;
                                startActivity(new Intent(getContext(), EditItemActivity.class));
                                dialog.dismiss();
                            }
                            else if (options[item].equals("Mark As Sold?")) {
                                FirebaseDatabase.getInstance().getReference("Items").child(itemArrayList.get(position).getItemId())
                                .child("Sold").setValue("yes");
                                getAllData();
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            });
            holder.distance.setVisibility(View.VISIBLE);
            holder.distance.setText("" + itemArrayList.get(position).getDistance()+" KM");



        }

        @Override
        public int getItemCount() {
            return itemArrayList.size();
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
                cat_image=itemView.findViewById(R.id.imageView);
                cardView=itemView.findViewById(R.id.card);
                distance=itemView.findViewById(R.id.distance);
            }
        }
    }

}