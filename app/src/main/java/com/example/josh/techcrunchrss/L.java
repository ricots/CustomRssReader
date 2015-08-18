package com.example.josh.techcrunchrss;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Josh on 7/15/2015.
 */
public class L {
    public static void m(String message){
        Log.d("VIVZ", message);
    }
    public static void s(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
