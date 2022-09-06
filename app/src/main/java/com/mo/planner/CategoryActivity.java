package com.mo.planner;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mo.planner.Adapters.CategoryActivityAdapter;
import com.mo.planner.Adapters.ToDoAdapter;
import com.mo.planner.Model.CategoryModel;
import com.mo.planner.Model.ToDoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {

    String categoryName;
    private Toolbar title;
    private FloatingActionButton fab;
    private RecyclerView categoryRecyclerView;
    private ToDoModel model;
    CategoryActivityAdapter tasksAdapter;
    private ArrayList<ToDoModel> taskList;

    String onlineUserID;
    public DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);

        categoryName=getIntent().getStringExtra("categoryName");
        title=findViewById(R.id.category_title);
        title.setTitle(categoryName);
        fab=findViewById(R.id.fab);



        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);


        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.inbox);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.activity:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        overridePendingTransition(0,0);
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
                        startActivity(new Intent(getApplicationContext(), Category.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.exit_app:
                        mAuth.signOut();
                        Intent intent = new Intent(CategoryActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;

                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        taskList=getAllCategoryTasks();
        Collections.reverse(taskList);

    }


    public ArrayList<ToDoModel> getAllCategoryTasks() {
        final ArrayList<ToDoModel> taskList=new ArrayList<>();
        reference.orderByChild("categoryN").equalTo(categoryName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ToDoModel task = snapshot1.getValue(ToDoModel.class);
                    taskList.add(task);
                }
                tasksAdapter = new CategoryActivityAdapter(taskList, CategoryActivity.this);
                categoryRecyclerView.setAdapter(tasksAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return taskList;
    }

}
