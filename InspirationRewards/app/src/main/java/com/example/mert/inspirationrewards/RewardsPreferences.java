package com.example.mert.inspirationrewards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class RewardsPreferences {
    private static final String TAG = "RewardsPreferences";
    private SharedPreferences preference;
    private Editor ed;

    public RewardsPreferences(Activity activity) {
        super();
        Log.d(TAG, "RewardsPreferences: Constructor");
        preference = activity.getSharedPreferences(activity.getString(R.string.prefFile), Context.MODE_PRIVATE);
        ed = preference.edit();
    }
    public void save(String key, String text) {
        Log.d(TAG, "Save: " + key + ":" + text);
        ed.putString(key, text);
        ed.apply();
    }
    public void saveBool(String key, Boolean text) {
        Log.d(TAG, "Save: " + key + ":" + text);
        ed.putBoolean(key, text);
        ed.apply();
    }
    public String getValue(String key) {
        String text = preference.getString(key, "");
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }
    public Boolean getBoolValue(String key) {
        Boolean text = preference.getBoolean(key, false);
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }
}