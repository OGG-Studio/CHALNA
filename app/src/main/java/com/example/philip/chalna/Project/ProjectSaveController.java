package com.example.philip.chalna.Project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.philip.chalna.Database.DBSQLiteModel;
import com.example.philip.chalna.Database.ProjectData;
import com.example.philip.chalna.R;
import com.example.philip.chalna.Utils.DescriptionManager;
import com.example.philip.chalna.Utils.FileManagementUtil;
import com.example.philip.chalna.Utils.GalleryAdapterModel;
import com.example.philip.chalna.Utils.LoadingClass;
import com.example.philip.chalna.Utils.StaticInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.philip.chalna.Project.PreviewDialog.MAXIMUM_PREVIEW_IMAGE_NUM;

public class ProjectSaveController extends AppCompatActivity {
    String[] resolutionList = new String[5];

    public static ProjectData projectData = null;
    public static GalleryAdapterModel galleryAdapterModel = null;

    public LoadingClass loadingClass;
    public Handler handler = new Handler();

    private final int maxDelay = 500;

    public static final String text_loading = "찰나를 정리하는 중...";
    List<Bitmap> bitmapList = new ArrayList<>();
    DBSQLiteModel myDB;

    SeekBar seekBar;
    ImageView imageView;

    //INFORMATION
    SharedPreferences sh_pref;
    SharedPreferences.Editor sh_edit;

    Integer lastIndex = 0;
    int delay = 100;
    int width = 0;
    int height = 0;
    AnimationDrawable animView = null;

    Activity activity = this;
    Context context = this;

    // Button
    ImageButton btn1;
    ImageButton btn2;
    ImageButton btn3;

