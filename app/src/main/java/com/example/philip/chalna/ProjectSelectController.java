package com.example.philip.chalna;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProjectSelectController extends AppCompatActivity {
    Context context;
    DBSQLiteModel myDB;
    //UI
    LinearLayout playingContainer, completeContainer;
    private String TAG = "PROJECT_SELECT_CONTROLLER";

    FloatingActionButton menuOpenBtn, settingBtnItem, newProjectBtnItem;
    LinearLayout settingBtn , newProjectBtn;
    boolean isOpen = false;

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


        menuOpenBtn = findViewById(R.id.select_floating_btn);
        menuOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpen){
                    isOpen = true;
                    menuOpenBtn.animate().rotation(45).start();
                    settingBtn.setVisibility(View.VISIBLE);
                    newProjectBtn.setVisibility(View.VISIBLE);

                    settingBtn.animate().translationY(-200).start();
                    newProjectBtn.animate().translationY(-400).start();
                }else{
                    isOpen = false;
                    menuOpenBtn.animate().rotation(0).start();

                    settingBtn.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(isOpen==false)
                                settingBtn.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }
                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();

                    newProjectBtn.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(isOpen==false)
                                newProjectBtn.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }
                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
                }
            }
        });

        newProjectBtn = findViewById(R.id.select_createProject_btn);
        newProjectBtnItem = findViewById(R.id.select_createProject_btn_item);
        newProjectBtnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjectCreateController.class);
                startActivityForResult(intent, StaticInformation.REQUEST_CREATE_CONTROL);
            }
        });
        newProjectBtn.setVisibility(View.GONE);

        settingBtn = findViewById(R.id.floating_setting_btn);
        settingBtn.setVisibility(View.GONE);
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
        Collections.sort(pList, new Comparator<ProjectData>() {
            @Override
            public int compare(ProjectData o1, ProjectData o2) {
                if(o1.modificationDate > o2.modificationDate){
                    return -1;
                }else if (o1.modificationDate < o2.modificationDate){
                    return 1;
                }else{
                    return 0;
                }
            }
        });

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
