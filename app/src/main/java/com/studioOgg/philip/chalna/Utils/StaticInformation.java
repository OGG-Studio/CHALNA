package com.studioOgg.philip.chalna.Utils;

import android.os.Environment;

public class StaticInformation {
    public static final int GALLERY_CODE = 1001;

    // NORMAL
    public static final int TRUE = 1;
    public static final int FALSE = 0;
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

    public static final String ILLEGAL_EXP = "[:\\\\/%*?:|\"<>]";

    public static final int PROJECT_PALYING = 0;
    public static final int PROJECT_COMPLETE = 1;

    public static final int SAVE_ONLY = 111;
    public static final int SAVE_AND_SHARE = 112;
    public static final int SAVE_AND_COMPLETE = 113;

    public static final String GLOBAL_SETTING = "global_setting";
    public static final String GLOBAL_SETTING_NOTI = "global_notification";

    public static final String TUTORIAL_PROJECT = "tuto_project";
    public static final String TUTORIAL_PROJECT_SELECTE = "tuto_project_selec";
    public static final String TUTORIAL_PROJECT_CAMERA = "tuto_project_cam";
    public static final String TUTORIAL_PROJECT_PREVIEW = "tuto_project_prev";
}
