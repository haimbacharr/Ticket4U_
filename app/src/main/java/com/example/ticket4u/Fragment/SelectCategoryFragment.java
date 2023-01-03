package com.example.ticket4u.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ticket4u.R;
import com.example.ticket4u.User.AddItemActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SelectCategoryFragment extends Fragment {

    Spinner category,subcategory;
    private Dialog loadingDialog;
    ArrayAdapter<String> categoryAdapter,subcategoryAdapter;
    ArrayList<String> categoriesArrayList=new ArrayList<String>();
    ArrayList<String> subcategoriesArrayList=new ArrayList<String>();
    public static String CATEGORY="",SUBCATEGORY="";
    Button addItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_category, container, false);
        subcategory=view.findViewById(R.id.subcategory);
        category=view.findViewById(R.id.category);
        addItem=view.findViewById(R.id.button2);
        /////loading dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                CATEGORY=selectedItem;
                category.setSelection(position);
                getSubCategory(selectedItem);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();

                SUBCATEGORY=selectedItem;
                subcategory.setSelection(position);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SUBCATEGORY.equals("")){
                    Toast.makeText(getContext(),"you can not add to the categorey",Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(new Intent(getContext(), AddItemActivity.class));
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        getCategory();
        super.onStart();
    }

    public void getCategory(){
        loadingDialog.show();
        categoriesArrayList.clear();
        CATEGORY="";
        DatabaseReference myRef=  FirebaseDatabase.getInstance().getReference().child("Category");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(!categoriesArrayList.contains(dataSnapshot1.child("Name").getValue(String.class))){
                        categoriesArrayList.add(dataSnapshot1.child("Name").getValue(String.class));
                    }

                }
                // Creating adapter for spinner
                categoryAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categoriesArrayList);

                // Drop down layout style - list view with radio button
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(categoryAdapter);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
    public void getSubCategory(String name){
        loadingDialog.show();
        subcategoriesArrayList.clear();
        SUBCATEGORY="";
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference().child("Category").child(name).child("SubCategory");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(!subcategoriesArrayList.contains(dataSnapshot1.child("SubName").getValue(String.class))){
                        subcategoriesArrayList.add(dataSnapshot1.child("SubName").getValue(String.class));
                    }
                }
                // Drop down layout style - list view with radio button
                subcategoryAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, subcategoriesArrayList);
                subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subcategory.setAdapter(subcategoryAdapter);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }



}