package com.nsa.flexjobs.Extra;


import android.app.ProgressDialog;
import android.content.Context;


public class Progress {
    Context context;
     ProgressBar progressBar;
    public Progress(Context context) {
        this.context = context;
        progressBar=new ProgressBar(context, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);

    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setMessage(String message){
        progressBar.setMessage(message);
    }
    public void setTitle(String message){
        progressBar.setTitle(message);
    }
    public boolean isShowing(){
        return progressBar.isShowing();
    }
    public  void show(){
        progressBar.show();
    }
    public void dismiss(){
        if(progressBar.isShowing())
        progressBar.dismiss();
    }

}
