package com.example.philip.notisetting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener {

    String[] period_item = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14"};
    TextView periodView;
    TextView timeView;

    Calendar cal;
    TimePicker tp;

    Button registerBtn;

    int rgs_period;
    int rgs_hour;
    int rgs_min;

    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        periodView = (TextView) findViewById(R.id.period_view); // 주기를 나타낼 텍스트뷰
        timeView = (TextView) findViewById(R.id.time_view);            // 시간을 나타낼 텍스트 뷰

        // 스피너 - 반복 주기를 선택
        Spinner period_spinner = (Spinner) findViewById(R.id.period_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, period_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        period_spinner.setAdapter(adapter);

        period_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                periodView.setText(period_item[position]+"일 마다 ");
                rgs_period = Integer.parseInt(period_item[position]);
                // 주기를 선택하였을 때
            }
            @Override
            public void onNothingSelected(AdapterView adapterView) {
                periodView.setText("");
                // 아무것도 선택되지 않았을 때
            }
        });

        //알람 시간을 나타내기 위한 area
        cal = Calendar.getInstance();
        int hourOfDay = cal.get(cal.HOUR_OF_DAY);
        int min = cal.get(cal.MINUTE);

        rgs_hour = hourOfDay;
        rgs_min = min;

        timeView = (TextView) findViewById(R.id.time_view);
        tp = (TimePicker) findViewById(R.id.tp);

        // 초기값(현재 시간)
        timeView.setText("");
        tp.setOnTimeChangedListener(this);


        resultView = (TextView) findViewById(R.id.resultView);
        // 등록버튼 눌렀을 때
        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resultView.setText("주기 : "+rgs_period+"  시간 : "+rgs_hour+"시  "+rgs_min+"분");
                resultView.setText("  당신의 '찰나'를 위해    "+rgs_period+"일마다,   "+rgs_hour+"시 "+rgs_min+"분에  알려드릴게요.");

            }//end onClick method
        });//end registerBtn Listener



    }//end onCreate method

    // 시간 스피너를 터치하여 값에 변동을 주었을 때
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        timeView.setText(hourOfDay+"시  "+minute+"분");
        rgs_hour = hourOfDay;

    }


}//end MainActivity class
