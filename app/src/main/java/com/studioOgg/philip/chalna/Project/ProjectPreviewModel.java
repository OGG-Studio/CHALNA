package com.studioOgg.philip.chalna.Project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.studioOgg.philip.chalna.Database.DBSQLiteModel;
import com.studioOgg.philip.chalna.Database.ProjectData;
import com.studioOgg.philip.chalna.Utils.DescriptionManager;
import com.studioOgg.philip.chalna.Utils.FileManagementUtil;
import com.studioOgg.philip.chalna.Utils.StaticInformation;
import com.studioOgg.philip.chalna.Utils.TimeClass;

import java.io.File;

public class ProjectPreviewModel {
    Context context;
    Activity activity;

    public ProjectPreviewModel(Context c, Activity a){
        context = c;
        activity = a;
    }
    /**
     * delete
     * @param project_meta
     */
    public void delete(final ProjectData project_meta){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
        alert_confirm.setMessage("당신의 찰나를 지울까요?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FileManagementUtil.deleteDirectory(project_meta.dir);
                            Thread.sleep(200);
                            Toast.makeText(context, "굿바이!", Toast.LENGTH_LONG).show();
                            Thread.sleep(500);
                            DBSQLiteModel myDB = DBSQLiteModel.getInstance(context);
                            myDB.dbDeleteFromALARM(new String[]{String.valueOf(project_meta.id)});
                            myDB.dbDeleteFromPROJECT(new String[]{project_meta.name});
                            activity.finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }
    /**
     * modify prosessing
     * @param project_meta
     */
    public void modify(ProjectData project_meta){
        Intent intent = new Intent(context, ProjectCreateController.class);
        intent.putExtra("MODIFY_MODE", true);
        intent.putExtra(DBSQLiteModel.PROJECT_ID, project_meta.id);
        activity.startActivity(intent);
    }
    /**
     * share information
     * @param project_meta
     */
    public void share(ProjectData project_meta, String image_path){
        File file = new File(image_path);
        Uri mSaveImageUri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/gif");
        intent.putExtra(Intent.EXTRA_STREAM, mSaveImageUri);
        activity.startActivity(Intent.createChooser(intent, "Choose"));
    }
    /**
     * complete prosessing
     * @param project_meta
     */
    public void complete(ProjectData project_meta){
        DBSQLiteModel myDB = DBSQLiteModel.getInstance(context);
        project_meta.project_level = StaticInformation.PROJECT_COMPLETE;
        project_meta.description = DescriptionManager.getCompleteDescription(project_meta.startDate, TimeClass.getCurrentTime());
        project_meta.modificationDate = TimeClass.getCurrentTime();

        myDB.syncProjectData(project_meta);
        myDB.dbDeleteFromALARM(new String[]{String.valueOf(project_meta.id)});
        Toast.makeText(context, "찰나가 완성되었습니다!", Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    /**
     * cancle Complete function
     * @param project_meta
     */
    public void cancleComplete(ProjectData project_meta){
        DBSQLiteModel myDB = DBSQLiteModel.getInstance(context);
        project_meta.project_level = StaticInformation.PROJECT_PALYING;
        project_meta.description = DescriptionManager.getNewDescription();
        project_meta.modificationDate = TimeClass.getCurrentTime();

        myDB.syncProjectData(project_meta);
        Toast.makeText(context, "다시, 새로운 찰나", Toast.LENGTH_SHORT).show();
        activity.finish();
    }
}
