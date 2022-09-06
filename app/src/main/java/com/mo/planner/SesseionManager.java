package com.mo.planner;

import android.content.Context;
import android.content.SharedPreferences;

public class SesseionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SesseionManager(Context context){
        sharedPreferences=context.getSharedPreferences("AppKey",0);
        editor=sharedPreferences.edit();
        editor.apply();
    }
    public void setFlag(Boolean flag){
        editor.putBoolean("KEY_FLAG",flag);
        editor.commit();
    }
    public boolean getFlag(){
        return sharedPreferences.getBoolean("KEY_FLAG",false);
    }
    public void setCurrentTime(String currentTime)
    {
        editor.putString("KEY_TIME",currentTime);
        editor.commit();
    }
    public String getCurrentTime(){
        return sharedPreferences.getString("KEY_TIME","");
    }
}
