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

public class DescriptionManagerTest {
    @Test
    public void Description_Test() {
        System.out.println(DescriptionManager.getAddDescription("2018","3","3",20));
        System.out.println(DescriptionManager.getAlarmDescription());
        System.out.println(DescriptionManager.getCompleteDescription(TimeClass.getCurrentTime(), TimeClass.getCurrentTime()));
        System.out.println(DescriptionManager.getModifyDescription());
        System.out.println(DescriptionManager.getNewDescription());
        System.out.println(DescriptionManager.getRemoveDescription("2","2","2",3));
        System.out.println(DescriptionManager.getSaveDescription());
    }
}
