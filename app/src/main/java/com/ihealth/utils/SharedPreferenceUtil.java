package com.ihealth.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
    public static String getStringTypeSharedPreference(Context context, String spKey, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(spKey, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

    public static void editSharedPreference(Context context, String spKey, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(spKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static void clearSharedPreference(Context context, String spKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(spKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
