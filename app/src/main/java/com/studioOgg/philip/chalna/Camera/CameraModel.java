package com.studioOgg.philip.chalna.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.studioOgg.philip.chalna.Database.DBSQLiteModel;
import com.studioOgg.philip.chalna.Utils.StaticInformation;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

public class CameraModel {
    Context context;
    public Bitmap guidedImage = null;
    //Option
    int guidedMode = StaticInformation.GUIDED_SOBELFILTER;
    // OPEN_CV

    /**
     * Opencv C++ NDK Sobel Filter Function
     * @param matAddrInput long type Mat input, ( Src )
     * @param matAddrResult long type Mat input ( Det )
     * @return void
     */
    public native void sobel_filter(long matAddrInput, long matAddrResult);

    /**
     * setter 'guidedMode parameter'
     * Guided mode -> 0 GUIDED_SOBELFILTER
     * Guided mode -> 1 GUIDED_TRANSPARENCY
     * @param i guided mode.
     */
    public void setGuidedMode(int i) {
        guidedMode = i;
    }

    /**
     * get Guided Mode
     * Guided mode -> 0 GUIDED_SOBELFILTER
     * Guided mode -> 1 GUIDED_TRANSPARENCY
     * @return guided mode
     */
    public int getGuidedMode() {
        return guidedMode;
    }

    /**
     * setting Guided Image for Guided Processing
     * @param img
     */
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

    /**
     * get EXif Roatation value for picture rotation information and rotation that picture.
     * @param img
     * @return Bitmap result
     */
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

    /**
     * rotate bitmap for landscape mode
     * @param img bitmap image
     * @param display_orientation orientation information
     * @param camera_mode current camera mode
     * @return new rotated bitmap
     */
    public Bitmap rotateLandScape(Bitmap img, int display_orientation, int camera_mode) {
        if (display_orientation == StaticInformation.CAMERA_ORIENTATION_LEFT) {
            return img;
        } else if (display_orientation == StaticInformation.CAMERA_ORIENTATION_PORTRAIT) {
            return rotate(img, -90);
        } else {
            return rotate(img, -180);
        }
    }

    /**
     * get file rotation information using image path
     * @param imagePath image absolute path
     * @return rotation information
     */
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

    /**
     * setting guided image
     * @param img bitmap image
     * @param display_orientation orientation information
     * @param camera_mode current camera mode
     */
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

    /**
     * The actual angle of rotation is obtained via exif information.
     * @param exifOrientation
     * @return the actual angle of rotation.
     */
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

    /**
     * Rotate Bitmap for degrees
     * @param bitmap src image
     * @param degrees the anggle of degree
     * @return dst rotated image
     */
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
