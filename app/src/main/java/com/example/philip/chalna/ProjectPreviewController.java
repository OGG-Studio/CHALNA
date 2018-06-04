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
                loading.loadingOn(activity, "Preview Ready...");
                new Thread() {
                    @Override
                    public void run() {
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
    public void share(String image_path){
        File file = new File(image_path);
        Uri mSaveImageUri = Uri.fromFile(file); //file의 경로를 uri로 변경합니다.
        Intent intent = new Intent(Intent.ACTION_SEND); //전송 메소드를 호출합니다. Intent.ACTION_SEND
        intent.setType("image/gif"); //jpg 이미지를 공유 하기 위해 Type을 정의합니다.
        intent.putExtra(Intent.EXTRA_STREAM, mSaveImageUri); //사진의 Uri를 가지고 옵니다.
        startActivity(Intent.createChooser(intent, "Choose")); //Activity를 이용하여 호출 합니다.
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.action_button_share){
            File file = new File(project_meta.dir+"/result.gif");
            if(file.exists()){
                share(project_meta.dir+"/result.gif");
            }else{
                Toast.makeText(context, "이미지를 공유하기 위해서는 '저장하기'를 먼저 눌러주세요.", Toast.LENGTH_SHORT);
            }
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
                loading.loadingOn(activity, "loading...");
                new Thread() {
                    @Override
                    public void run() {
                        galleryAdapterModel.setGIFSetting(gif_duration, width);
                        if (galleryAdapterModel.saveGIF()) {
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
        frameTimeEditText.setGravity(Gravity.CENTER);
        frameTimeEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setView(frameTimeEditText);

        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String frame = frameTimeEditText.getText().toString();
                int duration = Integer.parseInt(frame);
                if(duration<80 || duration > 1000){
                    Toast.makeText(context, "유효하지 않은 입력입니다. [80ms~1000ms]", Toast.LENGTH_SHORT).show();
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
