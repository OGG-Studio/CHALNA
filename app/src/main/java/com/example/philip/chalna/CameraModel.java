package com.example.philip.chalna;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.nfc.Tag;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

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

    public Bitmap rotateLandScape(Bitmap img, int display_mode){
        if(display_mode==StaticInformation.CAMERA_ORIENTATION_LEFT){
            return img;
        }else if(display_mode==StaticInformation.CAMERA_ORIENTATION_PORTARATE){
            return rotate(img,-90);
        }else{
            return rotate(img,-180);
        }

    }
    public void setGuidedImage(Bitmap img, int display_mode) {
        img = rotateLandScape(img, display_mode);
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

    public int exifOrientationToDegrees(int exifOrientation) {
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
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return bitmap;
    }
}
