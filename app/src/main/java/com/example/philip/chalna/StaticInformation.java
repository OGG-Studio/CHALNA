package com.example.philip.chalna;

import android.os.Environment;

public class StaticInformation {
    public static final int GALLERY_CODE = 1001;

    // GUIDED MODE
    public static final int GUIDED_TRANSPARENCY = 0;
    public static final int GUIDED_SOBELFILTER = 1;
    public static final String DCIM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath().toString();
    public static final String CHALNA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath().toString() +"/CHALNA";

    //REQUEST
    public static final int REQUEST_CREATE_CONTROL = 1;

    //Caemra
    public static final int CAMERA_ORIENTATION_RIGHT = 1;
    public static final int CAMERA_ORIENTATION_LEFT = 2;
    public static final int CAMERA_ORIENTATION_PORTRAIT = 3;

    public static final int CAMERA_REAR = 0;
    public static final int CAMERA_FRONT = 1;

    public static final int DISPLAY_ORIENTATION_DEFAULT = 0;
    public static final int DISPLAY_ORIENTATION_RIGHT= 1;
    public static final int DISPLAY_ORIENTATION_LEFT = 2;
    public static final int DISPLAY_ORIENTATION_PORTRAIT = 3;
}
