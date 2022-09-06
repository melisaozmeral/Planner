package com.mo.planner;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;

import android.content.Context;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mo.planner.Adapters.CategoryAdapter;
import com.mo.planner.Model.CategoryModel;
import com.mo.planner.Model.ToDoModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static androidx.core.content.ContextCompat.getSystemService;


public class AddNewTask extends BottomSheetDialogFragment implements  TimePickerDialog.OnTimeSetListener {
    public static final String TAG = "ActionBottomDialog";
    String onlineUserID;

    public EditText newTaskText;
    public TextView calendarDialogText;
    public TextView priorityText;
    public TextView categoryText;

    Button newTaskSaveButton;
    Button calendarDialogButton;
    ImageButton categoryDialogButton;
    ImageButton priorityMenuButton;
    public DatabaseReference reference,categoryReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> categoryList;
    DatePickerDialog.OnDateSetListener listener;
    ImageButton alertButton;
    TextView alertText;



    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);
        categoryReference=FirebaseDatabase.getInstance().getReference().child("category").child(onlineUserID);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText=getView().findViewById(R.id.newTaskText);
        calendarDialogText=getView().findViewById(R.id.calendarDialogText);
        priorityText=getView().findViewById(R.id.priorityText);
        categoryText=getView().findViewById(R.id.categoryText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);
        calendarDialogButton=getView().findViewById(R.id.calendarDialogButton);
        priorityMenuButton =getView().findViewById(R.id.priorityDialogButton);
        categoryDialogButton=getView().findViewById(R.id.categoryDialogButton);
        showSoftKeyboard(newTaskText);
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        alertButton=getView().findViewById(R.id.alertButton);
        alertText=getView().findViewById(R.id.alertText);




        boolean isUpdate = false;
        final Bundle bundle = getArguments();

        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            String date=bundle.getString("date");
            String priority=bundle.getString("priority");
            String categoryName=bundle.getString("categoryN");
            newTaskText.setText(task);
            calendarDialogText.setText(date);
            priorityText.setText(priority);
            categoryText.setText(categoryName);
            switch (priority)
            {
                case "!!!":{
                    calendarDialogText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.red));
                    priorityText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.red));
                    break;}
                case "!!":{
                    calendarDialogText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.medium));
                    priorityText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.medium));
                    break;
                }
                case "!":{
                    calendarDialogText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.low));
                    priorityText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.low));
                    break;
                }

            }
            assert task != null;

        }


        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setCompoundDrawableTintList(AppCompatResources.getColorStateList(newTaskSaveButton.getContext(),R.color.red));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setCompoundDrawableTintList(AppCompatResources.getColorStateList(newTaskSaveButton.getContext(),R.color.grey));
                }
            }
        });
         alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getChildFragmentManager(),null);
            }
        });
        calendarDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(),R.style.MyDialogTheme,listener,year,month,day);
                datePickerDialog.getWindow();
                datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "SİL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        calendarDialogText.setText("");
                    }
                });
                datePickerDialog.show();
            }
        });
        listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date=dayOfMonth+"."+month+"."+year;
                calendarDialogText.setText(date);
            }
        };


        priorityMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu=new PopupMenu(getContext(),priorityMenuButton);
                try {
                    Field[] fields = menu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(menu);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                menu.getMenuInflater().inflate(R.menu.priority,menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.high: {
                                priorityText.setText("!!!");
                                priorityText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.red));
                                calendarDialogText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.red));
                                break;
                            }
                            case R.id.medium: {
                                priorityText.setText("!!");
                                priorityText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.medium));
                                calendarDialogText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.medium));
                                break;
                            }
                            case R.id.low: {
                                priorityText.setText("!");
                                priorityText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.low));
                                calendarDialogText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.low));
                                break;
                            }
                            case R.id.no: {
                                priorityText.setText(" ");
                                priorityText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.grey));
                                calendarDialogText.setTextColor(AppCompatResources.getColorStateList(getContext(), R.color.grey));
                                break;
                            }
                        }
                        return true;
                    }
                });
               menu.show();
            }
        });

        categoryDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu categoryMenu=new PopupMenu(getActivity(),v);
                categoryReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CategoryModel category = snapshot.getValue(CategoryModel.class);
                            categoryMenu.getMenu().add(category.getCategoryName());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });

                categoryMenu.getMenuInflater().inflate(R.menu.category,categoryMenu.getMenu());
                categoryMenu.show();
                categoryMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        categoryText.setText(item.getTitle());
                        return true;
                        }
                });
            }
        });






        final boolean finalIsUpdate = isUpdate;
        if(finalIsUpdate)
        {
            newTaskText.setSelection(newTaskText.getText().length());
        }
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTask = newTaskText.getText().toString().trim();
                String id = reference.push().getKey();
                String mDate = calendarDialogText.getText().toString().trim();
                String mPriority=priorityText.getText().toString().trim();
                String mCategory=categoryText.getText().toString().trim();
                int status = 0;
                if (TextUtils.isEmpty(mTask)) {
                    newTaskText.setError("Boş geçilemez");
                    return;
                } else {
                    if(finalIsUpdate){
                        HashMap<String, Object> task=new HashMap<String, Object>();
                        task.put("task",mTask);
                        task.put("date",mDate);
                        task.put("priority",mPriority);
                        task.put("categoryN",mCategory);
                        reference.child(bundle.getString("id")).updateChildren(task);
                        dismiss();
                }else{
                    ToDoModel model = new ToDoModel(mTask, id, status, mDate,mPriority,mCategory);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Görev başarıyla eklendi", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            dismiss();
                        }
                    });
                    }
                }
            }
        });
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof IDialogCloseListener)
            ((IDialogCloseListener)activity).handleDialogClose(dialog);
    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                 getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        updateTimeText(c);
        startAlarm(c);
    }
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
    private void updateTimeText(Calendar c) {
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        alertText.setText(timeText);
    }


}
