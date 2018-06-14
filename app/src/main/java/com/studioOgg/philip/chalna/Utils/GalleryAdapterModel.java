package com.studioOgg.philip.chalna.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

public class GalleryAdapterModel extends BaseAdapter {
    private static final String TAG = "GalleryAdapterModel";
    private static GalleryAdapterModel instance;
    private Context context;

    private String imagePath;
    private String[] imageFileNames;

    private int delay;
    private int sizeWidth;
    private int sizeHeight;

    private static int progress = 0;

    /**
     * settong gallery adapter model parameter
     * @param delay delay
     * @param sizeWidth image width
     * @param sizeHeight image height
     */
    public void setGIFSetting(int delay, int sizeWidth, int sizeHeight){
        this.delay = delay;
        this.sizeWidth = sizeWidth;
        this.sizeHeight = sizeHeight;
    }
    public static GalleryAdapterModel getInstance(Context c, String imagePath){
        if(instance==null) instance = new GalleryAdapterModel(c, imagePath);
        instance.imagePath = imagePath;
        return instance;
    }

    /**
     * save GIF
     * @return success/fail
     */
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

    /**
     * save gif and show that progress
     * @param tv progress textview
     * @param text text view
     * @param save_file_name save -> file name
     * @return success / fail
     */
    public boolean saveGIF(TextView tv, String text, String save_file_name){
        FileOutputStream outStream = null;
        try{
            outStream = new FileOutputStream(imagePath+"/"+save_file_name+".gif");
            outStream.write(generateGIF(tv, text));
            outStream.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * resize bitmap
     * @param original
     * @param resizeWidth
     * @return
     */
    static public Bitmap resizeBitmap(Bitmap original, int resizeWidth) {
        double aspectRatio = 0;
        Bitmap result;
        if(original.getHeight() > original.getWidth()){
            aspectRatio = (double) original.getHeight() / (double) original.getWidth();
            int targetHeight = (int) (resizeWidth * aspectRatio);
            result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        }else{
            aspectRatio = (double) original.getWidth() / (double) original.getHeight();
            int targetWidth = (int) (resizeWidth * aspectRatio);
            result = Bitmap.createScaledBitmap(original, targetWidth, resizeWidth, false);
        }
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    /**
     * generated GIF. this function transfer image list to gif
     * @return byte array gif information
     */
    public byte[] generateGIF() {
        // Transform this function to thread-based function to later
        // Array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(delay);  // 100/ms
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

    /**
     *     /**
     * generated GIF. this function transfer image list to gif
     * @return byte array gif information
     */
    public byte[] generateGIF(final TextView textView, final String forwardString) {
        // Transform this function to thread-based function to later
        // Array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(delay);  // 100/ms
        encoder.setRepeat(0);   // 0 reapte
        encoder.start(bos);
        progress = 0;

        final int imageNum = imageFileNames.length;
        for(String imageFileName : imageFileNames){
            File imgFile = new File(imagePath+"/"+imageFileName);
            Bitmap myBitmap = resizeBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            encoder.addFrame(myBitmap);
            progress++;
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(
                            forwardString +" (" + progress+"/" + imageNum +")"
                    );
                }
            });
        }
        encoder.finish();
        return bos.toByteArray();
    }

    /**
     * bitmap resize
     * @param original src
     * @return dst
     */
    private Bitmap resizeBitmap(Bitmap original) {
        return Bitmap.createScaledBitmap(original, sizeWidth, sizeHeight, false);
    }

    /**
     * get image file names in gallery adeapter
     * @return
     */
    public String[] getImageFileNames(){
        return imageFileNames;
    }

    /**
     * syncronize current gallery adapter with folder directory structure
     * @return
     */
    public boolean UpdateGallery(){
        int changedCheck = 0;
        File imagePathAsFile = new File(imagePath);
        if(imageFileNames!=null){
            changedCheck = imageFileNames.length;
        }
        imageFileNames = imagePathAsFile.list(new ImageFileFilter());
        if(imageFileNames.length!=changedCheck){
            return true;
        }
        return false;
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

    /**
     * image filter filter, jpg, png, PNG, JPG and chalna
     */
    private class ImageFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String filename) {
            if(!filename.startsWith("CHALNA")){
                return false;
            }
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