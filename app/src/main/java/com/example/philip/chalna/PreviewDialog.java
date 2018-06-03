package com.example.philip.chalna;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class PreviewDialog {
    AppCompatDialog progressDialog;
    Context context;

    public  PreviewDialog(Context c){
        context = c;
    }

    public void previewOn(Activity activity, final byte[] gif_byte_Image){
        if(activity==null || activity.isFinishing()){
            return;
        }
        progressDialog = new AppCompatDialog(activity);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Transparency
        progressDialog.setContentView(R.layout.preview_dialog);
        progressDialog.show();

        // Animation
        final ImageView img_loading_frame = progressDialog.findViewById(R.id.project_preview_img);
        img_loading_frame.post(new Runnable() { @Override public void run() {
            Glide.with(context)
                    .load(gif_byte_Image)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(img_loading_frame);
        } });
    }


    public void previewOff(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}