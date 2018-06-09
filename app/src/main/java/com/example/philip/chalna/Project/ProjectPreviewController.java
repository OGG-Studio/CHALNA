package com.example.philip.chalna.Project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.philip.chalna.Camera.CameraController;
import com.example.philip.chalna.Camera.CameraModel;
import com.example.philip.chalna.Database.DBSQLiteModel;
import com.example.philip.chalna.Database.ProjectData;
import com.example.philip.chalna.Utils.DescriptionManager;
import com.example.philip.chalna.Utils.FileManagementUtil;
import com.example.philip.chalna.Utils.GalleryAdapterModel;
import com.example.philip.chalna.Utils.LoadingClass;
import com.example.philip.chalna.R;
import com.example.philip.chalna.Utils.StaticInformation;
import com.example.philip.chalna.Utils.TimeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectPreviewController extends AppCompatActivity {
    Context context = this;
    Activity activity = this;
    private final String TAG = "Project_Preview";

    Handler handler = new Handler();

    String dir_path;
    GalleryAdapterModel galleryAdapterModel;
    ProjectPreviewModel projectPreviewModel;
    FrameLayout Image_View;

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
    ImageButton fetchBtn;

    TextView project_name_tv;


    AlertDialog alertDialog;
    /**
     * Animation test
     */
    private FileManagementUtil fileIOModel = new FileManagementUtil(context);
    private boolean isFirstCreate = false;
    private SharedPreferences sh_pref;
    private SharedPreferences.Editor sh_edit;


    public boolean projectInit(int project_id) {
        myDB = DBSQLiteModel.getInstance(context);

        project_meta = myDB.getDataByIdFromPROJECT(Integer.toString(project_id));

        //Check Valid Project
        if (!project_valid_check()) {
            return false;
        }

//        dir_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Test";
        dir_path = project_meta.dir;

        galleryAdapterModel = GalleryAdapterModel.getInstance(this, dir_path);
        return true;
    }

    public boolean projectInit(String project_name) {
        myDB = DBSQLiteModel.getInstance(context);

        project_meta = myDB.getDataByNameFromPROJECT(project_name);

        //Check Valid Project
        if (!project_valid_check()) {
            return false;
        }

        dir_path = project_meta.dir;

        galleryAdapterModel = GalleryAdapterModel.getInstance(this, dir_path);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_preview2);

        Image_View = (FrameLayout) findViewById(R.id.Image_view);

        Intent intent = getIntent();
        final String project_name = intent.getStringExtra("PROJECT_NAME");
        Log.d("ALARM", intent.getExtras().containsKey("PROJECT_NAME") + " ");

        //Target 24>= inflict
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        context = this;
        if (!projectInit(project_name)) {
            finish();
            return;
        }
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
                //timingDialog(false);
                ProjectSaveController.projectData = project_meta;
                ProjectSaveController.galleryAdapterModel = galleryAdapterModel;
                Intent intent = new Intent(context, ProjectSaveController.class);
                startActivityForResult(intent, StaticInformation.SAVE_ONLY);
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
                if (galleryAdapterModel.getCount() >= 2)
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
                PreviewDialog.galleryAdapterModel = galleryAdapterModel;
                PreviewDialog.projectData = project_meta;
                previewDialog.previewOn(activity);
            }
        });
        deleteBtn = findViewById(R.id.project_delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryAdapterModel.getCount() <= 0) return;

                File file;
                if (galleryAdapterModel.getCount() > 1) {
                    file = new File(dir_path + "/" + galleryAdapterModel.getImageFileNames()[seekBar.getProgress()]);
                } else {
                    file = new File(dir_path + "/" + galleryAdapterModel.getImageFileNames()[0]);
                }
                boolean deleted = file.delete();
                if (deleted) {
                    Toast.makeText(context, "성공적으로 삭제하였습니다", Toast.LENGTH_SHORT).show();
                    project_meta.is_modify = StaticInformation.TRUE;
                    myDB.syncProjectData(project_meta);
                } else {
                    Toast.makeText(context, "DELETE ERROR", Toast.LENGTH_SHORT).show();
                }
                updateGallaryImage();
                String[] date = TimeClass.getDate();
                project_meta.description = DescriptionManager.getRemoveDescription(date[0], date[1], date[2], galleryAdapterModel.getCount());

                if (galleryAdapterModel.getCount() == 0) {
                    project_meta.wide = StaticInformation.DISPLAY_ORIENTATION_DEFAULT;

                    ImageView imgView = findViewById(R.id.project_imageView);
                    imgView.setImageDrawable(getResources().getDrawable(R.drawable.default_preview));
                }
                myDB.syncProjectData(project_meta);
            }
        });

        fetchBtn = findViewById(R.id.project_fetch_btn);
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, StaticInformation.GALLERY_CODE);
            }
        });

        project_name_tv = findViewById(R.id.project_current_name);
        project_name_tv.setText(project_meta.name);

        projectPreviewModel = new ProjectPreviewModel(this, this);
    }

    private boolean project_valid_check() {
        if (project_meta == null) {
            return false;
        }

        if (myDB.getDataByIdFromPROJECT(Integer.toString(project_meta.id)) == null) {
            return false;
        }
        return true;
    }

    public void previewDraw(int progress) {
        final ImageView imgView = findViewById(R.id.project_imageView);
        if (galleryAdapterModel.getCount() < 0) return;
        if (galleryAdapterModel.getCount() == 0) {
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.default_preview));
            return;
        }

        String imageName = galleryAdapterModel.getImageFileNames()[progress];

        int width = imgView.getWidth();
        int height = imgView.getHeight();

        //Optimization ..
        try {
            Log.d(TAG, "image View size : " + width + " " + height);
            Glide.with(context).load(dir_path + "/" + imageName).override(width, height).into(imgView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.default_error));
        }
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

    public void updateGallaryImage() {
        galleryAdapterModel = GalleryAdapterModel.getInstance(this, dir_path);
        galleryAdapterModel.UpdateGallery();
        if (galleryAdapterModel.getCount() > 0) {
            if (galleryAdapterModel.getCount() == 1) {
                previewDraw(0);
            } else {
                seekBar.setMax(galleryAdapterModel.getCount() - 1);
                seekBar.setProgress(galleryAdapterModel.getCount() - 1);
            }
        } else {
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

            if (!projectInit(project_meta.id)) {
                finish();
                return;
            }

            Log.d("onWindowFocusChanged", "GALALY UPDATE : " + galleryAdapterModel.getCount());
            updateGallaryImage();
            Log.d("DEBUG_TEST", project_meta.name + "  ");
            project_name_tv.setText(project_meta.name);
        }
        // do Something
        if (isFirstCreate == false) {
            isFirstCreate = true;
            sh_pref = getSharedPreferences(StaticInformation.TUTORIAL_PROJECT, MODE_PRIVATE);
            if(sh_pref.getInt(StaticInformation.TUTORIAL_PROJECT_PREVIEW, 0)==0){
                sh_edit = sh_pref.edit();
                sh_edit.putInt(StaticInformation.TUTORIAL_PROJECT_PREVIEW, 1);
                sh_edit.commit();

                ProjectPreviewTuto ppt = new ProjectPreviewTuto(this);
                ppt.tutorial_start();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case StaticInformation.GALLERY_CODE:
                    String imagePath = fileIOModel.getRealPathFromURI(data.getData());

                    File srcFile = new File(imagePath);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String currentDateandTime = sdf.format(new Date());
                    String fileName = project_meta.dir + "/CHALNA_" + currentDateandTime + ".jpg";
                    File dstFile = new File(fileName);

                    try {
                        FileManagementUtil.copyDirectory(srcFile, dstFile);

                        int imageRotation = CameraModel.getFileRotation(imagePath);
                        if (galleryAdapterModel.getCount()  <= 0) {
                            if(imageRotation == 0 || imageRotation==270){
                                project_meta.wide = FileManagementUtil.getBitmapImgHeight(imagePath) > FileManagementUtil.getBitmapImgWidth(imagePath) ?
                                        StaticInformation.DISPLAY_ORIENTATION_PORTRAIT :
                                        StaticInformation.DISPLAY_ORIENTATION_LEFT;
                            }else if ( imageRotation == 90){
                                project_meta.wide = FileManagementUtil.getBitmapImgHeight(imagePath) > FileManagementUtil.getBitmapImgWidth(imagePath) ?
                                        StaticInformation.DISPLAY_ORIENTATION_LEFT :
                                        StaticInformation.DISPLAY_ORIENTATION_PORTRAIT;
                            }else{
                                project_meta.wide = FileManagementUtil.getBitmapImgHeight(imagePath) > FileManagementUtil.getBitmapImgWidth(imagePath) ?
                                        StaticInformation.DISPLAY_ORIENTATION_RIGHT :
                                        StaticInformation.DISPLAY_ORIENTATION_PORTRAIT;
                            }
                        }
                        updateGallaryImage();
                        String[] date = TimeClass.getDate();
                        project_meta.description = DescriptionManager.getAddDescription(date[0], date[1], date[2], galleryAdapterModel.getCount());

                        myDB.syncProjectData(project_meta);
                        Toast.makeText(context, "새로운 찰나가 추가되었어요!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "이미지 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StaticInformation.SAVE_AND_SHARE:
                    final String filePath = project_meta.dir + "/" + project_meta.name + "_result.gif";
                    File file = new File(filePath);
                    if (file.exists()) {
                        projectPreviewModel.share(project_meta, filePath);
                    }
                    break;
                case StaticInformation.SAVE_AND_COMPLETE:
                    final String filePath2 = project_meta.dir + "/" + project_meta.name + "_result.gif";
                    File file2 = new File(filePath2);
                    if (file2.exists()) {
                        projectPreviewModel.complete(project_meta);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_button_share) {
            final String filePath = project_meta.dir + "/" + project_meta.name + "_result.gif";
            File file = new File(filePath);
            if (file.exists()) {
                if(project_meta.is_modify==1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("공유하기")
                            .setMessage("결과가 수정되었습니다. 새로 저장하시겠습니까?")
                            .setPositiveButton("새로 저장", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int value) {
                                    ProjectSaveController.projectData = project_meta;
                                    ProjectSaveController.galleryAdapterModel = galleryAdapterModel;
                                    Intent intent = new Intent(context, ProjectSaveController.class);
                                    startActivityForResult(intent, StaticInformation.SAVE_AND_SHARE);
                                }
                            })
                            .setNegativeButton("이전결과", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    projectPreviewModel.share(project_meta, filePath);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    projectPreviewModel.share(project_meta, filePath);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("공유하기")
                        .setMessage("공유를 위해 파일을 우선 파일을 저장해야 합니다.")
                        .setPositiveButton("저장 후 공유", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int value) {
                                ProjectSaveController.projectData = project_meta;
                                ProjectSaveController.galleryAdapterModel = galleryAdapterModel;
                                Intent intent = new Intent(context, ProjectSaveController.class);
                                startActivityForResult(intent, StaticInformation.SAVE_AND_SHARE);
                            }
                        })
                        .setNegativeButton("취소", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else if (id == R.id.action_button_delete) {
            projectPreviewModel.delete(project_meta);
        } else if (id == R.id.action_button_modify) {
            projectPreviewModel.modify(project_meta);
        } else if (id == R.id.action_button_complete) {
            final String filePath = project_meta.dir + "/" + project_meta.name + "_result.gif";
            File file = new File(filePath);
            if (file.exists()) {
                if(project_meta.is_modify==1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("완료하기")
                            .setMessage("결과가 수정되었습니다. 새로 저장하시겠습니까?")
                            .setPositiveButton("새로 저장", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int value) {
                                    ProjectSaveController.projectData = project_meta;
                                    ProjectSaveController.galleryAdapterModel = galleryAdapterModel;
                                    Intent intent = new Intent(context, ProjectSaveController.class);
                                    startActivityForResult(intent, StaticInformation.SAVE_AND_COMPLETE);
                                }
                            })
                            .setNegativeButton("이전결과로 완성", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    projectPreviewModel.complete(project_meta);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    projectPreviewModel.complete(project_meta);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("완료하기")
                        .setMessage("완료를 위해 파일을 저장해야 합니다.")
                        .setPositiveButton("저장 후 완료", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int value) {
                                ProjectSaveController.projectData = project_meta;
                                ProjectSaveController.galleryAdapterModel = galleryAdapterModel;
                                Intent intent = new Intent(context, ProjectSaveController.class);
                                startActivityForResult(intent, StaticInformation.SAVE_AND_COMPLETE);
                            }
                        })
                        .setNegativeButton("취소", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
