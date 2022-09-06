package com.mo.planner.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mo.planner.AddNewTask;
import com.mo.planner.AlertReceiver;
import com.mo.planner.Chronometer;
import com.mo.planner.MainActivity;
import com.mo.planner.Model.ToDoModel;
import com.mo.planner.R;
import com.mo.planner.TimePickerFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public  class ToDoAdapter<reference> extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {
    private MainActivity activity;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    String onlineUserID = mUser.getUid();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);
    private ArrayList<ToDoModel> taskList;

    public ToDoAdapter(ArrayList<ToDoModel> taskList, MainActivity activity) {
        this.taskList = taskList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new MyViewHolder(itemView);

    }


    public void onBindViewHolder(MyViewHolder holder, int position) {
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
            holder.cTask.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            if(item.getPriority().isEmpty()){
            holder.cTask.setButtonTintList(AppCompatResources.getColorStateList(getContext(), R.color.white));
            }
        }

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
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cTask;
        TextView cDate;

        public MyViewHolder(View view) {
            super(view);
            cTask = view.findViewById(R.id.todoCheckBox);
            cDate = view.findViewById(R.id.dateText);

        }


    }

}
