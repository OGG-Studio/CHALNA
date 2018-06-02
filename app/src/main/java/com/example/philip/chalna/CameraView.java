package com.example.philip.chalna;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import org.opencv.android.JavaCameraView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;

public class CameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "myCameraView";
    private String mPictureFileName = null;
    private int camera_mode = StaticInformation.CAMERA_REAR;
    public CameraController cameraController;
    Context context;

    int zoom_factor;

    public void setmPictureFileName(String mPictureFileName) {
        this.mPictureFileName = mPictureFileName;
    }
    public String getmPictureFileName(){
        return mPictureFileName;
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setPictureSize(int w, int h){
        Camera.Parameters params = mCamera.getParameters();
        params.setPictureSize(w, h);
        mCamera.setParameters(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
//        mCamera.autoFocus (new Camera.AutoFocusCallback() {
//            public void onAutoFocus(boolean success, Camera camera) {
//
//            }
//        });
        return true;
    }

    public void setZoomFactor(int alpha){
        Camera.Parameters params = mCamera.getParameters();
        params.setZoom(alpha);
        mCamera.setParameters(params);
    }
    public void setCameraMode(int mode){
        camera_mode = mode;
    }
    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }
    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);
        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix m = new Matrix();
//                m.postRotate(0);

            if(camera_mode==StaticInformation.CAMERA_FRONT){
                Log.i(TAG, "Rotation CAMERA");
                if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_PORTARATE){
                    m.postRotate(-90);
                }else if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_RIGHT){
                    m.postRotate(-180);
                }
                m.postScale(-1, 1);

                Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
                bmp.recycle();

                data = ImageProcessingIO.bitmapToByteArray(rotateBitmap);
            }else if(camera_mode==StaticInformation.CAMERA_REAR && cameraController.currentOrientation!=StaticInformation.CAMERA_ORIENTATION_LEFT){
                if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_PORTARATE){
                    m.postRotate(90);
                }else if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_RIGHT){
                    m.postRotate(180);
                }
                Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
                bmp.recycle();
                data = ImageProcessingIO.bitmapToByteArray(rotateBitmap);
            }
            fos.write(data);
            fos.close();

            cameraController.setGuidedImageToView(mPictureFileName);
        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }
}
