package com.example.ticket4u.Fragment;

import static com.example.ticket4u.Utils.Constant.getKilometers;
import static com.example.ticket4u.Utils.Constant.getSort;
import static com.example.ticket4u.Utils.Constant.getUserId;
import static com.example.ticket4u.Utils.Constant.getUserInterest;
import static com.example.ticket4u.Utils.Constant.getUserLatitude;
import static com.example.ticket4u.Utils.Constant.getUserLoginStatus;
import static com.example.ticket4u.Utils.Constant.getUserLongitude;
import static com.example.ticket4u.Utils.Constant.setSort;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket4u.Model.Category;
import com.example.ticket4u.Model.Item;
import com.example.ticket4u.R;
import com.example.ticket4u.User.DetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class HomeFragment extends Fragment  {

    public static ArrayList<Item> itemArrayList = new ArrayList<Item>();
    public ArrayList<Item> itemArrayList1 = new ArrayList<Item>();
    RecyclerView recyclerView, listrecylerView;
    ArrayAdapter arrayAdapter;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categoryArrayList = new ArrayList<Category>();
    DatabaseReference myRef;
    EditText search;
    ArrayList<String> favouriteItemList = new ArrayList<String>();
    private Dialog loadingDialog;
    ImageButton imfilter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        search = view.findViewById(R.id.search);

        checkPermission();
        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myRef = FirebaseDatabase.getInstance().getReference().child("Category");
        imfilter = view.findViewById(R.id.imfilter);
        recyclerView = view.findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listrecylerView = view.findViewById(R.id.listrecylerView);
        listrecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getActivity().setTitle("Ticket4u -> Home Page");

        imfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    popup = new PopupMenu(getActivity(), v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.sort, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.name:
                                    if (itemArrayList.size() > 0) {
                                        Collections.sort(itemArrayList, Item.NAMEASC);
                                    }
                                    categoryAdapter.notifyDataSetChanged();
                                    setSort(getContext(),"name");
                                    return true;
                                case R.id.distance:
                                    if (itemArrayList.size() > 0) {
                                        Collections.sort(itemArrayList, Item.DISASC);
                                    }
                                    setSort(getContext(),"distance");
                                    categoryAdapter.notifyDataSetChanged();
                                    return true;
                                case R.id.date:
                                    if (itemArrayList.size() > 0) {
                                        Collections.sort(itemArrayList, Item.DATEASC);
                                    }
                                    setSort(getContext(),"date");
                                    categoryAdapter.notifyDataSetChanged();
                                    return true;
                                case R.id.price:
                                    if (itemArrayList.size() > 0) {
                                        Collections.sort(itemArrayList, Item.PRICEASC);
                                    }
                                    setSort(getContext(),"price");
                                    categoryAdapter.notifyDataSetChanged();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                }
            }
        });
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
                ArrayList<Item> filterlist = new ArrayList<>();
                for (Item item : itemArrayList1) {
                    if (item.getName().toLowerCase().contains(text.toLowerCase()))
                    {
                        filterlist.add(item);
                    }
                }
                try {
                    categoryAdapter.filteredList(filterlist);
                }catch (NullPointerException e){

                }
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

    public void getAllData() {
        itemArrayList1.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {//loop for all items in DB
                        if (dataSnapshot1.child("Sold").getValue(String.class).equals("not")) { //just available items - without SOLD
                            Double distance = 0.0;
                            try {
                                String latt = dataSnapshot1.child("latitude").getValue(String.class);
                                String longt = dataSnapshot1.child("longitude").getValue(String.class);
                                distance = getKilometers(Double.parseDouble(latt),Double.parseDouble(longt),
                                        Double.parseDouble(getUserLatitude(getContext()))
                                        ,Double.parseDouble(getUserLongitude(getContext())));
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            itemArrayList1.add(new Item
                                    (dataSnapshot1.child("Name").getValue(String.class),
                                    dataSnapshot1.child("ItemImage").getValue(String.class),
                                            dataSnapshot1.child("Description").getValue(String.class),
                                            dataSnapshot1.child("Quantity").getValue(String.class),
                                            dataSnapshot1.child("OriginalPrice").getValue(String.class),
                                            dataSnapshot1.child("Category").getValue(String.class),
                                            dataSnapshot1.child("SubCategory").getValue(String.class),
                                            dataSnapshot1.child("UserId").getValue(String.class),
                                            dataSnapshot1.child("ItemId").getValue(String.class),
                                            dataSnapshot1.child("AskingPrice").getValue(String.class),
                                            dataSnapshot1.child("Date").getValue(String.class),
                                            dataSnapshot1.child("City").getValue(String.class),
                                            dataSnapshot1.child("Number").getValue(String.class),""+distance));
                        }
                    } catch (NullPointerException e) {
                    } catch (Exception e) {
                    }
                }
                categoryAdapter = new CategoryAdapter();
                listrecylerView.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();

                if(getSort(getContext()).equalsIgnoreCase("name")){
                    if (itemArrayList.size() > 0) {
                        Collections.sort(itemArrayList, Item.NAMEASC);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }else if(getSort(getContext()).equalsIgnoreCase("date")){
                    if (itemArrayList.size() > 0) {
                        Collections.sort(itemArrayList, Item.DATEASC);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }else if(getSort(getContext()).equalsIgnoreCase("distance")){
                    if (itemArrayList.size() > 0) {
                        Collections.sort(itemArrayList, Item.DISASC);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }else if(getSort(getContext()).equalsIgnoreCase("price")){
                    if (itemArrayList.size() > 0) {
                        Collections.sort(itemArrayList, Item.PRICEASC);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void getSpecificData(String name) {
        loadingDialog.show();
        itemArrayList1.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (name.equals(dataSnapshot1.child("Category").getValue(String.class))) {
                        Double distance = 0.0;
                        try {
                            String latt = dataSnapshot1.child("latitude").getValue(String.class);
                            String longt = dataSnapshot1.child("longitude").getValue(String.class);
                            distance = getKilometers(Double.parseDouble(latt),Double.parseDouble(longt),
                                    Double.parseDouble(getUserLatitude(getContext()))
                                    ,Double.parseDouble(getUserLongitude(getContext())));
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (dataSnapshot1.child("Sold").getValue(String.class).equals("not")) {
                            itemArrayList1.add(new Item(dataSnapshot1.child("Name").getValue(String.class), dataSnapshot1.child("ItemImage").getValue(String.class), dataSnapshot1.child("Description").getValue(String.class), dataSnapshot1.child("Quantity").getValue(String.class), dataSnapshot1.child("OriginalPrice").getValue(String.class), dataSnapshot1.child("Category").getValue(String.class), dataSnapshot1.child("SubCategory").getValue(String.class), dataSnapshot1.child("UserId").getValue(String.class), dataSnapshot1.child("ItemId").getValue(String.class), dataSnapshot1.child("AskingPrice").getValue(String.class), dataSnapshot1.child("Date").getValue(String.class), dataSnapshot1.child("City").getValue(String.class), dataSnapshot1.child("Number").getValue(String.class),""+distance));
                        }
                    }

                }
                categoryAdapter = new CategoryAdapter();
                listrecylerView.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getFavouriteList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(getContext()));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    favouriteItemList.add(dataSnapshot1.child("itemId").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getData() {
        loadingDialog.show();
        categoryArrayList = new ArrayList<Category>();
        categoryArrayList.add(new Category("All", ""));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (!categoryArrayList.contains(dataSnapshot1.child("Name").getValue(String.class))) {
                        categoryArrayList.add(new Category(dataSnapshot1.child("Name").getValue(String.class), dataSnapshot1.child("Image").getValue(String.class)));
                    }
                }
                arrayAdapter = new ArrayAdapter();
                recyclerView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addToFav(String itemId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(getContext())).child(itemId);
        myRef.child("itemId").setValue(itemId);
    }

    public void removeToFav(String itemId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Favourite").child(getUserId(getContext())).child(itemId);
        myRef.removeValue();
    }

    public void checkPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public class ArrayAdapter extends RecyclerView.Adapter<ArrayAdapter.ImageViewHoler> {

        public ArrayAdapter() {

        }

        @NonNull
        @Override
        public ArrayAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_category_type, parent, false);
            return new ArrayAdapter.ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ArrayAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {
            if (categoryArrayList.get(position).getName().equals("All")) {
                holder.cat_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_border_all_24));
            } else {
                Picasso.with(getContext()).load(categoryArrayList.get(position).getImage()).placeholder(R.drawable.progress_animation).fit().centerCrop().into(holder.cat_image);
            }

            holder.name.setText(categoryArrayList.get(position).getName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (categoryArrayList.get(position).getName().equals("All")) {
                        loadingDialog.show();
                        getAllData();
                    } else {
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
                name = itemView.findViewById(R.id.name);
                cat_image = itemView.findViewById(R.id.cat_image);
                cardView = itemView.findViewById(R.id.card);
            }
        }
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ImageViewHoler> {
        public CategoryAdapter() {
            itemArrayList = itemArrayList1;
        }

        @NonNull
        @Override
        public CategoryAdapter.ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
            return new CategoryAdapter.ImageViewHoler(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.ImageViewHoler holder, @SuppressLint("RecyclerView") int position) {

            if (getUserLoginStatus(getContext())) {
                if (favouriteItemList.size() != 0) {
                    if (favouriteItemList.contains(itemArrayList.get(position).getItemId())) {
                        holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
                    } else {
                        holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_favorite));
                    }
                } else {
                    holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_favorite));
                }

            } else {
                holder.fav_icon.setVisibility(View.GONE);
            }


            if (itemArrayList.get(position).getCategory().equals(getUserInterest(getContext()))) {
                holder.rlback.setBackgroundResource(R.drawable.list_back_blue);
            } else {
                holder.rlback.setBackgroundResource(R.drawable.list_back);
            }
            try {
                if (Integer.parseInt(itemArrayList.get(position).getAskingPrice()) <= Integer.parseInt(itemArrayList.get(position).getOriginalPrice())) {
                    holder.imbestprice.setVisibility(View.VISIBLE);
                } else {
                    holder.imbestprice.setVisibility(View.GONE);
                }
            } catch (NumberFormatException e) {
            } catch (Exception e) {
            }
            holder.fav_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.fav_icon.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.favorite).getConstantState()) {
                        removeToFav(itemArrayList.get(position).getItemId());
                        holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_favorite));
                    } else {
                        addToFav(itemArrayList.get(position).getItemId());
                        holder.fav_icon.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
                    }
                }
            });

            Picasso.with(getContext()).load(itemArrayList.get(position).getPic()).placeholder(R.drawable.progress_animation).fit().centerCrop().into(holder.cat_image);
            holder.name.setText(itemArrayList.get(position).getName());
            holder.price.setText("Price " + itemArrayList.get(position).getAskingPrice() + " $");
            holder.quantity.setText("Quantity " + itemArrayList.get(position).getQuantity());
