package com.studioOgg.philip.chalna.Project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.studioOgg.philip.chalna.Database.DBSQLiteModel;
import com.studioOgg.philip.chalna.Database.ProjectData;
import com.studioOgg.philip.chalna.R;
import com.studioOgg.philip.chalna.Utils.GalleryAdapterModel;
import com.studioOgg.philip.chalna.Utils.LoadingClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static com.studioOgg.philip.chalna.Project.ProjectSaveController.SAVE_DB;
import static com.studioOgg.philip.chalna.Project.ProjectSaveController.SAVE_DELAY;

public class PreviewDialog {

    public static ProjectData projectData = null;
    public static GalleryAdapterModel galleryAdapterModel = null;
    public List<Bitmap> bitmapList;

    AppCompatDialog progressDialog;
    Context context;
    LoadingClass loading = new LoadingClass();

    Handler handler = new Handler();
    int delay = 100;
    int lastIndex = 0;

    String text_loading;

    AnimationDrawable animView = null;

    ImageView iv;
    public DBSQLiteModel myDB;
    public SeekBar seekBar;

    public SharedPreferences sh_pref;
    public SharedPreferences.Editor sh_edit;

    private static final int maxDelay = 500;
    public static final int MAXIMUM_PREVIEW_IMAGE_NUM = 100;

    public  PreviewDialog(Context c){
        context = c;
    }

    public void previewOn(Activity activity){
        if(activity==null || activity.isFinishing()){
            return;
        }

        if(galleryAdapterModel==null || projectData == null) return;


        bitmapList = new ArrayList<>();

        myDB = DBSQLiteModel.getInstance(context);

        animView = new AnimationDrawable();
        animView.setOneShot(false);

        progressDialog = new AppCompatDialog(activity);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Transparency
        progressDialog.setContentView(R.layout.mini_preview_dialog);

        iv = progressDialog.findViewById(R.id.preview_image_viewer);

        text_loading = "Preview Ready... ";
        loading.loadingOn(activity, text_loading);

        seekBar = progressDialog.findViewById(R.id.preview_seekbar);
        sh_pref = context.getSharedPreferences(SAVE_DB, MODE_PRIVATE);

        delay = sh_pref.getInt(SAVE_DELAY,100);
        seekBar.setProgress((delay*seekBar.getMax())/maxDelay);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delay = maxDelay*progress/seekBar.getMax();
                animRestart();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sh_pref = context.getSharedPreferences(SAVE_DB, MODE_PRIVATE);
                if(sh_pref!=null){
                    sh_edit = sh_pref.edit();
                    sh_edit.putInt(SAVE_DELAY, delay);
                    sh_edit.commit();
                }
            }
        });

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                animStop();
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.default_preview));
                bitmapList = null;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] str = galleryAdapterModel.getImageFileNames();
                if(str.length > MAXIMUM_PREVIEW_IMAGE_NUM){
                    // 거대한 이미지 처리
                    Toast.makeText(context, "사진이 너무 많습니다.",Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "뒤에서 "+MAXIMUM_PREVIEW_IMAGE_NUM+"개의 사진만 보여집니다..",Toast.LENGTH_SHORT).show();
                    largeImageProc();

                }else{
                    smallImageProc();
                }
            }
        }).start();
    }
    public void animRestart(){
        animStop();
        animStart();
    }
    public void animStart(){
        animView = new AnimationDrawable();
        animView.setOneShot(false);
        iv.setImageDrawable(animView);

        final int imageNum = galleryAdapterModel.getCount();
        for(int i=0;i!=imageNum; i++){
            animView.addFrame(new BitmapDrawable(bitmapList.get(i)), delay);
        }
        animView.start();
    }

    public void animStop(){
        animView.stop();

        Drawable currentFrame, checkFrame;
        currentFrame = animView.getCurrent();
        int frameNumber = 0;

        // Checks the position of the frame
        for (int i = 0; i < animView.getNumberOfFrames(); i++) {
            checkFrame = animView.getFrame(i);
            if (checkFrame == currentFrame) {
                frameNumber = i;
                break;
            }
        }
        lastIndex = frameNumber;
    }
    public List<String> reverseNlist(String[] pre, int N){
        int listNum = pre.length;
        return Arrays.asList(pre).subList(listNum-N,listNum);
    }
    public void imageProcAndProgress(List<String> list){
        int i = 0;
        final int imageNum = list.size();
        for(String file_name : list){
            try {
                final int current = i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.settingProgress(String.format(text_loading + " (%d/%d)", current, imageNum));
                    }
                });
                Log.e("DEBUG_TEST", projectData.dir + "/"+file_name);
                Bitmap myBitmap = Glide.with(context)
                        .load(projectData.dir + "/" + file_name)
                        .asBitmap()
                        .into(200, 200)
                        .get();
                bitmapList.add(myBitmap);
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    public void largeImageProc(){
        // large
        imageProcAndProgress(reverseNlist(galleryAdapterModel.getImageFileNames(), MAXIMUM_PREVIEW_IMAGE_NUM));
        Log.d("DEBUG", bitmapList.size()+" ");
        handler.post(new Runnable() {
            @Override
            public void run() {
                animStart();
                progressDialog.show();
            }
        });
        loading.loadingOff();
    }
    public void smallImageProc(){
        // Small
        imageProcAndProgress(Arrays.asList(galleryAdapterModel.getImageFileNames()));
        Log.d("DEBUG", bitmapList.size()+" ");
        handler.post(new Runnable() {
            @Override
            public void run() {
                animStart();
                progressDialog.show();
            }
        });
        loading.loadingOff();
    }
}