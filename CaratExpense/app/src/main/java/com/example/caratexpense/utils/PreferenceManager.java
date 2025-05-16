package com.example.caratexpense.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "carat_pref";
    private static final String KEY_FIRST_TIME_LAUNCH = "first_time_launch";
    private static final String KEY_USER_NAME = "user_name";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    
    public PreferenceManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(KEY_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(KEY_FIRST_TIME_LAUNCH, true);
    }
    
    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }
    
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }
}
