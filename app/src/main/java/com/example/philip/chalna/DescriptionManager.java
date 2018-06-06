package com.example.philip.chalna;

public class DescriptionManager {
    public static final String DISCRIPTION_1 = "새로운 찰나의 순간을 간직하세요!"; // New
    public static final String DISCRIPTION_2 = "%s년 %s월 %s일 사진이 추가되었어요! [%d 장]"; // add
    public static final String DISCRIPTION_3 = "%s년 %s월 %s일 사진이 추가되었어요! [%d 장]"; // remove
    public static final String DISCRIPTION_4 = "새로운 찰나를 기록할 시간이에요!"; // New
    public static final String DISCRIPTION_5 = "[ 총 이미지 : %d, 초당 프레임 : %.2f, 해상도 %dx%d ]"; // Complete
    public static final String DISCRIPTION_6 = "새로운 찰나가 기록되었습니다!"; // SAVE
    public static final String DISCRIPTION_7 = "찰나의 정보가 변경되었어요!"; // SAVE

    public static String getNewDescription() {
        return DISCRIPTION_1;
    }
    public static String getAddDescription(String y, String m, String d, int imageLen) {
        return String.format(DISCRIPTION_2, y, m, d, imageLen);
    }
    public static String getRemoveDescription(String y, String m, String d, int imageLen){
        return String.format(DISCRIPTION_3, y, m, d, imageLen);
    }
    public static String getAlarmDescription() {
        return DISCRIPTION_4;
    }
    public static String getCompleteDescription(int imageLen, double frame, int width, int height) {
        return String.format(DISCRIPTION_5, imageLen, 1000/frame, width,height);
    }
    public static String getSaveDescription() {
        return DISCRIPTION_6;
    }
    public static String getModifyDescription() {
        return DISCRIPTION_7;
    }
}
