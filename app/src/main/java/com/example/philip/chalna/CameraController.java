package com.example.philip.chalna;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CameraController extends AppCompatActivity
        implements CvCameraViewListener2 {
    private static final String TAG = "CameraController";

    Activity activity = this;
    Context context = this;
    private CameraView mOpenCvCameraView;

    // Opencv matrix
    private Mat matInput;
    private Mat matResult;

    // Model
    CameraModel cameraModel;
    FileManagementUtil fileIOModel;

    // Button
    Button btnImageLoad;
    Button changeViewBtn;
    Button changeGuidedModeBtn;

    // UI
    ImageView guidedImageView;
    SeekBar seekBar;

    // C++
//    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    //Data
    DBSQLiteModel myDB;
    ProjectData currentPorject;

    //Camera Listener
    public OrientationEventListener mOrientationEventListener;
    public int currentOrientation= 0;

    // Library Load
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    // Constant
    private String path_dir;
    private GalleryAdapterModel galleryAdapterModel;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Start Camera Model");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        //Model Initialization
        cameraModel = new CameraModel();
        fileIOModel = new FileManagementUtil(this);

        //UI GET
        guidedImageView = findViewById(R.id.guidedImageViewer);
        guidedImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mOpenCvCameraView.onTouchEvent(event);
            }
        });

        btnImageLoad = findViewById(R.id.LoadImageBtn);
        btnImageLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, StaticInformation.GALLERY_CODE);
            }
        });

        changeGuidedModeBtn = findViewById(R.id.changeGuidedModeBtn);
        changeGuidedModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraModel.getGuidedMode()==StaticInformation.GUIDED_SOBELFILTER)
                    cameraModel.setGuidedMode(StaticInformation.GUIDED_TRANSPARENCY);
                else
                    cameraModel.setGuidedMode(StaticInformation.GUIDED_SOBELFILTER);

                Log.e("TEST_DEBUG", ""+cameraModel.getGuidedMode());
                final String img_path = mOpenCvCameraView.getmPictureFileName();
                if(img_path!=null){
                    setGuidedImageToView(img_path);
                }

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Permision check
            if (!hasPermissions(PERMISSIONS)) {
                //Request
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        //INFORMATION
        Intent intent = getIntent();
        path_dir = intent.getStringExtra("DIR");
        String project_name = intent.getStringExtra("PROJECT_NAME");
        galleryAdapterModel = GalleryAdapterModel.getInstance(this, path_dir);

        myDB = DBSQLiteModel.getInstance(this);
        currentPorject = myDB.getDataByName(project_name);

        // WIDE MODE
        if(currentPorject.wide==0){
            //Vertical
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            //Horizontal
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mOpenCvCameraView = findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setFocusable(true);

        if(currentPorject.mode==0){
            mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        }
        else{
            mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
        }

        //OPTION SETTING CAMERA
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        mOpenCvCameraView.setCameraMode(currentPorject.mode);
        mOpenCvCameraView.cameraController = this;

        //UI Access
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setProgress((int) (guidedImageView.getAlpha() * 100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                guidedImageView.setAlpha((float) progress / 100);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        changeViewBtn = findViewById(R.id.changeViewBtn);
        changeViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "myCheck Camera Change");
                mOpenCvCameraView.disableView();
                mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        });

        // Camera Callback initialization
        setupOrientationEventListener();
    }

    boolean firstCreate = false;
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        // do Something
        if(hasFocus && !firstCreate){
            Camera.Size mCameraSize = mOpenCvCameraView.getResolution();

            mOpenCvCameraView.setPictureSize(mCameraSize.width, mCameraSize.height);

            // Auto Guided
            galleryAdapterModel.UpdateGallery();
            String[] imageNames = galleryAdapterModel.getImageFileNames();
            int lastIndex = imageNames.length;

            if (imageNames != null && lastIndex > 0) {
                String fileName = path_dir + "/" + galleryAdapterModel.getImageFileNames()[lastIndex - 1];
                mOpenCvCameraView.setmPictureFileName(fileName);
                setGuidedImageToView(fileName);
            }

            Log.d("DEBUG_TEST","GUIDED_SIZE = " +guidedImageView.getWidth() + " " + guidedImageView.getHeight());
            Log.d("DEBUG_TEST","CameraView = " +mOpenCvCameraView.getWidth() + " " + mOpenCvCameraView.getHeight());
            Log.d("DEBUG_TEST","Res = " +mOpenCvCameraView.getResolution().width + " " + mOpenCvCameraView.getResolution().height);

            firstCreate = true;
        }
    }
    public void setGuidedImageToView(String fileName){
        Log.d(TAG, "View Size " + guidedImageView.getWidth() + " " + guidedImageView.getHeight());
        Glide.with(context).load(fileName).asBitmap().override(guidedImageView.getWidth(), guidedImageView.getHeight()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Thread readyThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "image Size " + resource.getWidth() + " " + resource.getHeight());
                        cameraModel.setGuidedImage(resource);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                guidedImageView.setImageBitmap(cameraModel.guidedImage);
                            }
                        });
                    }
                });
                readyThread.start();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrientationEventListener.disable();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

        mOrientationEventListener.enable();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

        if(currentPorject.mode==1){
            Core.flip(matInput, matInput, 1);
        }
        return matInput;
    }

    //Permission method
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //Permision Check
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //Denide?
                return false;
            }
        }
        //all permision ok
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, grantResults.length + " " + grantResults[0] + " " + grantResults[1] + " " + grantResults[2]);
        Log.d(TAG, permissions[0] + " " + permissions[1] + " " + permissions[2]);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CameraController.this);

        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    //Event
    public boolean OnTakePicture(View v) {
        Log.d(TAG, "onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = path_dir + "/" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(fileName); //새로고침할 사진경로
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        return false;
    }

    // Intent Result Event
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case StaticInformation.GALLERY_CODE:
//                    Bitmap bm = fileIOModel.getGuidedImageFromRealPath(data.getData()); //Deprecated
//                    cameraModel.setGuidedImage(bm);
//                    guidedImageView.setImageBitmap(cameraModel.guidedImage);
                    String imagePath = fileIOModel.getRealPathFromURI(data.getData());
                    mOpenCvCameraView.setmPictureFileName(imagePath);
                    setGuidedImageToView(imagePath);
                    break;
                default:
                    break;
            }
        }
    }

    /***
     *          CALL BACK
     */

    public void setupOrientationEventListener() {
        mOrientationEventListener = new OrientationEventListener(this){
            @Override
            public void onOrientationChanged(int orientation) {
                Log.d("Orientation_Test", "Current " + orientation);
                if(activity.isFinishing()){
                    mOrientationEventListener.disable();
                    return;
                }

                int newOrientation;
                if(orientation >=75 && orientation < 134){
                    newOrientation = StaticInformation.CAMERA_ORIENTATION_RIGHT;
                }else if(orientation >= 225 && orientation <289){
                    newOrientation = StaticInformation.CAMERA_ORIENTATION_LEFT;
                }else{
                    newOrientation = StaticInformation.CAMERA_ORIENTATION_PORTARATE;
                }

                if(newOrientation!=currentOrientation){
                    int degree = 0;

                    if(newOrientation==StaticInformation.CAMERA_ORIENTATION_RIGHT){
                        degree = -180;
                    }else if(newOrientation==StaticInformation.CAMERA_ORIENTATION_LEFT){
                        degree = 0;
                    }else{
                        degree = -90;
                    }

                    animateViews(degree);
                    currentOrientation = newOrientation;
                }
            }
        };
        mOrientationEventListener.enable();
    }

    private  void animateViews(int degrees) {
        List<View> views = new ArrayList<View>(){
            {
                add(btnImageLoad);
                add(changeGuidedModeBtn);
                add(changeViewBtn);
            }
        };
        for (View view : views) {
            view.animate().rotation(Float.valueOf(degrees)).start();
        }
    }
}