//            if(itemArrayList.get(position).getDistance().equalsIgnoreCase("0.0")){
//                holder.distance.setVisibility(View.GONE);
//            }else{
                holder.distance.setVisibility(View.VISIBLE);
                holder.distance.setText("" + itemArrayList.get(position).getDistance()+" KM");
//            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (favouriteItemList.contains(itemArrayList.get(position).getItemId())) {
                        startActivity(new Intent(getContext(), DetailActivity.class).putExtra("index", position).putExtra("status", true));
                    } else {
                        startActivity(new Intent(getContext(), DetailActivity.class).putExtra("index", position).putExtra("status", false));
                    }
                }
            });
        }

        public void filteredList(ArrayList<Item> filterlist) {
            itemArrayList = filterlist;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return itemArrayList.size();
        }

        public class ImageViewHoler extends RecyclerView.ViewHolder {
            TextView name, price, quantity,distance;
            ImageView cat_image, fav_icon, imbestprice;
            CardView cardView;
            RelativeLayout rlback;

            public ImageViewHoler(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.price);
                distance = itemView.findViewById(R.id.distance);
                quantity = itemView.findViewById(R.id.quantity);
                fav_icon = itemView.findViewById(R.id.fav_icon);
                cat_image = itemView.findViewById(R.id.imageView);
                cardView = itemView.findViewById(R.id.card);
                imbestprice = itemView.findViewById(R.id.imbestprice);
                rlback = itemView.findViewById(R.id.rlback);
            }
        }
    }

}