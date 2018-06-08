package com.example.philip.chalna.Project;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.philip.chalna.Database.DBSQLiteModel;
import com.example.philip.chalna.Database.ProjectData;
import com.example.philip.chalna.R;
import com.example.philip.chalna.Utils.FileManagementUtil;
import com.example.philip.chalna.Utils.GalleryAdapterModel;
import com.example.philip.chalna.Utils.LoadingClass;

import java.io.File;

public class ProjectCompleteController extends AppCompatActivity {
    Context context = this;
    private DBSQLiteModel myDB;
    private ProjectData project_meta;

    String dir_path;
    GalleryAdapterModel galleryAdapterModel;
    private LoadingClass loading;
    private TextView project_name_tv;

    String fileImageResultPath;
    private ImageView imageView;
    private ProjectPreviewModel projectPreviewModel;

    private boolean project_valid_check() {
        if(project_meta==null){
            return false;
        }

        if(myDB.getDataByIdFromPROJECT(Integer.toString(project_meta.id))==null){
            return false;
        }
        return true;
    }
    public boolean projectInit(String project_name){
        myDB = DBSQLiteModel.getInstance(context);

        project_meta = myDB.getDataByNameFromPROJECT(project_name);

        //Check Valid Project
        if(!project_valid_check()){
            Log.e("DEBUG_TEST","PROJECT VALID CHECL FAIL");
            return false;
        }

        dir_path = project_meta.dir;

        galleryAdapterModel = GalleryAdapterModel.getInstance(this, dir_path);
        fileImageResultPath = project_meta.dir + "/"+ project_name +"_result.gif";
        if(!FileManagementUtil.existFile(fileImageResultPath)){
            Log.e("DEBUG_TEST","PROJECT VALID CHECL FAIL");
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_preview_complete);

        Intent intent = getIntent();
        final String project_name = intent.getStringExtra("PROJECT_NAME");
        Log.d("ALARM",intent.getExtras().containsKey("PROJECT_NAME")+" ");

        //Target 24>= inflict
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        loading = new LoadingClass();

        if(!projectInit(project_name)){
            Toast.makeText(context, "파일에 오류가 있습니다...!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        project_name_tv = findViewById(R.id.complete_project_name);
        project_name_tv.setText(project_meta.name);

        imageView = findViewById(R.id.complete_project_imageview);

        Glide.with(context)
                .load(fileImageResultPath)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);

        projectPreviewModel = new ProjectPreviewModel(this,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.action_button_share){
            final String filePath = project_meta.dir+"/"+project_meta.name+"_result.gif";
            File file = new File(filePath);
            if(file.exists()){
                projectPreviewModel.share(project_meta, filePath);
            }
        }
        else if(id==R.id.complete_delete){
            projectPreviewModel.delete(project_meta);

        }
        else if(id==R.id.complete_callback){
            projectPreviewModel.cancleComplete(project_meta);
        }
        return super.onOptionsItemSelected(item);
    }
}
