package com.example.philip.sundaynoti;

import android.app.Fragment;
import android.os.Build;
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
    Button timeBtn;

    Calendar cal;
    TimePicker tp;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        periodView = (TextView) findViewById(R.id.period_view);
        timeView = (TextView) findViewById(R.id.tv);


        Spinner period_spinner = (Spinner) findViewById(R.id.period_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, period_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        period_spinner.setAdapter(adapter);




        period_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                periodView.setText(period_item[position]);


            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {
                periodView.setText("");
            }
        });


        cal = Calendar.getInstance();
        int hourOfDay = cal.get(cal.HOUR_OF_DAY);
        int min = cal.get(cal.MINUTE);
        tv = (TextView) findViewById(R.id.tv);
        tp = (TimePicker) findViewById(R.id.tp);

        tv.setText("[초기 설정된 시각] "+hourOfDay+"시  " + min + "분  ");
        tp.setOnTimeChangedListener(this);
    }//end onCreate method


    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        tv.setText("[현재 설정된 시간]  "+hourOfDay+"시  "+minute+"분  ");
    }
}//end MainActivity class
