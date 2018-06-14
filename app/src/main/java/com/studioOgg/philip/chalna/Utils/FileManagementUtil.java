package com.studioOgg.philip.chalna.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileManagementUtil {
    private static final String TAG = "FILE_MANAGER_MODEL";
    Context context = null;

    /**
     * get width of bitmap image using absolute path
     * @param fileName
     * @return image width
     */
    public static int getBitmapImgWidth(String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        return options.outWidth;
    }
    /**
     * get height of bitmap image using absolute path
     * @param fileName
     * @return image height
     */
    public static int getBitmapImgHeight(String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        return options.outHeight;
    }

    /**
     * isFile
     * @param fileName
     * @return
     */
    public static boolean existFile(String fileName) {
        File f = new File(fileName);
        return f.exists();
    }

    /**
     * delete directory and all child file
     * @param path
     */
    public static void deleteDirectory(String path)
    {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        for(File childFile : childFileList)
        {
            if(childFile.isDirectory()) {
                deleteDirectory(childFile.getAbsolutePath());     //하위 디렉토리 루프
            }
            else {
                childFile.delete();    //하위 파일삭제
            }
        }
        file.delete();    //root 삭제
    }

    /**
     * change file name src -> dst
     * @param source
     * @param dst
     * @return success/fail
     */
    public static boolean fileNameChange(File source, File dst){
        if(source.renameTo(dst)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * If targetLocation does not exist, it will be created.
     *
     */
    public static void copyDirectory(File sourceLocation , File targetLocation)
            throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
    public FileManagementUtil(Context c){
        context = c;
    }

    /**
     * transfer URI to Real Path
     * @param contentUri
     * @return Absolute Path
     */
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

    /**
     * get exif Orientation dgree
     * @param exifOrientation
     * @return
     */
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
}
