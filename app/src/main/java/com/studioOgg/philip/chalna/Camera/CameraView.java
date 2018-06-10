package com.studioOgg.philip.chalna.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.studioOgg.philip.chalna.Utils.ImageProcessingIO;
import com.studioOgg.philip.chalna.Utils.StaticInformation;

import org.opencv.android.JavaCameraView;

import java.io.FileOutputStream;
import java.util.List;

public class CameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "myCameraView";
    private String mPictureFileName = null;
    private int camera_mode = StaticInformation.CAMERA_REAR;
    public CameraController cameraController;
    Context context;

    //FOR ZOOM
    int zoom_factor;
    int max_camera_zoom;
    double base_distance=0;
    int base_zoom=0;

    boolean double_touch_start;

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
        int pointer_count = motionEvent.getPointerCount();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_POINTER_DOWN:
                if(camera_mode==StaticInformation.CAMERA_FRONT){
                    Toast.makeText(context, "전면 카메라에서는 지원하지 않습니다.", Toast.LENGTH_SHORT);
                }else{
                    double_touch_start = true;
                    base_distance = getDistanceTwoHand(motionEvent);
                    base_zoom = zoom_factor;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                double_touch_start = false;
                // UPDATE
                cameraController.currentProject.zoom_factor = zoom_factor;
                cameraController.myDB.syncProjectData(cameraController.currentProject);
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointer_count==2 && double_touch_start == true){
                    double alpha = 50*(getDistanceTwoHand(motionEvent)-base_distance)/base_distance;
                    zoom_factor = base_zoom + (int)alpha;
                    zoom_factor = clip(zoom_factor);
                }
                setZoomFactor(zoom_factor);
                break;
        }
        return true;
    }

    public double getDistanceTwoHand(MotionEvent event){
        return Math.sqrt(Math.pow(event.getX(0)-event.getX(1),2)+Math.pow(event.getY(0)-event.getY(1),2));
    }
    public int clip(int zoom){
        if(zoom<=0){
            return 0;
        }else if(zoom < max_camera_zoom){
            return zoom;
        }else{
            return max_camera_zoom;
        }
    }
    public void setCameraZoomSetting(){
        Camera.Parameters params = mCamera.getParameters();
        zoom_factor = cameraController.currentProject.zoom_factor;

        Log.d(TAG, "MAX ZOOM : "+params.getMaxZoom());
        max_camera_zoom = params.getMaxZoom();
        setZoomFactor(zoom_factor);
    }
    public void setZoomFactor(int alpha){
        Camera.Parameters params = mCamera.getParameters();
        Log.d(TAG, "MAX ZOOM : "+params.getMaxZoom());

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
        Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
            }
        };

        // PictureCallback is implemented by the current class
        mCamera.takePicture(myShutterCallback, null, this);
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

                if(cameraController.currentProject.wide == StaticInformation.DISPLAY_ORIENTATION_DEFAULT){
                    if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_PORTRAIT){
                        m.postRotate(-90);
                    }else if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_RIGHT){
                        m.postRotate(-180);
                    }
                }else{
                    if(cameraController.currentProject.wide==StaticInformation.CAMERA_ORIENTATION_PORTRAIT){
                        m.postRotate(-90);
                    }else if(cameraController.currentProject.wide==StaticInformation.CAMERA_ORIENTATION_RIGHT){
                        m.postRotate(-180);
                    }
                }
                m.postScale(-1, 1);

                Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
                data = ImageProcessingIO.bitmapToByteArray(rotateBitmap);
            }else if(camera_mode==StaticInformation.CAMERA_REAR){
                if(cameraController.currentProject.wide==StaticInformation.DISPLAY_ORIENTATION_DEFAULT){
                    if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_PORTRAIT){
                        m.postRotate(90);
                    }else if(cameraController.currentOrientation==StaticInformation.CAMERA_ORIENTATION_RIGHT){
                        m.postRotate(180);
                    }
                }else{
                    if(cameraController.currentProject.wide==StaticInformation.CAMERA_ORIENTATION_PORTRAIT){
                        m.postRotate(90);
                    }else if(cameraController.currentProject.wide==StaticInformation.CAMERA_ORIENTATION_RIGHT){
                        m.postRotate(180);
                    }
                }
                Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
                data = ImageProcessingIO.bitmapToByteArray(rotateBitmap);
//                if (bmp != null && !bmp.isRecycled()) {
//                    bmp.recycle();
//                }
            }
            fos.write(data);
            fos.close();
            cameraController.setGuidedImageToView(mPictureFileName);
        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }
}
