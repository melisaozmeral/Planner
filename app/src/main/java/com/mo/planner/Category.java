package com.mo.planner;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mo.planner.Adapters.CategoryAdapter;
import com.mo.planner.Model.CategoryModel;

import java.util.ArrayList;
import java.util.Collections;

public class Category extends AppCompatActivity {
    Button categoryButton;
    FloatingActionButton addCategoryButton;
    EditText newCategoryName,categoryName;
    Dialog categoryDialog;
    RecyclerView categoryView;
    String onlineUserID;
    private Toolbar title;
    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryModel> categoryList;

    public DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_layout);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("category").child(onlineUserID);
        title=findViewById(R.id.toolbar);

        addCategoryButton =findViewById(R.id.addCategory);
        categoryView=findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryView.setLayoutManager(layoutManager);

        categoryView=findViewById(R.id.categoryRecyclerView);
        categoryView = findViewById(R.id.categoryRecyclerView);
        categoryAdapter = new CategoryAdapter(categoryList,this);
        categoryView.setAdapter(categoryAdapter);



        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.inbox);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.activity:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.timer:
                        startActivity(new Intent(getApplicationContext(),Chronometer.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.step:
                        startActivity(new Intent(getApplicationContext(),StepCounter.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.inbox:
                        return false;
                    case R.id.exit_app:
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                }
                return false;
            }
        });

        categoryList=getAllCategory();
        Collections.reverse(categoryList);

        categoryAdapter.setCategory(categoryList);



        categoryDialog=new Dialog(Category.this);
        categoryDialog.setContentView(R.layout.new_category);
        categoryDialog.setTitle("Kategori");
        categoryDialog.setCancelable(true);
        categoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryName=categoryDialog.findViewById(R.id.newCategoryName);
        newCategoryName=categoryDialog.findViewById(R.id.newCategoryName);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog.show();
                newCategoryName.getText().clear();
                showSoftKeyboard(newCategoryName);

            }
        });
        categoryButton=categoryDialog.findViewById(R.id.categoryButton);


        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mCategory=newCategoryName.getText().toString().trim();
                String mId = reference.push().getKey();
                if(categoryName.getText().toString().isEmpty()){
                    Toast.makeText(Category.this,"Boş geçilmez",Toast.LENGTH_LONG);
                    return;
                }
                else{
                    CategoryModel categoryModel=new CategoryModel(mId,mCategory);
                    reference.child(mId).setValue(categoryModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Category.this,"Kategori başarıyla oluşturuldu",Toast.LENGTH_LONG);
                            } else {
                                Toast.makeText(Category.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                categoryDialog.dismiss();
            }
        });


    }


    public ArrayList<CategoryModel> getAllCategory() {
        final ArrayList<CategoryModel> categoryList=new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CategoryModel category = snapshot.getValue(CategoryModel.class);
                    categoryList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        return categoryList;
    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}
