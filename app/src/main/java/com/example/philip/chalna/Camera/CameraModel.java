package com.example.philip.chalna.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.philip.chalna.Database.DBSQLiteModel;
import com.example.philip.chalna.Utils.StaticInformation;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

public class CameraModel {
    Context context;
    public Bitmap guidedImage = null;


    //Option
    int guidedMode = StaticInformation.GUIDED_SOBELFILTER;


    // OPEN_CV
    public native void sobel_filter(long matAddrInput, long matAddrResult);

    public void setGuidedMode(int i) {
        guidedMode = i;
    }

    public int getGuidedMode() {
        return guidedMode;
    }

    public void setGuidedImage(Mat img) {
        // convert to bitmap:
        Bitmap bm = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);

        switch (guidedMode) {
            case StaticInformation.GUIDED_SOBELFILTER:
                Mat matResult = new Mat(img.rows(), img.cols(), img.type());
                sobel_filter(img.getNativeObjAddr(), matResult.getNativeObjAddr());
                Utils.matToBitmap(matResult, bm);
                break;
            case StaticInformation.GUIDED_TRANSPARENCY:
                Utils.matToBitmap(img, bm);
                break;
        }
        guidedImage = bm;
    }

    public Bitmap imageExifRotation(Bitmap img, final String filePath) {
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            );

            int exifDegree = exifOrientationToDegrees(exifOrientation);

            Log.e("[imageExifRotation", "current Phone = " + exifOrientation + " change Dgree = " + exifDegree);
            img = rotate(img, exifDegree);
        } catch (IOException e) {
            Toast.makeText(context, "File Path Error! Sorry..", Toast.LENGTH_SHORT);
            return img;
        }
        return img;
    }

    public Bitmap rotateLandScape(Bitmap img, int display_orientation, int camera_mode) {
        if (display_orientation == StaticInformation.CAMERA_ORIENTATION_LEFT) {
            return img;
        } else if (display_orientation == StaticInformation.CAMERA_ORIENTATION_PORTRAIT) {
            return rotate(img, -90);
        } else {
            return rotate(img, -180);
        }
    }

    public static int getFileRotation(String imagePath){
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            );
            int exifDegree = exifOrientationToDegrees(exifOrientation);

            return exifDegree;
        } catch (IOException e) {
        }
        return 0;

    }
    public void setGuidedImage(Bitmap img, int display_orientation, int camera_mode) {
        img = rotateLandScape(img, display_orientation, camera_mode);
        switch (guidedMode) {
            case StaticInformation.GUIDED_SOBELFILTER:
                img = img.copy(Bitmap.Config.ARGB_8888, true);
                Mat inputImage = new Mat();
                Utils.bitmapToMat(img, inputImage);
                Mat matResult = new Mat(inputImage.rows(), inputImage.cols(), inputImage.type());
                sobel_filter(inputImage.getNativeObjAddr(), matResult.getNativeObjAddr());

                Bitmap bm = Bitmap.createBitmap(inputImage.cols(), inputImage.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matResult, bm);

                guidedImage = bm;
                break;
            case StaticInformation.GUIDED_TRANSPARENCY:
                guidedImage = img;
                break;
        }
    }

    public static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Log.d("Bitmap : ", bitmap + " " + bitmap.isRecycled());
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
//                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return bitmap;
    }
}
