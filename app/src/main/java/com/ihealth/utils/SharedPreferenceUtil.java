package com.ihealth.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
    public static String SP_FACE_DETECT_TIME="SP_FACE_DETECT_TIME";
    public static String SP_LOGIN_SUCESS_HOSPITAL_BEAN="SP_LOGIN_SUCESS_HOSPITAL_BEAN";
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
