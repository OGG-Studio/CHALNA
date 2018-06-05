package com.example.philip.chalna;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.Date;

public class ProjectPreviewController extends AppCompatActivity {
    Context context = this;
    Activity activity = this;
    private final String TAG = "Project_Preview";

    Handler handler = new Handler();

    String dir_path;
    GalleryAdapterModel galleryAdapterModel;

    /**
     * Data
     */
    DBSQLiteModel myDB;
    ProjectData project_meta;

    boolean isOnResume;

    // Dialog
    LoadingClass loading;
    PreviewDialog previewDialog;
    /**
     * UI
     */
    LinearLayout container;
    SeekBar seekBar;

    ImageButton saveBtn;
    ImageButton takePictureBtn;
    ImageButton showBtn;
    ImageButton deleteBtn;

    TextView project_name_tv;

    AlertDialog alertDialog;
    /**
     * Animation test
     */
    int gif_duration = 0;
    int gif_width = 0;
    int gif_height = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_preview2);

        Intent intent = getIntent();
        final String project_name = intent.getStringExtra("PROJECT_NAME");
        Log.d("ALARM",intent.getExtras().containsKey("PROJECT_NAME")+" ");

        //Target 24>= inflict

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        context = this;
        myDB = DBSQLiteModel.getInstance(context);
        Log.d(TAG,"ALARM : Project Name : " + project_name);

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
                timingDialog();
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
                if(galleryAdapterModel.getCount()>=2)
                    previewDraw(progress);
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
                final String forware_message = "Preview Ready... ";
                loading.loadingOn(activity, forware_message);
                new Thread() {
                    @Override
                    public void run() {
                        galleryAdapterModel.setGIFSetting(gif_duration, 256);
                        final byte[] anim = galleryAdapterModel.generateGIF(loading.tv_progress_message, forware_message);
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
        deleteBtn = findViewById(R.id.project_delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(galleryAdapterModel.getCount()<=0) return;

                File file;
                if(galleryAdapterModel.getCount()>1){
                    file = new File(dir_path+"/"+galleryAdapterModel.getImageFileNames()[seekBar.getProgress()]);
                }else{
                    file = new File(dir_path+"/"+galleryAdapterModel.getImageFileNames()[0]);
                }
                boolean deleted = file.delete();
                if(deleted){
                    Toast.makeText(context, "성공적으로 삭제하였습니다",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "DELETE ERROR",Toast.LENGTH_SHORT).show();
                }
                updateGallaryImage();
                String[] date = TimeClass.getDate();
                project_meta.description = DescriptionManager.getRemoveDescription(date[0],date[1],date[2],galleryAdapterModel.getCount());

                if(galleryAdapterModel.getCount()==0){
                    project_meta.wide = StaticInformation.DISPLAY_ORIENTATION_DEFAULT;

                    ImageView imgView = findViewById(R.id.project_imageView);
                    imgView.setImageDrawable(getResources().getDrawable(R.drawable.default_preview));
                }
                myDB.syncProjectData(project_meta);
            }
        });

        project_name_tv = findViewById(R.id.project_current_name);
        project_name_tv.setText(project_meta.name);
    }

    public void previewDraw(int progress){
        final ImageView imgView = findViewById(R.id.project_imageView);
        if (galleryAdapterModel.getCount() < 0) return;
        if(galleryAdapterModel.getCount()==0) {
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.default_preview)); return;
        }

        String imageName = galleryAdapterModel.getImageFileNames()[progress];

        int width = imgView.getWidth();
        int height = imgView.getHeight();

        //Optimization ..
        Log.d(TAG, "image View size : " + width + " " + height);
        Glide.with(context).load(dir_path + "/" + imageName).override(width, height).into(imgView);
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

    public void updateGallaryImage(){
        galleryAdapterModel = GalleryAdapterModel.getInstance(this, dir_path);
        galleryAdapterModel.UpdateGallery();
        if (galleryAdapterModel.getCount() > 0) {
            if(galleryAdapterModel.getCount()==1){
                previewDraw(0);
            }else{
                seekBar.setMax(galleryAdapterModel.getCount() - 1);
                seekBar.setProgress(galleryAdapterModel.getCount() - 1);
            }
        }else{
            previewDraw(0);
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("onWindowFocusChanged", "Change : " + isOnResume);
        // do Something
        if (isOnResume == false) {
            isOnResume = true;
            Log.d("onWindowFocusChanged", "GALALY UPDATE : " + galleryAdapterModel.getCount());
            updateGallaryImage();
        }
    }

    //OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview_menu, menu);
        return true;
    }
    void DeleteDir(String path)
    {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        for(File childFile : childFileList)
        {
            if(childFile.isDirectory()) {
                DeleteDir(childFile.getAbsolutePath());     //하위 디렉토리 루프
            }
            else {
                childFile.delete();    //하위 파일삭제
            }
        }
        file.delete();    //root 삭제
    }
    public void delete(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("당신의 찰나를 지울까요?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DeleteDir(project_meta.dir);
                            Thread.sleep(500);
                            Toast.makeText(context, "굿바이!", Toast.LENGTH_LONG).show();
                            Thread.sleep(500);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }
    public void share(String image_path){
        File file = new File(image_path);
        Uri mSaveImageUri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/gif");
        intent.putExtra(Intent.EXTRA_STREAM, mSaveImageUri);
        startActivity(Intent.createChooser(intent, "Choose"));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.action_button_share){
            File file = new File(project_meta.dir+"/result.gif");
            if(file.exists()){
                share(project_meta.dir+"/result.gif");
            }else{
                Toast.makeText(context, "이미지를 공유하기 위해서는 '저장하기'를 먼저 눌러주세요.", Toast.LENGTH_SHORT).show();
            }
        }
        if(id==R.id.action_button_delete){
            delete();
        }
        return super.onOptionsItemSelected(item);
    }

    public void resolutionDialog(){
        alertDialog.dismiss();
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("GIF 환경설정");
        alert.setMessage("이미지의 작은 변 크기를 설정해주세요.\n 이미지의 비율은 유지됩니다.");
        alert.setCancelable(true);

        final EditText frameTimeEditText = new EditText(context);
        frameTimeEditText.setText("512");
        frameTimeEditText.setSelection(frameTimeEditText.length());
        frameTimeEditText.setGravity(Gravity.CENTER);
        frameTimeEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setView(frameTimeEditText);

        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final int width = Integer.parseInt(frameTimeEditText.getText().toString());

                if(width <=30){
                    Toast.makeText(context, "이미지가 너무 작습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(width>2000){
                    Toast.makeText(context, "이미지가 너무 큽니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String forware_message = "loading... ";
                loading.loadingOn(activity, forware_message);
                new Thread() {
                    @Override
                    public void run() {
                        galleryAdapterModel.setGIFSetting(gif_duration, width);
                        if (galleryAdapterModel.saveGIF(loading.tv_progress_message, forware_message)) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loading.loadingOff();
                                    project_meta.description = DescriptionManager.getSaveDescription();
                                    Date currentTime = new Date();
                                    project_meta.modificationDate =currentTime.getTime();

                                    myDB.syncProjectData(project_meta);
                                }
                            });
                        };
                    }
                }.start();
            }
        });
        alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialog.dismiss();
            }
        });
        alertDialog = alert.show();
    }
    public void timingDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("GIF 환경설정");
        alert.setMessage("프레임을 몇 초 단위로 전환할까요?\n(1000 = 1초)");
        alert.setCancelable(true);

        final EditText frameTimeEditText = new EditText(context);
        frameTimeEditText.setText("100");
        frameTimeEditText.setSelection(frameTimeEditText.length());
        frameTimeEditText.setGravity(Gravity.CENTER);
        frameTimeEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setView(frameTimeEditText);

        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String frame = frameTimeEditText.getText().toString();
                int duration = Integer.parseInt(frame);
                if(duration<50 || duration > 1000){
                    Toast.makeText(context, "유효하지 않은 입력입니다. [50ms~1000ms]", Toast.LENGTH_SHORT).show();
                }else{
                    gif_duration = duration;
                    resolutionDialog();
                }
            }
        });
        alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialog.dismiss();
            }
        });
        alertDialog = alert.show();
    }
}
