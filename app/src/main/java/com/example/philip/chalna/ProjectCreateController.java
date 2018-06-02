package com.example.philip.chalna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;

public class ProjectCreateController extends AppCompatActivity {

    DBSQLiteModel myDB;

    // UI

    Button ok;
    RadioGroup setting_wide;
    RadioGroup setting_mode;
    EditText setting_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_create);

        myDB = DBSQLiteModel.getInstance(this);
        ok = findViewById(R.id.create_ok);
        setting_mode = findViewById(R.id.create_mode);
        setting_wide = findViewById(R.id.create_wide);
        setting_name = findViewById(R.id.create_name);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = setting_name.getText().toString();
                RadioButton wideButton = findViewById(setting_wide.getCheckedRadioButtonId());
                int wide;
                if(wideButton.getText().toString().equals("vertical")){
                    wide = 0;
                }else{
                    wide = 1;
                }

                RadioButton modeButton = findViewById(setting_mode.getCheckedRadioButtonId());
                int mode;
                if(modeButton.getText().toString().equals("rear")){
                    mode = 0;
                }else{
                    mode = 1;
                }

                Log.d("DEBUG_TEST", StaticInformation.CHALNA_PATH+"/"+name  + " Folder Create");
                File file = new File(StaticInformation.CHALNA_PATH+"/"+name);
                if ( !file.exists() ){
                    if(file.mkdir()){
                        Log.d("DEBUG_TEST", StaticInformation.CHALNA_PATH+"/"+name  + " Success");
                    }
                }
                myDB.dbInsertionIntoPROJECT(name,wide,mode,StaticInformation.CHALNA_PATH+"/"+name,0);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
