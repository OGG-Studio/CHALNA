package com.example.philip.chalna;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class GalleryAdapterModel extends BaseAdapter {
    private static final String TAG = "GalleryAdapterModel";
    private static GalleryAdapterModel instance;
    private Context context;

    private String imagePath;
    private String[] imageFileNames;

    private int delay;
    private int sizeWidth;

    public static GalleryAdapterModel getInstance(Context c, String imagePath){
        if(instance==null) instance = new GalleryAdapterModel(c, imagePath);
        return instance;
    }
    public boolean saveGIF(){
        FileOutputStream outStream = null;
        try{
            outStream = new FileOutputStream(imagePath+"/result.gif");
            outStream.write(generateGIF());
            outStream.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    static public Bitmap resizeBitmap(Bitmap original, int resizeWidth) {
        double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }
    public byte[] generateGIF() {
        // Transform this function to thread-based function to later
        // Array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(delay);  // 500/ms
        encoder.setRepeat(0);   // 0 reapte
        encoder.start(bos);
        for(String imageFileName : imageFileNames){
            File imgFile = new File(imagePath+"/"+imageFileName);
            Bitmap myBitmap = resizeBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()), sizeWidth);
            encoder.addFrame(myBitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

    public String[] getImageFileNames(){
        return imageFileNames;
    }
    public void UpdateGallery(){
        File imagePathAsFile = new File(imagePath);
        imageFileNames = imagePathAsFile.list(new ImageFileFilter());
    }
    public GalleryAdapterModel(Context context, String imagePath) {
        this.context = context;
        this.imagePath = imagePath;

        File imagePathAsFile = new File(imagePath);
        imageFileNames = imagePathAsFile.list(new ImageFileFilter());

        this.sizeWidth = 512;
        this.delay = 100;
    }

    @Override
    public int getCount() {
        return imageFileNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private class ImageFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String filename) {
            if (filename.endsWith(".jpg"))
                return true;
            else if (filename.endsWith(".JPG"))
                return true;
            else if (filename.endsWith(".png"))
                return true;
            else if (filename.endsWith(".PNG"))
                return true;
            else
                return false;
        }
    }
}