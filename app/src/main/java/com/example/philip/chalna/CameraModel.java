package com.example.philip.chalna;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class CameraModel {
    public Bitmap guidedImage = null;

    //Option
    int guidedMode = 1;

    // OPEN_CV
    public native void sobel_filter(long matAddrInput, long matAddrResult);

    public void setGuidedImage(Mat img){
        // convert to bitmap:
        Bitmap bm = Bitmap.createBitmap(img.cols(), img.rows(),Bitmap.Config.ARGB_8888);

        switch (guidedMode){
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
    public void setGuidedImage(Bitmap img){
        switch (guidedMode){
            case StaticInformation.GUIDED_SOBELFILTER:
                img = img.copy(Bitmap.Config.ARGB_8888,true);
                Mat inputImage = new Mat();
                Utils.bitmapToMat(img, inputImage);
                Mat matResult = new Mat(inputImage.rows(), inputImage.cols(), inputImage.type());
                sobel_filter(inputImage.getNativeObjAddr(), matResult.getNativeObjAddr());

                Bitmap bm = Bitmap.createBitmap(inputImage.cols(), inputImage.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matResult, bm);

                guidedImage = bm;
                break;
            case StaticInformation.GUIDED_TRANSPARENCY:
                guidedImage = img;
                break;
        }
    }
}
