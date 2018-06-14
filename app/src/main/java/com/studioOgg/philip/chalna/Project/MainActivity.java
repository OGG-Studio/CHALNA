package com.studioOgg.philip.chalna.Project;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.studioOgg.philip.chalna.R;
import com.studioOgg.philip.chalna.Utils.StaticInformation;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";
    Handler handler = new Handler(Looper.getMainLooper());

    //Test
    private void folderInit() {
        Log.d(TAG, "Folder initialization");
        String chalnaDirectory = StaticInformation.DCIM_PATH + "/CHALNA";
        File CHALNA_FORDER = new File(chalnaDirectory);
        CHALNA_FORDER.mkdirs();
        Log.d(TAG, "Folder initialization Complete");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        new Thread() {
            @Override
            public void run() {
                Log.d("TESTAT", Build.VERSION.SDK_INT+" " + Build.VERSION_CODES.M);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!hasPermissions(PERMISSIONS)) {
                        //Request
                        requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    } else {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //mkdir
                        folderInit();

                        Intent intent = new Intent(getApplicationContext(), ProjectSelectController.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Log.d("TESTAT", "HELLO WORLD");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final int[] grantResults = new int[PERMISSIONS.length];

                            PackageManager packageManager = getPackageManager();
                            String packageName = getPackageName();

                            final int permissionCount = PERMISSIONS.length;
                            for (int i = 0; i < permissionCount; i++) {
                                grantResults[i] = packageManager.checkPermission(
                                        PERMISSIONS[i], packageName);
                            }

                            onRequestPermissionsResult(
                                    PERMISSIONS_REQUEST_CODE, PERMISSIONS, grantResults);
                        }
                    });

                }
            }
        }.start();
    }

    //Permission method
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.RECEIVE_BOOT_COMPLETED", "android.permission.INTERNET"};

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
                    boolean cameraPermissionAccepted =
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                                    grantResults[4] == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                    else {
                        folderInit();
                        Intent intent = new Intent(this, ProjectSelectController.class);
                        startActivity(intent);
                        finish();
                    }

                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
}