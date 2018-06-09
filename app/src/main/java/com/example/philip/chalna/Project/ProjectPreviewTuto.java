package com.example.philip.chalna.Project;


import android.widget.Toast;

import com.example.philip.chalna.Camera.CameraController;
import com.example.philip.chalna.Project.ProjectSelectController;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ProjectPreviewTuto {
    ProjectPreviewController activity_class;
    final int millls = 500;
    public ProjectPreviewTuto(ProjectPreviewController psc){
        activity_class = psc;
    }

    //기록하기
    public void tutorial_start(){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.takePictureBtn) // set what view will be pointed or highlighted
                .setTitleText("기록하기 : 촬영") // set the title of the tutorial
                .setContentText("카메라를 실행시켜 사진을 촬영합니다.") // set the content or detail text
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

    // 가져오기
    private void one_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.fetchBtn) // set what view will be pointed or highlighted
                .setTitleText("가져오기") // set the title of the tutorial
                .setContentText("촬영하지 않고, 갤러리에서 사진을 가져옵니다.") // set the content or detail text
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

    // 이미지 목록
    private void two_tuto(final int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.Image_View) // set what view will be pointed or highlighted
                .setTitleText("이미지 목록") // set the title of the tutorial
                .setContentText("'찰나'프로젝트에 기록된 모든 사진들을 볼 수 있습니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .withRectangleShape(true)
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

    // 식뷰 seek view
    private void three_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.seekBar) // set what view will be pointed or highlighted
                .setTitleText("이미지 슬라이드 바") // set the title of the tutorial
                .withRectangleShape(true)
                .setContentText("슬라이드 바를 움직여, 사진들을 볼 수 있습니다.") // set the content or detail text
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

    // 사진 삭제
    private void four_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.deleteBtn) // set what view will be pointed or highlighted
                .setTitleText("사진 삭제") // set the title of the tutorial
                .setContentText("이미지 목록에 나타난 현재 사진을 1장 삭제합니다.") // set the content or detail text
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

    // 미리보기 프리뷰
    private void five_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.showBtn) // set what view will be pointed or highlighted
                .setTitleText("미리보기 : Preview") // set the title of the tutorial
                .setContentText("모든 찰나 사진들을 GIF 형식으로 볼 수 있습니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }
                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        six_tuto(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                .show();
    }

    // 저장하기
    private void six_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.saveBtn) // set what view will be pointed or highlighted
                .setTitleText("저장하기") // set the title of the tutorial
                .setContentText("소중한 '찰나'의 순간들을 GIF파일로 변환하기 위해, 설정화면으로 이동합니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }
                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        Toast.makeText(activity_class, "이제 '찰나'를 이용해보세요! ", Toast.LENGTH_LONG).show();
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                .show();
    }
}
