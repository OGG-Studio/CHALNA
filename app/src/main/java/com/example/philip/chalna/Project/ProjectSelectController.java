package com.example.philip.chalna.Project;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.philip.chalna.Database.DBSQLiteModel;
import com.example.philip.chalna.Database.ProjectData;
import com.example.philip.chalna.Utils.GalleryAdapterModel;
import com.example.philip.chalna.R;
import com.example.philip.chalna.Utils.StaticInformation;
import com.example.philip.chalna.Utils.TimeClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ProjectSelectController extends AppCompatActivity {
    Context context;
    DBSQLiteModel myDB;

    Handler handler = new Handler();
    LinearLayout Playing_area;
    LinearLayout Finish_area;

    SharedPreferences sh_pref;
    SharedPreferences.Editor sh_edit;
    //UI
    LinearLayout playingContainer, completeContainer;
    private String TAG = "PROJECT_SELECT_CONTROLLER";

    FloatingActionButton menuOpenBtn, settingBtnItem, newProjectBtnItem;
    LinearLayout settingBtn , newProjectBtn;
    boolean isOpen = false;

    public boolean isFirstCreate = false;
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

    public void openMenuOption(){
        isOpen = true;
        menuOpenBtn.animate().rotation(45).start();
        settingBtn.setVisibility(View.VISIBLE);
        newProjectBtn.setVisibility(View.VISIBLE);

        settingBtn.animate().translationY(-200).start();
        newProjectBtn.animate().translationY(-400).start();
    }
    public void closeMenuOption(){
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_select);

        context = this;

        myDB = DBSQLiteModel.getInstance(this);

        Playing_area = findViewById(R.id.playing_area);
        Finish_area = findViewById(R.id.finish_area);

        playingContainer = findViewById(R.id.select_playing_container);
        completeContainer = findViewById(R.id.select_complete_container);

        menuOpenBtn = findViewById(R.id.select_floating_btn);
        menuOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpen){
                    openMenuOption();
                }else{
                    closeMenuOption();
                }
            }
        });

        newProjectBtn = findViewById(R.id.select_createProject_btn);
        newProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjectCreateController.class);
                startActivityForResult(intent, StaticInformation.REQUEST_CREATE_CONTROL);
            }
        });
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
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingController.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        playingContainer.removeAllViews();
        completeContainer.removeAllViews();
        drawCurrentProject();
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isOpen) {
            closeMenuOption();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // do Something
        if (isFirstCreate == false) {
            isFirstCreate = true;
            sh_pref = getSharedPreferences(StaticInformation.TUTORIAL_PROJECT, MODE_PRIVATE);
            if(sh_pref.getInt(StaticInformation.TUTORIAL_PROJECT_SELECTE, 0)==0){
                sh_edit = sh_pref.edit();
                sh_edit.putInt(StaticInformation.TUTORIAL_PROJECT_SELECTE, 1);
                sh_edit.commit();

                ProjectSelectTuto psc = new ProjectSelectTuto(this);
                psc.tutorial_start();
            }

        }
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
            //Sanity Check

            final PreviewItem previewItem = new PreviewItem(this);
            previewItem.name.setText(project.name);
            previewItem.description.setText(project.description);
            previewItem.startTime.setText(TimeClass.dateSummary(project.modificationDate));

            GalleryAdapterModel ga = GalleryAdapterModel.getInstance(context, project.dir);
            ga.UpdateGallery();
            String[] imageFileNames = ga.getImageFileNames();
            int count = imageFileNames.length;
            previewItem.alarmCycle.setText(String.format("[%d 장]", count));

            Log.d(TAG, "Image Update Test count : " + count);
            if(count>0){
                for(int i=0;i<count;i++){
                    Log.d(TAG, imageFileNames[i]);
                }
                Glide.with(context).load(project.dir+"/"+imageFileNames[count-1]).centerCrop().override(150,150).into(previewItem.imageView);
            }

            if(project.project_level == 0){
                previewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProjectPreviewController.class);
                        intent.putExtra("PROJECT_NAME", project.name);
                        startActivity(intent);
                    }
                });
                playingContainer.addView(previewItem);
            }else{
                previewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProjectCompleteController.class);
                        intent.putExtra("PROJECT_NAME", project.name);
                        startActivity(intent);
                    }
                });
                completeContainer.addView(previewItem);
            }
        }
    }
}
