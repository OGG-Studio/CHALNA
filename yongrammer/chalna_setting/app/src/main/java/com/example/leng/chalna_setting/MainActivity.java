package com.example.leng.chalna_setting;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ToggleButton btn = (ToggleButton) this.findViewById(R.id.tBtn);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (btn.isChecked()){
                    btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.off_toggle));
                }else{
                    btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.on_toggle));
                }
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

        /*findViewById(R.id.ask).setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Uri uri = Uri.parse("mailto:yongrammer@gmail.com");
                        Intent email = new Intent(Intent.ACTION_SENDTO, uri);
                        startActivity(email);

                    }
                }
        );*/

        findViewById(R.id.dev).setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), introductionView.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
