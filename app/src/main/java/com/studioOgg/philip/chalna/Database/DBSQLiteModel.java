package com.studioOgg.philip.chalna.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

// Reference : http://kuroikuma.tistory.com/75

public class DBSQLiteModel extends SQLiteOpenHelper {
    private static final String TAG = "DB_HELPER" ;

    private static final String DB_FILE_NAME = "projects.db" ;
    private static final int DB_VERSION = 8;

    public static DBSQLiteModel instance = null;
    public static SQLiteDatabase db;

    public static final String PROJECT ="PROJECT";
    public static final String ALARM = "ALARM";

    //PROJECT
    public static final String PROJECT_ID = "Project_id";
    public static final String PROJECT_NAME = "Project_Name";
    public static final String PROJECT_WIDE = "Project_Wide";
    public static final String PROJECT_MODE = "Project_Mode";
    public static final String PROJECT_DIR = "Project_dir";
    public static final String PROJECT_LEVEL = "Project_Level";
    public static final String PROJECT_ZOOM_FACTOR = "Project_Zoom_Factor";

    public static final String PROJECT_DESCRIPTION = "Project_Description";
    public static final String PROJECT_MODIFICATION_DATE = "Project_ModDate";
    public static final String PROJECT_START_DATE = "Project_StartDate";


    public static final String PROJECT_IS_MODIFIED  = "Project_Modify";
    public static final String PROJECT_GUIDED_FILTER_MODE = "Project_Guided_Mode";

    //ADD FEATURE

    //ALRAM
    public static final String ALARM_ID = "Alarm_id";
    public static final String ALARM_TIME = "Alarm_Time";
    public static final String ALARM_CYCLE = "Alarm_Cycle";
    public static final String ALARM_NEXT_TIME = "Alarm_Next_Time";

    // 시작날짜 종료날짜 시간 요일
    //

