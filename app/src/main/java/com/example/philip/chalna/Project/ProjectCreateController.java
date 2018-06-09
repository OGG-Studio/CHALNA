package com.example.philip.chalna.Project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.philip.chalna.AlarmReceiver.AlarmBroadcastController;
import com.example.philip.chalna.Database.AlarmData;
import com.example.philip.chalna.Database.DBSQLiteModel;
import com.example.philip.chalna.Database.ProjectData;
import com.example.philip.chalna.Utils.DescriptionManager;
import com.example.philip.chalna.Utils.FileManagementUtil;
import com.example.philip.chalna.R;
import com.example.philip.chalna.Utils.StaticInformation;
import com.example.philip.chalna.Utils.TimeClass;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class ProjectCreateController extends AppCompatActivity implements TimePicker.OnTimeChangedListener{

    String[] period_item = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14"};

    Calendar cal;
    TimePicker tp;

    ImageButton registerBtn;

    int rgs_period;
    int rgs_hour;
    int rgs_min;

    EditText project_name;
    TextView resultView;

    DBSQLiteModel myDB;

    ToggleButton toggleButton;

    ProjectData _projectData;
    AlarmData _alarmData;

    boolean modifyMode = false;
    public void modifyModeComplete(){
        resultView.setText("당신의 '찰나'를 위해  "+rgs_period+ "일마다, "+rgs_hour+"시 "+rgs_min+"분에  알려드릴게요.");

        String name = project_name.getText().toString();
        if(!checkName(name) ){
            Toast.makeText(getApplicationContext(), "유효하지 않은 이름입니다!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(myDB.getDataByNameFromPROJECT(name)!=null && _projectData.dir.compareTo(StaticInformation.CHALNA_PATH+"/"+name)!=0){
            Toast.makeText(getApplicationContext(), "중복되는 이름이에요!",Toast.LENGTH_SHORT).show();
            return;
        }

        final String newDir = StaticInformation.CHALNA_PATH+"/"+name;
        File file = new File(newDir);
        if ( !file.exists() ){
            if(file.mkdir()){
                Log.d("DEBUG_TEST", StaticInformation.CHALNA_PATH+"/"+name  + " Success");
            }
        }
        if(_projectData.dir.compareTo(newDir)!=0){
            final String preResultPath = newDir +"/"+_projectData.name+"_result.gif";
            final String newResultPath = newDir + "/" + name +"_result.gif";


            File preFile = new File(_projectData.dir);

            File preResultFile = new File(preResultPath);
            File newResultFile = new File(newResultPath);

            try {
                FileManagementUtil.copyDirectory(preFile, file);
                // result
                if(FileManagementUtil.existFile(preResultPath)){
                    FileManagementUtil.fileNameChange(preResultFile, newResultFile);
                }
                FileManagementUtil.deleteDirectory(_projectData.dir);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        _projectData.dir = newDir;
        _projectData.name = name;
        _projectData.description = DescriptionManager.getModifyDescription();
        Date date = new Date();
        _projectData.modificationDate = date.getTime();

        if(toggleButton.isChecked()){
            Intent intent = new Intent(ProjectCreateController.this, AlarmBroadcastController.class);
            intent.putExtra(DBSQLiteModel.PROJECT_ID, _projectData.id);

            // Calendar의 알람주기를 설정
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            String[] date_info = TimeClass.timeDateToNumeric(calendar.getTime());
            calendar.set(Integer.parseInt(date_info[0]), Integer.parseInt(date_info[1])-1, Integer.parseInt(date_info[2]), rgs_hour, rgs_min, 1);

            AlarmData ad = myDB.getDataByNameFromALARM(Integer.toString(_projectData.id));
            if(ad!=null){
                myDB.dbDeleteFromALARM(new String[]{Integer.toString(_projectData.id)});
            }
            myDB.dbInsertionIntoALARM(_projectData.id, calendar.getTimeInMillis(), rgs_period, calendar.getTimeInMillis());

            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), _projectData.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            //아래 알람 울리는거 repeat으로 바꿔야해!
            if(TimeClass.getCurrentTime() < calendar.getTimeInMillis())
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), rgs_period*TimeClass.oneDay, pi);
            else
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+rgs_period*TimeClass.oneDay, rgs_period*TimeClass.oneDay, pi);

            Toast.makeText(getApplicationContext(), "찰나가 수정되었습니다! " + rgs_hour +"시 " + rgs_min +"분에 알려드릴게요!",Toast.LENGTH_LONG).show();
        }
        else{
            myDB.dbDeleteFromALARM(new String[]{Integer.toString(_projectData.id)});
            Toast.makeText(getApplicationContext(), "찰나가 수정되었습니다! ",Toast.LENGTH_LONG).show();
        }
        myDB.syncProjectData(_projectData);
        setResult(RESULT_OK);
        finish();
    }
    public void nonModifyModeComplete(){
        Toast.makeText(getApplicationContext(), "찰나가 수정되었습니다!  "+ rgs_period + "일 마다 , " + rgs_hour +"시 " + rgs_min +"분에 알려드릴게요!",Toast.LENGTH_LONG).show();

        String name = project_name.getText().toString();
        if(!checkName(name) ){
            Toast.makeText(getApplicationContext(), "유효하지 않은 이름입니다!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(myDB.getDataByNameFromPROJECT(name)!=null){
            Toast.makeText(getApplicationContext(), "중복되는 이름이에요!",Toast.LENGTH_SHORT).show();
            return;
        }

        int wide = StaticInformation.DISPLAY_ORIENTATION_DEFAULT;
        int mode = 0;

        //버튼으로 받은 알람 정보를 넘겨주기 위함
        File file = new File(StaticInformation.CHALNA_PATH+"/"+name);
        if ( !file.exists() ){
            if(file.mkdir()){
                Log.d("DEBUG_TEST", StaticInformation.CHALNA_PATH+"/"+name  + " Success");
            }
        }
        ProjectData projectData = new ProjectData(-1,name, mode, wide, StaticInformation.CHALNA_PATH+"/"+name,0,0,
                DescriptionManager.getNewDescription(),System.currentTimeMillis(),System.currentTimeMillis(),
                0, StaticInformation.GUIDED_SOBELFILTER);
        myDB.dbInsertionIntoPROJECT(projectData);
        projectData = myDB.getDataByNameFromPROJECT(projectData.name);

        if(toggleButton.isChecked()){
            Intent intent = new Intent(ProjectCreateController.this, AlarmBroadcastController.class);
            intent.putExtra(DBSQLiteModel.PROJECT_ID, projectData.id);

            // Calendar의 알람주기를 설정
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            String[] date_info = TimeClass.timeDateToNumeric(calendar.getTime());
            calendar.set(Integer.parseInt(date_info[0]), Integer.parseInt(date_info[1])-1, Integer.parseInt(date_info[2]), rgs_hour, rgs_min, 1);

            AlarmData ad = myDB.getDataByNameFromALARM(Integer.toString(projectData.id));
            if(ad!=null){
                myDB.dbDeleteFromALARM(new String[]{Integer.toString(projectData.id)});
            }
            myDB.dbInsertionIntoALARM(projectData.id, calendar.getTimeInMillis(), rgs_period, calendar.getTimeInMillis());
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), projectData.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            //아래 알람 울리는거 repeat으로 바꿔야해!
            if(TimeClass.getCurrentTime() < calendar.getTimeInMillis())
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), rgs_period*TimeClass.oneDay, pi);
            else
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+rgs_period*TimeClass.oneDay, rgs_period*TimeClass.oneDay, pi);

            Toast.makeText(getApplicationContext(), "새로운 찰나가 만들어졌어요! " + rgs_hour +"시 " + rgs_min +"분에 알려드릴게요!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "새로운 찰나가 만들어졌어요! ",Toast.LENGTH_LONG).show();
        }
        setResult(RESULT_OK);
        finish();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_create);

        Intent intent = getIntent();
        modifyMode = intent.getBooleanExtra("MODIFY_MODE", false);

        myDB = DBSQLiteModel.getInstance(this);
        //----------------------------------- 알람 주기 설정 -------------------------------------
        project_name = findViewById(R.id.project_create_name);
        project_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    project_name.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // 스피너 - 반복 주기를 선택
        Spinner period_spinner = (Spinner) findViewById(R.id.project_create_period);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, period_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        period_spinner.setAdapter(adapter);
        period_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                rgs_period = Integer.parseInt(period_item[position]);
                // 주기를 선택하였을 때
            }
            @Override
            public void onNothingSelected(AdapterView adapterView) {
            }
        });

        toggleButton = findViewById(R.id.toggle);
        toggleButton.setChecked(false);
        //---------------------------------------------------------------------------------------


        //----------------------------------- 알람 시간 설정 -------------------------------------
        //알람 시간을 나타내기 위한 area
        cal = Calendar.getInstance();
        int hourOfDay = cal.get(cal.HOUR_OF_DAY);
        final int min = cal.get(cal.MINUTE);

        rgs_hour = hourOfDay;
        rgs_min = min;

        tp = (TimePicker) findViewById(R.id.project_create_time_picker);


        // 초기값(현재 시간)
        tp.setOnTimeChangedListener(this);
        //---------------------------------------------------------------------------------------

        //----------------------버튼을 눌렀을 때, 그 결과를 TextView에 나타내기 위한 과정-------------
        resultView = (TextView) findViewById(R.id.resultView);
        // 등록버튼 눌렀을 때
        registerBtn = (ImageButton) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modifyMode){
                    nonModifyModeComplete();
                }else{
                    modifyModeComplete();
                }
                //---------------------------------------------------------
            }//end onClick method

        });//end registerBtn Listener


        if(modifyMode){
            int project_id = intent.getIntExtra(DBSQLiteModel.PROJECT_ID, -1);
            if(project_id == -1){
                modifyMode = false;
                return;
            }
            _projectData = myDB.getDataByIdFromPROJECT(Integer.toString(project_id));
            _alarmData = myDB.getDataByNameFromALARM(Integer.toString(project_id));

            project_name.setText(_projectData.name);
            project_name.setSelection(project_name.length());

            if(_alarmData != null) {
                toggleButton.setChecked(true);
                period_spinner.setSelection(_alarmData.alarm_cycle - 1);

                String[] times = TimeClass.timeNumericToString(_alarmData.alarm_time);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tp.setHour(Integer.parseInt(times[TimeClass.HOUR]));
                    tp.setMinute(Integer.parseInt(times[TimeClass.MINUTE]));
                } else {
                    tp.setCurrentHour(Integer.parseInt(times[TimeClass.HOUR]));
                    tp.setCurrentMinute(Integer.parseInt(times[TimeClass.MINUTE]));
                }
            }else{
                toggleButton.setChecked(false);
            }
        }
    }//end onCreate method

    // ----------시간 주기 변경 ------->시간 스피너를 터치하여 값에 변동을 주었을 때------------------
    public static boolean isValidFileName(String fileName) {
        if(fileName == null || fileName.trim().length() == 0)
            return false;

        return !Pattern.compile(StaticInformation.ILLEGAL_EXP).matcher(fileName).find();
    }
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        rgs_hour = hourOfDay;
        rgs_min = minute;
    }
    boolean checkName(String name){
        return isValidFileName(name);
    }
}//end MainActivity class