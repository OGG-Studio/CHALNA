package com.studioOgg.philip.chalna.Camera;


import android.widget.Toast;

import com.studioOgg.philip.chalna.Project.ProjectSelectController;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class CameraTuto {
    CameraController activity_class;
    final int millls = 500;
    public CameraTuto(CameraController psc){
        activity_class = psc;
    }

    //셋팅버튼
    public void tutorial_start(){
        activity_class.animateViews(0);
        activity_class.mOrientationEventListener.disable();

        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.settingBtn) // set what view will be pointed or highlighted
                .setTitleText("설정 버튼") // set the title of the tutorial
                .setContentText("카메라 환경설정을 할 수 있습니다. ") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        one_tuto(millls);
                    }
                })
                .setDelay(millls) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                .show();
    }

    // 이미지 로드
    private void one_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.btnImageLoad) // set what view will be pointed or highlighted
                .setTitleText("가져오기") // set the title of the tutorial
                .setContentText("구도를 가이드해줄 사진을 갤러리에서 가져옵니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        two_tuto(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                .show();
    }

    // 가이드 모드 변경
    private void two_tuto(final int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.changeGuidedModeBtn) // set what view will be pointed or highlighted
                .setTitleText("가이드 모드 변경") // set the title of the tutorial
                .setContentText("두 가지 구도 가이드 모드를 변경하며 사용할 수 있습니다! (투명도/테두리)") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }
                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        three_tuto(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                .show();
    }

    // 사진찍기
    private void three_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.takePictureBtn) // set what view will be pointed or highlighted
                .setTitleText("사진 촬영") // set the title of the tutorial
                .setContentText("찰나의 순간을 찰-칵! 촬영된 사진은 바로 가이드 사진으로 적용됩니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }
                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        four_tuto(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                .show();
    }

    // 카메라 전/후 변경
    private void four_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.changeViewBtn) // set what view will be pointed or highlighted
                .setTitleText("카메라 전환") // set the title of the tutorial
                .setContentText("전면/후면 카메라의 방향을 전환할 수 있습니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }
                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        five_tuto(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                .show();
    }

    // SEEK bar  투명도 조절 버튼
    private void five_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.seekBar) // set what view will be pointed or highlighted
                .setTitleText("투명도 조절 버튼") // set the title of the tutorial
                .setContentText("가이드해주는 사진의 투명도를 조절할 수 있습니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }
                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        Toast.makeText(activity_class, "소중한 '찰나'를 기록해 보세요! 출발-!", Toast.LENGTH_SHORT).show();
                        activity_class.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                activity_class.mOrientationEventListener.enable();
                            }
                        });
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                .show();
    }
}
