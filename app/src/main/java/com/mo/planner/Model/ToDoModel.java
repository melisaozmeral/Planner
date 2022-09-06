package com.mo.planner.Model;

public class ToDoModel {
    String task;
    String date;
    String priority;
    String id;
    String categoryN;
    int status,itemId;

public ToDoModel(){}

    public ToDoModel(String task, String id, int status, String date,String priority,String categoryN) {
        this.task = task;
        this.id = id;
        this.status = status;
        this.date=date;
        this.priority=priority;
        this.categoryN =categoryN;

    }


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public int getStatus() {
        return status;
    }

     public void setStatus(int status){
            this.status = status;

        }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getCategoryN() {
        return categoryN;
    }

    public void setCategoryN(String categoryN) {
        this.categoryN = categoryN;
    }


}
