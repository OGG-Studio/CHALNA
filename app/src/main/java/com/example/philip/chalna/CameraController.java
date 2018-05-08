package com.example.philip.chalna;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraController extends AppCompatActivity
        implements CvCameraViewListener2 {
    private static final String TAG = "CameraController";

    private CameraView mOpenCvCameraView;

    //opencv matrix
    private Mat matInput;
    private Mat matResult;

    // Model
    CameraModel cameraModel;
    FileManagementUtil fileIOModel;

    // Button
    Button btnImageLoad;
    Button changeViewBtn;

    // UI
    ImageView guidedImageView;
    SeekBar seekBar;

    // C++
    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    // Library Load
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Start Camera Model");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        btnImageLoad = findViewById(R.id.LoadImageBtn);
        btnImageLoad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, StaticInformation.GALLERY_CODE);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Permision check
            if (!hasPermissions(PERMISSIONS)) {
                //Request
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        mOpenCvCameraView = findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setFocusable(true);

        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        //Model Initialization
        cameraModel = new CameraModel();
        fileIOModel = new FileManagementUtil(this);

        //UI Access
        guidedImageView = findViewById(R.id.guidedImageViewer);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setProgress((int)(guidedImageView.getAlpha()*100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                guidedImageView.setAlpha((float)progress/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            };

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            };
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
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

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

//        if ( matResult != null ) matResult.release();
//        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
//
//        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

        return inputFrame.rgba();
    }


    //Permission method
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {"android.permission.CAMERA" , "android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //Permision Check
        for (String perms : permissions){
            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
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

        Log.d(TAG, grantResults.length +" "+ grantResults[0]+" "+grantResults[1]+" "+grantResults[2]);
        Log.d(TAG, permissions[0] + " " + permissions[1] + " " + permissions[2]);
        switch(requestCode){
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

        AlertDialog.Builder builder = new AlertDialog.Builder( CameraController.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
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
        Log.d(TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/DCIM/Test/sample_picture_" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }
    
    // Intent Result Event
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case StaticInformation.GALLERY_CODE:
                    Bitmap bm = fileIOModel.getGuidedImageFromRealPath(data.getData()); //갤러리에서 가져오기
                    cameraModel.setGuidedImage(bm);
                    guidedImageView.setImageBitmap(cameraModel.guidedImage);
                    break;
                default:
                    break;
            }
        }
    }
}