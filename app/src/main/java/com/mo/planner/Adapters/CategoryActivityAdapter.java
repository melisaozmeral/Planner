package com.mo.planner.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mo.planner.AddNewTask;
import com.mo.planner.CategoryActivity;
import com.mo.planner.MainActivity;
import com.mo.planner.Model.ToDoModel;
import com.mo.planner.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryActivityAdapter<reference> extends RecyclerView.Adapter<CategoryActivityAdapter.CategoryHolder> {
    private CategoryActivity activity;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    String onlineUserID = mUser.getUid();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);
    private ArrayList<ToDoModel> taskList;

    public CategoryActivityAdapter(ArrayList<ToDoModel> taskList, CategoryActivity activity) {
        this.taskList = taskList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        final ToDoModel item = taskList.get(position);
        holder.cTask.setText(item.getTask());
        holder.cDate.setText(item.getDate());
        holder.cTask.setChecked(toBoolean(item.getStatus()));
        HashMap<String, Object> priority = new HashMap<String, Object>();
        switch (item.getPriority())
        {
            case "!!!":{
                holder.cTask.setButtonTintList(AppCompatResources.getColorStateList(getContext(),R.color.red));
                holder.cDate.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                break;
            }
            case "!!":{
                holder.cTask.setButtonTintList(AppCompatResources.getColorStateList(getContext(),R.color.medium));
                holder.cDate.setTextColor(ContextCompat.getColor(getContext(), R.color.medium));
                break;
            }
            case "!":{
                holder.cTask.setButtonTintList(AppCompatResources.getColorStateList(getContext(),R.color.low));
                holder.cDate.setTextColor(ContextCompat.getColor(getContext(), R.color.low));
                break;
            }
            case " ":{
                holder.cTask.setButtonTintList(AppCompatResources.getColorStateList(getContext(),R.color.grey));
                holder.cDate.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
                break;
            }
        }

        holder.cTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<String, Object> status = new HashMap<String, Object>();
                if (isChecked) {
                    status.put("status", 1);
                } else {
                    status.put("status", 0);
                }
                reference.child(item.getId()).updateChildren(status);
            }
        });

        if (toBoolean(item.getStatus())) {
            holder.cTask.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.cTask.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
            holder.cTask.setButtonTintList(AppCompatResources.getColorStateList(getContext(), R.color.grey));
            holder.cDate.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
        }
        else {
            holder.cTask.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            holder.cTask.setTextColor(ContextCompat.getColor(getContext(), R.color.black2));
            if(item.getPriority().isEmpty()){
                holder.cTask.setButtonTintList(AppCompatResources.getColorStateList(getContext(), R.color.black2));
            }
        }
holder.cMenu.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        PopupMenu menu=new PopupMenu(activity,v);
        menu.getMenuInflater().inflate(R.menu.category_menu,menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        deleteItem(position);
                    case R.id.edit:
                        editTask(position);
                }
            return true;}
        });
    }
});

    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }


    private boolean toBoolean(int n) {
        return n != 0;
    }

    public Context getContext() {
        return activity;
    }


    public void setTasks(ArrayList<ToDoModel> todoList) {
        this.taskList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = taskList.get(position);
        reference.child(item.getId()).removeValue();
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void editTask(int position) {
        ToDoModel item = taskList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("date", item.getDate());
        bundle.putString("priority",item.getPriority());
        bundle.putString("categoryN",item.getCategoryN());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        notifyDataSetChanged();
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {
        CheckBox cTask;
        TextView cDate;
        Button cMenu;

        public CategoryHolder(View view) {
            super(view);
            cTask = view.findViewById(R.id.todoCheckBox);
            cDate = view.findViewById(R.id.dateText);
            cMenu=view.findViewById(R.id.menu);


        }


    }

}
