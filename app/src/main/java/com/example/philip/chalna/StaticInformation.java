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


}
