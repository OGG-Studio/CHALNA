package com.example.philip.chalna;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

public class FileManagementUtil {
    private static final String TAG = "FILE_MANAGER_MODEL";
    Context context = null;

    FileManagementUtil(Context c){
        context = c;
    }
    public String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }
    // Direction Processing when do same taking picture
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    private Bitmap rotate(Bitmap src, float degree) {
        // Java graphics Matrix
        Matrix matrix = new Matrix();
        // Rotation Degree Setting
        matrix.postRotate(degree);

        // Return Bitmap
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
    public Bitmap getGuidedImageFromRealPath(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // image rotation degree
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Log.d(TAG,"Degree Check : " + exifDegree);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);// image read using path.
//        return rotate(bitmap, exifDegree);
        return bitmap;
    }
    public Bitmap getGuidedImageFromRealPath(String imagePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // image rotation degree
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Log.d(TAG,"Degree Check : " + exifDegree);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);// image read using path.
//        return rotate(bitmap, exifDegree);
        return bitmap;
    }
}
