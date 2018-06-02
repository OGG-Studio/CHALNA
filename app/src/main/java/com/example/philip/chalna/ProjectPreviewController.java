package com.example.philip.chalna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ProjectPreviewController extends AppCompatActivity {
    Context context = this;
    Activity activity = this;
    private final String TAG = "Project_Preview";

    Handler handler = new Handler();

    String dir_path;
    GalleryAdapterModel galleryAdapterModel;

    /**
     *  Data
     */
    DBSQLiteModel myDB;
    ProjectData project_meta;

    boolean isOnResume;

    // Dialog
    LoadingClass loading;
    PreviewDialog previewDialog;
    /**
     *
     *  UI
     */
    LinearLayout container;
    SeekBar seekBar;

    Button saveBtn;
    Button takePictureBtn;
    Button showBtn;
    Button previewBtn;

    /**
       Animation test
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_preview);

        Intent intent = getIntent();
        final String project_name = intent.getStringExtra("PROJECT_NAME");

        context = this;
        myDB = DBSQLiteModel.getInstance(context);
        project_meta = myDB.getDataByNameFromPROJECT(project_name);

//        dir_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Test";
        dir_path = project_meta.dir;

        galleryAdapterModel = GalleryAdapterModel.getInstance(this, dir_path);

        /***************************************************************************
         *                          UI
         ***************************************************************************/
        loading = new LoadingClass();
        previewDialog = new PreviewDialog(this);

        //Btn
        saveBtn = findViewById(R.id.project_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.loadingOn(activity, "loading...");
                new Thread(){
                    @Override
                    public void run(){
                        if(galleryAdapterModel.saveGIF()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loading.loadingOff();
                                }
                            });
                        };
                    }
                }.start();
            }
        });
        takePictureBtn = findViewById(R.id.project_takePicture_btn);
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraController.class);
                intent.putExtra("DIR", dir_path);
                intent.putExtra("PROJECT_NAME", project_name);
                startActivity(intent);
                isOnResume = false;
                onWindowFocusChanged(true);
            }
        });

        //UI SEEK BAR
        seekBar = findViewById(R.id.project_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String imageName = galleryAdapterModel.getImageFileNames()[progress];

                final ImageView imgView = findViewById(R.id.project_imageView);

                int width = imgView.getWidth();
                int height = imgView.getHeight();

                //Optimization ..
                Log.d(TAG, "image View size : "+width+" "+height);
                Glide.with(context).load(dir_path+"/"+imageName).override(width,height).into(imgView);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        showBtn = findViewById(R.id.project_show);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.loadingOn(activity, "Preview Ready...");
                new Thread(){
                    @Override
                    public void run(){
                        final byte[] anim = galleryAdapterModel.generateGIF();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loading.loadingOff();
                                previewDialog.previewOn(activity, anim);
                            }
                        });

                    }
                }.start();

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        isOnResume = false;
    }
    @Override
    public void onPause() {
        super.onPause();
        isOnResume = false;
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        Log.d("onWindowFocusChanged", "Change : " + isOnResume);
        // do Something
        if(isOnResume==false){
            isOnResume = true;
            galleryAdapterModel = GalleryAdapterModel.getInstance(this, dir_path);
            galleryAdapterModel.UpdateGallery();
            Log.d("onWindowFocusChanged", "GALALY UPDATE : " + galleryAdapterModel.getCount());
            if(galleryAdapterModel.getCount()>0){
                seekBar.setMax(galleryAdapterModel.getCount()-1);
                seekBar.setProgress(galleryAdapterModel.getCount()-1);
            }
        }
    }
}
