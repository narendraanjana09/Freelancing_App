package com.nsa.labelimages.Extra;

import android.content.Context;
import android.content.SharedPreferences;

import com.nsa.labelimages.R;

public class SavedText {
    SharedPreferences sharedpreferences;
    Context context;

    public SavedText(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }
    public void setText(String key,String text){
        sharedpreferences.edit().putString(key,text).apply();
    }
    public String getText(String key){
        return sharedpreferences.getString(key,"");
    }
    public void remove(String key){
        sharedpreferences.edit().remove(key).commit();
    }

}
