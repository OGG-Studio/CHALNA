package com.example.philip.chalna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.util.ArrayList;

public class ProjectSelectController extends AppCompatActivity {

    Context context;
    DBSQLiteModel myDB;
    //UI
    Button newProjectBtn;
    LinearLayout playingContainer, completeContainer;
    private String TAG = "PROJECT_SELECT_CONTROLLER";

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == StaticInformation.REQUEST_CREATE_CONTROL) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG,"DEBUG_TEST : RESULT_OK");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }//onActivityResult

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_select);

        context = this;

        myDB = DBSQLiteModel.getInstance(this);

        playingContainer = findViewById(R.id.select_playing_container);
        completeContainer = findViewById(R.id.select_complete_container);

        newProjectBtn = findViewById(R.id.select_createProject_btn);
        newProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjectCreateController.class);
                startActivityForResult(intent, StaticInformation.REQUEST_CREATE_CONTROL);
            }
        });

//        myDB.debug_db();
    }
    @Override
    protected void onResume() {
        playingContainer.removeAllViews();
        completeContainer.removeAllViews();
        drawCurrentProject();
        super.onResume();
    }

    void drawCurrentProject(){
        myDB.projectSanityCheck();
        ArrayList<ProjectData> pList = myDB.selectAllFromPROJECT();

        //Sorting 수정 순
        for(final ProjectData project : pList){
            final PreviewItem previewItem = new PreviewItem(this);
            previewItem.name.setText(project.name);
            previewItem.description.setText(project.description);
            previewItem.startTime.setText(TimeClass.dateSummary(project.modificationDate));

            GalleryAdapterModel ga = GalleryAdapterModel.getInstance(context, project.dir);
            ga.UpdateGallery();
            String[] imageFileNames = ga.getImageFileNames();
            int count = imageFileNames.length;
            Log.d(TAG, "Image Update Test count : " + count);
            if(count>0){
                for(int i=0;i<count;i++){
                    Log.d(TAG, imageFileNames[i]);
                }
                Glide.with(context).load(project.dir+"/"+imageFileNames[count-1]).centerCrop().override(150,150).into(previewItem.imageView);
            }

            previewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProjectPreviewController.class);
                    intent.putExtra("PROJECT_NAME", project.name);
                    startActivity(intent);
                }
            });
            if(project.project_level == 0){
                playingContainer.addView(previewItem);
            }else{
                completeContainer.addView(previewItem);
            }
        }
    }
}
