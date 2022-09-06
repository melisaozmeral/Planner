package com.mo.planner.Model;

public class CategoryModel {
    String categoryId;
    String categoryName;

    public CategoryModel(){}

    public CategoryModel(String categoryId,String name){
        this.categoryId=categoryId;
        this.categoryName =name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