    //CONSATNT
    public static final String SAVE_DB = "Save_Meta";
    public static final String SAVE_DELAY = "delay";
    public static final String SAVE_WIDTH = "width";
    public static final String SAVE_HEIGHT = "height";
    public static final String SAVE_POSITION = "position";
    private Spinner period_spinner;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_project);

        if(galleryAdapterModel==null || projectData == null) return;

        galleryAdapterModel.UpdateGallery();
        if(galleryAdapterModel.getCount()==0){
            finish();
            return;
        }

        myDB = DBSQLiteModel.getInstance(context);

        animView = new AnimationDrawable();
        animView.setOneShot(false);

        imageView = findViewById(R.id.project_save_imageview);
        imageView.setImageDrawable(animView);

        loadingClass = new LoadingClass();
        loadingClass.loadingOn(activity, text_loading);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] str = galleryAdapterModel.getImageFileNames();
                if(str==null){
                    loadingClass.loadingOff();
                    return;
                }
                if(str.length >= 100){
                    // 거대한 이미지 처리
                    Toast.makeText(context, "사진이 너무 많습니다.",Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "뒤에서 "+MAXIMUM_PREVIEW_IMAGE_NUM+"개의 사진만 보여집니다..",Toast.LENGTH_SHORT).show();
                    largeImageProc();
                }else{
                    smallImageProc();
                }
            }
        }).start();

        seekBar = findViewById(R.id.project_save_seekbar);
        sh_pref = getSharedPreferences(SAVE_DB, MODE_PRIVATE);

        delay = sh_pref.getInt(SAVE_DELAY,100);
        seekBar.setProgress((delay*seekBar.getMax())/maxDelay);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser==true){
                    delay = maxDelay*progress/seekBar.getMax();
                    animRestart();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sh_pref = getSharedPreferences(SAVE_DB, MODE_PRIVATE);
                if(sh_pref!=null){
                    sh_edit = sh_pref.edit();
                    sh_edit.putInt(SAVE_DELAY, delay);
                    sh_edit.commit();
                }
            }
        });

        resolutionListInit();
        sh_pref = getSharedPreferences(SAVE_DB, MODE_PRIVATE);
        width = sh_pref.getInt(SAVE_WIDTH,0);
        height = sh_pref.getInt(SAVE_HEIGHT, 0);

        // 스피너 - 반복 주기를 선택
        period_spinner = (Spinner) findViewById(R.id.project_save_spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, resolutionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        period_spinner.setAdapter(adapter);
        period_spinner.setSelection(sh_pref.getInt(SAVE_POSITION,2));

        period_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if(resolutionList[position].equals("사용자 지정 해상도")) {
                    width = 0;
                    height = 0;
                    return;
                }

                String[] selected = resolutionList[position].split(" ")[0].split("x");
                Log.d("DEBUG_TEST",resolutionList[position]);
                width = Integer.parseInt(selected[0]);
                height = Integer.parseInt(selected[1]);
                sh_pref = getSharedPreferences(SAVE_DB, MODE_PRIVATE);
                sh_edit = sh_pref.edit();
                sh_edit.putInt(SAVE_POSITION, position);

                if(position==4){
                    if(sh_pref!=null){
                        sh_edit.putInt(SAVE_WIDTH, width);
                        sh_edit.putInt(SAVE_HEIGHT, height);
                    }
                }
                sh_edit.commit();
            }
            @Override
            public void onNothingSelected(AdapterView adapterView) {
            }
        });

        TextView tv = findViewById(R.id.project_save_name);
        tv.setText(projectData.name);

        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolutionDialog();
            }
        });
        btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }
    private void save(){
        final String forware_message = "찰나를 기록중... ";
        loadingClass.loadingOn(activity, forware_message);
        new Thread() {
            @Override
            public void run() {
                galleryAdapterModel.setGIFSetting(delay, width, height);
                if (galleryAdapterModel.saveGIF(loadingClass.tv_progress_message, forware_message,projectData.name+"_result")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadingClass.loadingOff();
                            projectData.description = DescriptionManager.getSaveDescription();
                            Date currentTime = new Date();
                            projectData.modificationDate = currentTime.getTime();
                            projectData.is_modify = StaticInformation.FALSE;
                            myDB.syncProjectData(projectData);
                            Toast.makeText(context,"기록되었습니다!",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                };
            }
        }.start();
    }
    private void resolutionDialog(){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.preview_setting);
        dialog.setTitle("해상도 설정");

        final EditText width_tv = dialog.findViewById(R.id.save_dialog_width);
        width_tv.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final EditText height_tv = dialog.findViewById(R.id.save_dialog_height);
        height_tv.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Button regBtn = dialog.findViewById(R.id.loginButton);
        Button signupBtn = dialog.findViewById(R.id.signupButton);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(width_tv.getText().toString())<=10 || Integer.parseInt(width_tv.getText().toString())>1000
                        || Integer.parseInt(height_tv.getText().toString())<=10 || Integer.parseInt(height_tv.getText().toString())>1000
                        || width_tv.getText().toString().matches("") || height_tv.getText().toString().matches("") ){
                    Toast.makeText(context, "유효하지 않은 해상도입니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                width = Integer.parseInt(width_tv.getText().toString());
                height = Integer.parseInt(height_tv.getText().toString());
                sh_pref = getSharedPreferences(SAVE_DB, MODE_PRIVATE);

                resolutionList[4] = String.format("%dx%d ( 사용자 지정 )", width, height);
                period_spinner.setSelection(4);

                if(sh_pref!=null){
                    sh_edit = sh_pref.edit();
                    sh_edit.putInt(SAVE_WIDTH, width);
                    sh_edit.putInt(SAVE_HEIGHT, height);
                    sh_edit.putInt(SAVE_POSITION, 4);
                    sh_edit.commit();
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                return;
            }
        });
        dialog.show();
    }
    private void resolutionListInit() {
        if(galleryAdapterModel.getCount()>0){
            int bitmap_width = FileManagementUtil.getBitmapImgWidth(projectData.dir + "/" + galleryAdapterModel.getImageFileNames()[0]);
            int bitmap_height = FileManagementUtil.getBitmapImgHeight(projectData.dir + "/" + galleryAdapterModel.getImageFileNames()[0]);

            if(bitmap_height < bitmap_width){
                double aspectRatio = (double) bitmap_height/ (double) bitmap_width;
                int target = (int) (256 * aspectRatio);
                resolutionList[0] = String.format("%dx%d ( 저해상도 )", 256, target);
            }else{
                double aspectRatio = (double) bitmap_width/ (double) bitmap_height;
                int target = (int) (256 * aspectRatio);
                resolutionList[0] = String.format("%dx%d ( 저해상도 )", target, 256);
            }
            if(bitmap_height > bitmap_width){
                double aspectRatio = (double) bitmap_height/ (double) bitmap_width;
                int target = (int) (256 * aspectRatio);
                resolutionList[1] = String.format("%dx%d ( 저해상도 )", 256, target);
            }else{
                double aspectRatio = (double) bitmap_width/ (double) bitmap_height;
                int target = (int) (256 * aspectRatio);
                resolutionList[1] = String.format("%dx%d ( 고해상도 )", target, 256);
            }
            if(bitmap_height < bitmap_width){
                double aspectRatio = (double) bitmap_height/ (double) bitmap_width;
                int target = (int) (512 * aspectRatio);
                resolutionList[2] = String.format("%dx%d ( 고해상도 )", 512, target);
            }else{
                double aspectRatio = (double) bitmap_width/ (double) bitmap_height;
                int target = (int) (512 * aspectRatio);
                resolutionList[2] = String.format("%dx%d ( 고해상도 )", target, 512);
            }
            if(bitmap_height > bitmap_width){
                double aspectRatio = (double) bitmap_height/ (double) bitmap_width;
                int target = (int) (512 * aspectRatio);
                resolutionList[3] = String.format("%dx%d ( 고해상도 )", 512, target);
            }else{
                double aspectRatio = (double) bitmap_width/ (double) bitmap_height;
                int target = (int) (512 * aspectRatio);
                resolutionList[3] = String.format("%dx%d ( 고해상도 )", target, 512);
            }
            sh_pref = getSharedPreferences(SAVE_DB, MODE_PRIVATE);
            int saved_width = sh_pref.getInt(SAVE_WIDTH, 0);
            int saved_height = sh_pref.getInt(SAVE_HEIGHT, 0);
            if(saved_height == 0 || saved_width == 0){
                resolutionList[4] = String.format("사용자 지정 해상도");
            }else{
                resolutionList[4] = String.format("%dx%d ( 사용자 지정 )", saved_width, saved_height);
            }
        }
    }

    public void animRestart(){
        animStop();
        animStart();
    }
    public void animStart(){
        animView = new AnimationDrawable();
        animView.setOneShot(false);
        imageView.setImageDrawable(animView);

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
                        loadingClass.settingProgress(String.format(text_loading + " (%d/%d)", current, imageNum));
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
            }
        });
        loadingClass.loadingOff();
    }
    public void smallImageProc(){
        // Small
        imageProcAndProgress(Arrays.asList(galleryAdapterModel.getImageFileNames()));
        Log.d("DEBUG", bitmapList.size()+" ");
        handler.post(new Runnable() {
            @Override
            public void run() {
                animStart();
            }
        });
        loadingClass.loadingOff();
    }
    @Override
    public void onStop() {
        loadingClass.loadingOff();
        setResult(RESULT_CANCELED);
        finish();
        super.onStop();
    }
}