    public DBSQLiteModel(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public static DBSQLiteModel getInstance(Context context) {
        if(instance==null) {
            instance = new DBSQLiteModel(context, DB_FILE_NAME, null, DB_VERSION);
            db = instance.getWritableDatabase();
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Project Name ok
        // Project wide = display orientation
        // Project Mode = camera rear or front
        // Project_dir = dicrectory
        // Project_level = step ( 0 -> create 1 -> Complete )
        String sql = String.format("create table "+ PROJECT +" ("+PROJECT_ID+" integer primary key autoincrement, Project_Name text, Project_Wide integer, Project_Mode integer, Project_dir text, Project_Level integer, "+PROJECT_ZOOM_FACTOR+" integer, "
                +PROJECT_DESCRIPTION+" text, "+PROJECT_MODIFICATION_DATE+" integer, "+PROJECT_START_DATE+" integer, "+
                PROJECT_IS_MODIFIED +" integer, "+ PROJECT_GUIDED_FILTER_MODE +" integer);");
        db.execSQL(sql);

        // alarm
        // alarm_id -> id
        // Project_id -> project id
        // alarm Project id
        // alarm_TIME
        // alarm_CYCLE
        // alarm_NEXT_TIME
        sql = String.format("create table "+ ALARM +" ("+ALARM_ID+" integer primary key autoincrement, "+PROJECT_ID+" integer, "+ALARM_TIME+" integer, "+ALARM_CYCLE+" integer, "+ALARM_NEXT_TIME+" integer);");
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists PROJECT";
        Log.d(TAG, "UPDATE DB");
        db.execSQL(sql);
        sql = "drop table if exists "+ ALARM;
        db.execSQL(sql);

        onCreate(db);
    }
    public ProjectData getDataByNameFromPROJECT(String project_name){
        String sql = "select * from PROJECT where Project_Name = '" + project_name+"';";
        Cursor c = db.rawQuery(sql, null);

        ProjectData result = null;
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(c.moveToFirst()){
            int _id = c.getInt(c.getColumnIndex(PROJECT_ID));
            String name = c.getString(c.getColumnIndex(PROJECT_NAME));
            int camera_wide = c.getInt(c.getColumnIndex(PROJECT_WIDE));
            int camera_mode = c.getInt(c.getColumnIndex(PROJECT_MODE));
            String dir = c.getString(c.getColumnIndex(PROJECT_DIR));
            int level = c.getInt(c.getColumnIndex(PROJECT_LEVEL));
            int zoom = c.getInt(c.getColumnIndex(PROJECT_ZOOM_FACTOR));

            String description = c.getString(c.getColumnIndex(PROJECT_DESCRIPTION));
            long modificationDate = c.getLong(c.getColumnIndex(PROJECT_MODIFICATION_DATE));
            long startDate = c.getLong(c.getColumnIndex(PROJECT_START_DATE));

            int is_modify = c.getInt(c.getColumnIndex(PROJECT_IS_MODIFIED));
            int guided_mode = c.getInt(c.getColumnIndex(PROJECT_GUIDED_FILTER_MODE));

            result = new ProjectData(_id, name,camera_mode, camera_wide, dir, level, zoom,description,modificationDate,startDate, is_modify, guided_mode);
        }else{
            Log.d(TAG,"Search Faile Error");
        }
        return result;
    }
    public ProjectData getDataByIdFromPROJECT(String project_id){
        String sql = "select * from PROJECT where "+PROJECT_ID+" = '" + project_id+"';";
        Cursor c = db.rawQuery(sql, null);

        ProjectData result = null;
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(c.moveToFirst()){
            int _id = c.getInt(c.getColumnIndex(PROJECT_ID));
            String name = c.getString(c.getColumnIndex(PROJECT_NAME));
            int camera_wide = c.getInt(c.getColumnIndex(PROJECT_WIDE));
            int camera_mode = c.getInt(c.getColumnIndex(PROJECT_MODE));
            String dir = c.getString(c.getColumnIndex(PROJECT_DIR));
            int level = c.getInt(c.getColumnIndex(PROJECT_LEVEL));
            int zoom = c.getInt(c.getColumnIndex(PROJECT_ZOOM_FACTOR));

            String description = c.getString(c.getColumnIndex(PROJECT_DESCRIPTION));
            long modificationDate = c.getLong(c.getColumnIndex(PROJECT_MODIFICATION_DATE));
            long startDate = c.getLong(c.getColumnIndex(PROJECT_START_DATE));

            int is_modify = c.getInt(c.getColumnIndex(PROJECT_IS_MODIFIED));
            int guided_mode = c.getInt(c.getColumnIndex(PROJECT_GUIDED_FILTER_MODE));

            result = new ProjectData(_id, name,camera_mode, camera_wide, dir, level, zoom,description,modificationDate,startDate, is_modify, guided_mode);
        }else{
            Log.d(TAG,"Search Faile Error");
        }
        return result;
    }
    public long syncProjectData(ProjectData p){
        ContentValues updateValues = new ContentValues();
        updateValues.put(PROJECT_MODE, p.mode);
        updateValues.put(PROJECT_NAME, p.name);
        updateValues.put(PROJECT_LEVEL, p.project_level);
        updateValues.put(PROJECT_DIR, p.dir);
        updateValues.put(PROJECT_WIDE, p.wide);
        updateValues.put(PROJECT_ZOOM_FACTOR, p.zoom_factor);

        updateValues.put(PROJECT_DESCRIPTION, p.description);
        updateValues.put(PROJECT_MODIFICATION_DATE, p.modificationDate);
        updateValues.put(PROJECT_START_DATE, p.startDate);

        updateValues.put(PROJECT_IS_MODIFIED, p.is_modify);
        updateValues.put(PROJECT_GUIDED_FILTER_MODE, p.guided_mode);

        return db.update(PROJECT, updateValues, PROJECT_ID + "=?", new String[]{String.valueOf(p.id)});
    }
    public long syncAlarmData(AlarmData p){
        ContentValues updateValues = new ContentValues();
        updateValues.put(ALARM_CYCLE, p.alarm_cycle);
        updateValues.put(ALARM_NEXT_TIME, p.alram_next_time);
        updateValues.put(ALARM_TIME, p.alarm_time);
        updateValues.put(PROJECT_ID, p.project_id);

        return db.update(ALARM, updateValues, ALARM_ID + "=?", new String[]{String.valueOf(p.id)});
    }
    public AlarmData getDataByNameFromALARM(String project_id){
        String sql = "select * from "+ALARM+" where "+PROJECT_ID+" = '" + project_id+"';";
        Cursor c = db.rawQuery(sql, null);

        AlarmData result = null;
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(c.moveToFirst()){
            int alarm_id = c.getInt(c.getColumnIndex(ALARM_ID));
            int new_project_id = c.getInt(c.getColumnIndex(PROJECT_ID));
            long alarm_time = c.getLong(c.getColumnIndex(ALARM_TIME));
            int alarm_cycle = c.getInt(c.getColumnIndex(ALARM_CYCLE));
            long alarm_next_time = c.getLong(c.getColumnIndex(ALARM_NEXT_TIME));

            result = new AlarmData(alarm_id,new_project_id, alarm_time, alarm_cycle, alarm_next_time);
        }else{
            Log.d(TAG,"Search Faile Error");
        }
        return result;
    }
    public void initializationTable(){
        String sql = "drop table if exists PROJECT";
        db.execSQL(sql);
    }
    public void dbInsertionIntoPROJECT(ProjectData p) {
        ContentValues values = new ContentValues();
        values.put(PROJECT_NAME, p.name);
        values.put(PROJECT_WIDE, p.wide);
        values.put(PROJECT_MODE, p.mode);
        values.put(PROJECT_DIR, p.dir);
        values.put(PROJECT_LEVEL, p.project_level);
        values.put(PROJECT_ZOOM_FACTOR, p.zoom_factor);
        values.put(PROJECT_DESCRIPTION, p.description);
        values.put(PROJECT_START_DATE, p.startDate);
        values.put(PROJECT_MODIFICATION_DATE, p.modificationDate);

        values.put(PROJECT_IS_MODIFIED, p.is_modify);
        values.put(PROJECT_GUIDED_FILTER_MODE, p.guided_mode);

        db.insert(PROJECT, null, values);
    }
    public void dbInsertionIntoALARM(int project_id, long alarm_time, int alarm_cycle, long alarm_next_time) {
        ContentValues values = new ContentValues();
        values.put(PROJECT_ID, project_id);
        values.put(ALARM_TIME, alarm_time);
        values.put(ALARM_CYCLE, alarm_cycle);
        values.put(ALARM_NEXT_TIME, alarm_next_time);
        db.insert(ALARM, null, values);
    }
    public void dbDeleteFromPROJECT(String[] name){
        db.delete(PROJECT,"Project_Name=?", name);
    }
    public void dbDeleteFromALARM(String[] id){
        db.delete(ALARM,PROJECT_ID+"=?", id);
    }
    public void projectSanityCheck(){
        ArrayList<ProjectData> projectList = selectAllFromPROJECT();
        //Sanity Check
        for(ProjectData p : projectList){
            File pDir = new File(p.dir);
            if(!pDir.isDirectory()){
                Log.d(TAG, "DEBUG_TEST : PAth: " + p.dir + " is Deleted");
                dbDeleteFromPROJECT(new String[] {p.name});
                dbDeleteFromALARM(new String[] {Integer.toString(p.id)});
            }
        }
    }
    public ArrayList<AlarmData> selectAllFromALARM(){
        ArrayList<AlarmData> list = new ArrayList<>();
        Cursor c = db.query(ALARM, null, null, null,null,null,null);

        while(c.moveToNext()){
            int alarm_id = c.getInt(c.getColumnIndex(ALARM_ID));
            int new_project_id = c.getInt(c.getColumnIndex(PROJECT_ID));
            long alarm_time = c.getLong(c.getColumnIndex(ALARM_TIME));
            int alarm_cycle = c.getInt(c.getColumnIndex(ALARM_CYCLE));
            long alarm_next_time = c.getLong(c.getColumnIndex(ALARM_NEXT_TIME));

            list.add(new AlarmData(alarm_id,new_project_id, alarm_time, alarm_cycle, alarm_next_time));
        }

        return list;
    }
    public ArrayList<ProjectData> selectAllFromPROJECT(){
        ArrayList<ProjectData> list = new ArrayList<>();
        Cursor c = db.query(PROJECT, null, null, null,null,null,null);

        while(c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("Project_id"));
            String name = c.getString(c.getColumnIndex("Project_Name"));
            int camera_wide = c.getInt(c.getColumnIndex("Project_Wide"));
            int camera_mode = c.getInt(c.getColumnIndex("Project_Mode"));
            String dir = c.getString(c.getColumnIndex("Project_dir"));
            int level = c.getInt(c.getColumnIndex("Project_Level"));
            int zoom = c.getInt(c.getColumnIndex(PROJECT_ZOOM_FACTOR));

            String description = c.getString(c.getColumnIndex(PROJECT_DESCRIPTION));
            long modificationDate = c.getLong(c.getColumnIndex(PROJECT_MODIFICATION_DATE));
            long startDate = c.getLong(c.getColumnIndex(PROJECT_START_DATE));

            int is_modify = c.getInt(c.getColumnIndex(PROJECT_IS_MODIFIED));
            int guided_mode = c.getInt(c.getColumnIndex(PROJECT_GUIDED_FILTER_MODE));

            list.add(new ProjectData(_id, name,camera_mode, camera_wide, dir, level, zoom,description,modificationDate,startDate, is_modify, guided_mode));
        }
        return list;
    }
    public void debug_db(){
        Cursor c = db.query("PROJECT", null, null, null,null,null,null);

        Log.d("DEBUG_TEST_DB", "DEBUG_START " + c.getCount());

        while(c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("Project_id"));
            String name = c.getString(c.getColumnIndex("Project_Name"));
            int camera_wide = c.getInt(c.getColumnIndex("Project_Wide"));
            int camera_mode = c.getInt(c.getColumnIndex("Project_Mode"));
            String dir = c.getString(c.getColumnIndex("Project_dir"));
            int level = c.getInt(c.getColumnIndex("Project_Level"));

            Log.d("DEBUG_TEST_DB", _id + " mode:" +camera_mode + " wide" +camera_wide + " " +name + " " +level + " ");
        }
    }
}
