package com.studioOgg.philip.chalna.Utils;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studioOgg.philip.chalna.R;

public class LoadingClass {
    AppCompatDialog progressDialog;
    public TextView tv_progress_message;
    public RelativeLayout container;


    /**
     * loading processing
     * @param activity
     * @param message
     * @param currentOrientation
     */
    public void loadingOn(Activity activity, String message, int currentOrientation){
        if(activity==null || activity.isFinishing()){
            return;
        }

        if (progressDialog!=null && progressDialog.isShowing()){
            settingProgress(message);
        }else{
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Transparency
            progressDialog.setContentView(R.layout.progress_loading);
            container = progressDialog.findViewById(R.id.loading_frame_container);

            if(currentOrientation == StaticInformation.CAMERA_ORIENTATION_RIGHT){
                container.setRotation(-180);
            }else if(currentOrientation==StaticInformation.CAMERA_ORIENTATION_PORTRAIT){
                container.setRotation(-90);
            }else{
                container.setRotation(0);
            }

            progressDialog.show();
        }

        // Animation
        final ImageView img_loading_frame = progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() { @Override public void run() { frameAnimation.start(); } });

        tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    /**
     * loading start
     * @param activity
     * @param message
     */
    public void loadingOn(Activity activity, String message){
        if(activity==null || activity.isFinishing()){
            return;
        }

        if (progressDialog!=null && progressDialog.isShowing()){
            settingProgress(message);
        }else{
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Transparency
            progressDialog.setContentView(R.layout.progress_loading);
            container = progressDialog.findViewById(R.id.loading_frame_container);
            progressDialog.show();
        }

        // Animation
        final ImageView img_loading_frame = progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() { @Override public void run() { frameAnimation.start(); } });

        tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    /**
     * change progress text
     * @param message
     */
    public void settingProgress(String message){
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    /**
     * loading stop.
     */
    public void loadingOff(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
