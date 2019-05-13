package com.namclu.android.bloquery;

import android.content.Context;
import android.content.SharedPreferences;

public class PaceSharedPreference {

    SharedPreferences.Editor mSEditor;
    SharedPreferences mSharedPreferences;

    public PaceSharedPreference(Context context){
        mSharedPreferences = context.getSharedPreferences("PaceShared",Context.MODE_PRIVATE);
        mSEditor = mSharedPreferences.edit();
    }


    public void setBooleanValue(String key, boolean value){
        mSEditor.putBoolean(key,value);
        mSEditor.apply();
    }

    public boolean getBooleanValue(String key){
        return mSharedPreferences.getBoolean(key,false);
    }


}
