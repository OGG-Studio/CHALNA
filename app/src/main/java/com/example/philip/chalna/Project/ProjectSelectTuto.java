package com.example.philip.chalna.Project;

import android.widget.Toast;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ProjectSelectTuto {
    ProjectSelectController activity_class;
    final int millls = 500;
    public ProjectSelectTuto(ProjectSelectController psc){
        activity_class = psc;
    }
    public void tutorial_start(){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.menuOpenBtn) // set what view will be pointed or highlighted
                .setTitleText("새 프로젝트") // set the title of the tutorial
                .setContentText("프로젝트 생성 및 환경설정이 가능합니다.") // set the content or detail text
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        newCreateProject(millls);
                    }
                })
                .setDelay(millls) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                .show();
    }
    private void newCreateProject(int millis){
        activity_class.handler.post(new Runnable() {
            @Override
            public void run() {
                activity_class.openMenuOption();
            }
        });
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.newProjectBtn) // set what view will be pointed or highlighted
                .setTitleText("새로운 찰나 만들기") // set the title of the tutorial
                .setContentText("새로운 프로젝트를 생성할 수 있습니다..") // set the content or detail text
                .withRectangleShape(true)
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        settingProject(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                .show();
    }
    private void settingProject(int millis){
        activity_class.handler.post(new Runnable() {
            @Override
            public void run() {
                activity_class.openMenuOption();
            }
        });
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.settingBtn) // set what view will be pointed or highlighted
                .setTitleText("정보 및 옵션") // set the title of the tutorial
                .setContentText("찰나에 대한 정보와 옵션 설정을 할 수 있습니다.") // set the content or detail text
                .withRectangleShape(true)
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        Playing_area_tuto(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                .show();
    }
    private void Playing_area_tuto(int millis){
        activity_class.handler.post(new Runnable() {
            @Override
            public void run() {
                activity_class.closeMenuOption();
            }
        });
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.Playing_area) // set what view will be pointed or highlighted
                .setTitleText("진행중인 프로젝트") // set the title of the tutorial
                .setContentText("현재 진행중인 프로젝트 목록을 보여줍니다.") // set the content or detail text
                .withRectangleShape(true)
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        Finish_area_tuto(millls);
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                .show();
    }
    private void Finish_area_tuto(int millis){
        new MaterialShowcaseView.Builder(activity_class) // instantiate the material showcase view using Builder
                .setTarget(activity_class.Finish_area) // set what view will be pointed or highlighted
                .setTitleText("완료된 프로젝트") // set the title of the tutorial
                .setContentText("완료된 프로젝트 목록을 보여줍니다.") // set the content or detail text
                .withRectangleShape(true)
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                    }
                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        Toast.makeText(activity_class, "찰나에 오신것을 환영합니다 !", Toast.LENGTH_SHORT).show();
                    }
                })
                .setDelay(millis) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant'
                .show();
    }

}
