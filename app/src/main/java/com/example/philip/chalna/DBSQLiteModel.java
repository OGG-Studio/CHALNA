package com.example.philip.chalna;

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
    private static final int DB_VERSION = 1;

    public static DBSQLiteModel instance = null;
    public static SQLiteDatabase db;

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
        String sql = String.format("create table PROJECT (Project_id integer primary key autoincrement, Project_Name text, Project_Wide integer, Project_Mode integer, Project_dir text, Project_Level integer);");
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists PROJECT";
        Log.d(TAG, "UPDATE DB");
        db.execSQL(sql);
    }
    public ProjectData getDataByName(String project_name){

        String sql = "select * from PROJECT where Project_Name = '"+project_name+"';";
        debug_db();
        Cursor c = db.rawQuery(sql, null);

        ProjectData result = null;
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(c.moveToFirst()){
            int _id = c.getInt(c.getColumnIndex("Project_id"));
            String name = c.getString(c.getColumnIndex("Project_Name"));
            int camera_wide = c.getInt(c.getColumnIndex("Project_Wide"));
            int camera_mode = c.getInt(c.getColumnIndex("Project_Mode"));
            String dir = c.getString(c.getColumnIndex("Project_dir"));
            int level = c.getInt(c.getColumnIndex("Project_Level"));
            result = new ProjectData(_id, name,camera_mode, camera_wide, dir, level);
        }else{
            Log.d(TAG,"Search Faile Error");
        }
        return result;
    }
    public void initializationTable(){
        String sql = "drop table if exists PROJECT";
        db.execSQL(sql);
    }
    public void dbInsert(String Project_Name, int Project_Wide, int Project_Camera_Mode, String Project_dir, int Project_Level){

        ContentValues values = new ContentValues();
        values.put("Project_Name", Project_Name);
        values.put("Project_Wide", Project_Wide);
        values.put("Project_Mode", Project_Camera_Mode);
        values.put("Project_dir", Project_dir);
        values.put("Project_Level", Project_Level);
        db.insert("PROJECT", null,values);

    }
    public void dbDelete(String[] name){
        db.delete("PROJECT","Project_Name=?", name);
    }
    public void projectSanityCheck(){
        ArrayList<ProjectData> projectList = selectAll();
        //Sanity Check
        for(ProjectData p : projectList){
            File pDir = new File(p.dir);
            if(!pDir.isDirectory()){
                Log.d(TAG, "DEBUG_TEST : PAth: " + p.dir + " is Deleted");
                dbDelete(new String[] {p.name});
            }
        }
    }
    public ArrayList<ProjectData> selectAll(){
        ArrayList<ProjectData> list = new ArrayList<>();
        Cursor c = db.query("PROJECT", null, null, null,null,null,null);

        while(c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("Project_id"));
            String name = c.getString(c.getColumnIndex("Project_Name"));
            int camera_wide = c.getInt(c.getColumnIndex("Project_Wide"));
            int camera_mode = c.getInt(c.getColumnIndex("Project_Mode"));
            String dir = c.getString(c.getColumnIndex("Project_dir"));
            int level = c.getInt(c.getColumnIndex("Project_Level"));

            list.add(new ProjectData(_id, name,camera_mode, camera_wide, dir, level));
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
