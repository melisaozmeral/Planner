package com.mo.planner.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mo.planner.Category;
import com.mo.planner.CategoryActivity;
import com.mo.planner.Model.CategoryModel;
import com.mo.planner.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryAdapter<reference> extends RecyclerView.Adapter<CategoryAdapter.MyHolder>{
    Category activity;
    Dialog categoryDialog;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    String onlineUserID = mUser.getUid();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("category").child(onlineUserID);
    private ArrayList<CategoryModel> categoryList;



    public CategoryAdapter(ArrayList<CategoryModel> categoryList, Category activity) {
        this.categoryList = categoryList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row, parent, false);
        return new MyHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final CategoryModel item= categoryList.get(position);
        holder.cText.setText(item.getCategoryName());
        holder.cMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu=new PopupMenu(activity,v);
                menu.getMenuInflater().inflate(R.menu.category_menu,menu.getMenu());
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
                       switch (item.getItemId()){
                           case R.id.delete:
                               deleteCategory(position);
                               Toast.makeText(getContext(),"Kategori Silindi",Toast.LENGTH_LONG);
                               break;
                           case R.id.edit:
                               categoryDialog=new Dialog(activity);
                               categoryDialog.setContentView(R.layout.new_category);
                               categoryDialog.setTitle("Kategori");
                               categoryDialog.setCancelable(true);
                               categoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                               EditText newCategoryName=categoryDialog.findViewById(R.id.newCategoryName);
                               Button categoryButton=categoryDialog.findViewById(R.id.categoryButton);
                               categoryDialog.show();
                               if (v.requestFocus()) {
                                   InputMethodManager imm = (InputMethodManager)
                                           activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                   imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                               }
                               newCategoryName.setText(categoryList.get(position).getCategoryName());
                               categoryButton.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       HashMap<String, Object> category=new HashMap<String, Object>();
                                       category.put("category",newCategoryName.getText().toString().trim());
                                       reference.child(categoryList.get(position).getCategoryId()).updateChildren(category);
                                       categoryDialog.dismiss();
                                   }
                               });
                               break;
                       }
                       return true;
                   }
               });
            }
        });
        holder.cText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), CategoryActivity.class);
                intent.putExtra("categoryName",holder.cText.getText());
                getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public void setCategory(ArrayList<CategoryModel> categoryModelList) {
        this.categoryList = categoryModelList;
        notifyDataSetChanged();
    }
    public Context getContext() {
        return activity;
    }

    public void deleteCategory(int position) {
        CategoryModel item = categoryList.get(position);
        reference.child(item.getCategoryId()).removeValue();
        categoryList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        TextView cText;
        Button cMenu;
      public MyHolder(View view) {
          super(view);
          cText = view.findViewById(R.id.categoryText);
          cMenu=view.findViewById(R.id.categoryMenu);


      }
  }

}

