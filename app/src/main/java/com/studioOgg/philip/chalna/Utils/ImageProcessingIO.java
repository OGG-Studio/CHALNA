package com.studioOgg.philip.chalna.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageProcessingIO {
    /**
     * transfer bitmap to byte array
     * @param $bitmap
     * @return
     */
    public static byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    /**
     * transfer byte array to bitmap
     * @param $byteArray
     * @return
     */
    public static Bitmap byteArrayToBitmap( byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }
}
