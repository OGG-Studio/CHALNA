package com.studioOgg.philip.chalna.Project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.studioOgg.philip.chalna.R;
import com.studioOgg.philip.chalna.Utils.StaticInformation;

public class SettingController extends AppCompatActivity{
    SharedPreferences sh_pre;
    SharedPreferences.Editor editor;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_view);

        final ToggleButton btn = (ToggleButton) this.findViewById(R.id.notyBtn);
        sh_pre = getSharedPreferences(StaticInformation.GLOBAL_SETTING, MODE_PRIVATE);
        if(sh_pre.getInt(StaticInformation.GLOBAL_SETTING_NOTI, 1) == 0){
            btn.setChecked(true);
            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.off_toggle));
            Log.d("DEBUG_TEST", " sh_pre get 0");
        }else{
            btn.setChecked(false);
            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.on_toggle));
            Log.d("DEBUG_TEST", " sh_pre get 1");
        }
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                sh_pre = getSharedPreferences(StaticInformation.GLOBAL_SETTING, MODE_PRIVATE);
                editor = sh_pre.edit();

                if (btn.isChecked()){
                    editor.putInt(StaticInformation.GLOBAL_SETTING_NOTI, 0);
                    btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.off_toggle));
                }else{
                    btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.on_toggle));
                    editor.putInt(StaticInformation.GLOBAL_SETTING_NOTI, 1);
                }
                Log.d("DEBUG_TEST", btn.isChecked() + " ");
                editor.commit();
            }
        });

        findViewById(R.id.wiki).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/PhilipBox/CHALNA/wiki"));
                        startActivity(intent);
                    }
                }
        );

        findViewById(R.id.star).setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.stdioOgg.philip.chalna"));
                        startActivity(intent);
                    }
                }
        );

        findViewById(R.id.dev).setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), IntroductionController.class);
                        startActivity(intent);
                    }
                }
        );

        Button easterEgg = findViewById(R.id.easter_egg);
        easterEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
                alert_confirm.setTitle("개발자는 잠이 부족해");
                alert_confirm.setMessage("SOS... 살려주세여...").setCancelable(false).setPositiveButton("확인",null);
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });
    }
}