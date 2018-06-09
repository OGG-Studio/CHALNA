package com.example.philip.chalna.Project;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.philip.chalna.R;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class IntroductionController extends AppCompatActivity{
    ImageView jang, yun, moon, yun2;

    Activity activity_class = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_introduction);

        jang = findViewById(R.id.jang);
        yun = findViewById(R.id.yun);
        moon = findViewById(R.id.moon);


        jang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                        .setTarget(jang) // set what view will be pointed or highlighted
                        .setTitleText("긴 바다곰(Long Sea Bear)") // set the title of the tutorial
                        .setContentText("배부른 프로그래머보다 배부른 프로게이머가 되고 싶다") // set the content or detail text
                        .setDismissOnTouch(true)
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                            }
                        })
                        .withCircleShape()
                        .setDelay(500) // set delay in milliseconds to show the tutor
                        //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                        .show();
            }
        });
        yun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                        .setTarget(yun) // set what view will be pointed or highlighted
                        .setTitleText("용 그래머(Yong grammer)") // set the title of the tutorial
                        .setContentText("8시간 취침 소취") // set the content or detail text
                        .setDismissOnTouch(true)
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                            }
                        })
                        .withCircleShape()
                        .setDelay(500) // set delay in milliseconds to show the tutor
                        //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                        .show();
            }
        });
        moon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                        .setTarget(moon) // set what view will be pointed or highlighted
                        .setTitleText("필립(philip)") // set the title of the tutorial
                        .setContentText("복정동 캣대디(Philip.Box)") // set the content or detail text
                        .setDismissOnTouch(true)
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                            }
                        })
                        .withCircleShape()
                        .setDelay(500) // set delay in milliseconds to show the tutor
                        //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                        .show();
            }
        });
    }

}