package com.studioOgg.philip.chalna;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.studioOgg.philip.chalna.Camera.CameraController;
import com.studioOgg.philip.chalna.Project.ProjectCreateController;
import com.studioOgg.philip.chalna.Project.ProjectSelectController;
import com.studioOgg.philip.chalna.Utils.DescriptionManager;
import com.studioOgg.philip.chalna.Utils.FileManagementUtil;
import com.studioOgg.philip.chalna.Utils.ImageProcessingIO;
import com.studioOgg.philip.chalna.Utils.StaticInformation;
import com.studioOgg.philip.chalna.Utils.TimeClass;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BitmapTestClass {
    @Test
    public void StaticInfoCheck_Check() {
        File file = new File("C:\\Users\\LEaps\\Documents\\TestFolder\\1.txt");
        File file2 = new File("C:\\Users\\LEaps\\Documents\\TestFolder\\2.txt");
        try {
            FileManagementUtil.copyDirectory(file,file2);
            FileManagementUtil.deleteDirectory("C:\\Users\\LEaps\\Documents\\TestFolder\\1.txt");
            FileManagementUtil.existFile("C:\\Users\\LEaps\\Documents\\TestFolder\\2.txt");
            FileManagementUtil.fileNameChange(file,file2);
            FileManagementUtil.getBitmapImgHeight("C:\\Users\\LEaps\\Documents\\TestFolder\\2.txt");
            FileManagementUtil.getBitmapImgWidth("C:\\Users\\LEaps\\Documents\\TestFolder\\2.txt");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }catch (NullPointerException e){

        }
    }
}
