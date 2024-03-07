package com.kgs.report.webapp;

import android.content.Context;
import android.widget.Toast;

public class WebAppInterface {
    private Context context;

    public WebAppInterface (Context context){
        this.context = context;
    }

    public void showToast(String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
