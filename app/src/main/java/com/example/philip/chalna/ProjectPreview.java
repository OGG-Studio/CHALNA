package com.example.philip.chalna;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;

public class ProjectPreview extends AppCompatActivity {
    Context context;
    private final String TAG = "Project_Preview";

    String dir_path;
    GalleryAdapterModel galleryAdapterModel;

    /**
     *  Data
     */
    DBSQLiteModel dbHelpter;
    ProjectData project_meta;
    /**
     *
     *  UI
     */
    LinearLayout container;
    SeekBar seekBar;

    Button saveBtn;
    Button takePictureBtn;
    Button showBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_preview);

        Intent intent = getIntent();
        String project_name = intent.getStringExtra("PROJECT_NAME");

        context = this;
        dbHelpter = DBSQLiteModel.getInstance(context);
        project_meta = dbHelpter.getDataByName(project_name);

//        dir_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Test";
        dir_path = project_meta.dir;
        galleryAdapterModel = new GalleryAdapterModel(this, dir_path);

        /***************************************************************************
         *                          UI
         ***************************************************************************/
        //Btn
        saveBtn = findViewById(R.id.project_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryAdapterModel.saveGIF();
            }
        });
        takePictureBtn = findViewById(R.id.project_takePicture_btn);
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraController.class);
                startActivity(intent);
            }
        });

        //UI SEEK BAR
        seekBar = findViewById(R.id.project_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String imageName = galleryAdapterModel.getImageFileNames()[progress];

                final ImageView imgView = findViewById(R.id.project_imageView);
                Glide.with(context).load(dir_path+"/"+imageName).into(imgView);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        if(galleryAdapterModel.getCount()>0){
            seekBar.setMax(galleryAdapterModel.getCount()-1);
            seekBar.setProgress(galleryAdapterModel.getCount()-1);
        }

        showBtn = findViewById(R.id.project_show);

    }

//    @Override
//    private void updatePreview(){
//        String[] imagePaths = galleryAdapterModel.getImageFileNames();
//        for(String imgPath : imagePaths){
//            File imgFile = new File(dir_path+"/"+imgPath);
//            if(imgFile.exists()){
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                int h = 200;
//                int height = myBitmap.getHeight();
//                int width = myBitmap.getWidth();
//                Log.d("PROJECT_PREVIEW", " " + h + " " + height + " " + width);
//                Bitmap resized = null;
//                while (height > h) {
//                    resized = Bitmap.createScaledBitmap(myBitmap, (width * h) / height, h, true);
//                    height = resized.getHeight();
//                    width = resized.getWidth();
//                }
//                ImageView imgView = new ImageView(this);
//                imgView.setImageBitmap(resized);
//                imgView.setScaleType(ImageView.ScaleType.FIT_START);
//                container.addView(imgView);
//            }
//        }
//    }
}
