package com.studioOgg.philip.chalna.Utils;

public class DescriptionManager {
    public static final String DISCRIPTION_1 = "새로운 찰나의 순간을 간직하세요!"; // New
    public static final String DISCRIPTION_2 = "%s년 %s월 %s일 사진이 추가되었어요!"; // add
    public static final String DISCRIPTION_3 = "%s년 %s월 %s일 사진이 제거되었어요.."; // remove
    public static final String DISCRIPTION_4 = "새로운 찰나를 기록할 시간이에요!"; // New
    public static final String DISCRIPTION_5 = "%s.%s.%s ~ %s.%s.%s 의 찰나"; // Complete
    public static final String DISCRIPTION_6 = "새로운 찰나가 기록되었습니다!"; // SAVE
    public static final String DISCRIPTION_7 = "찰나의 정보가 변경되었어요!"; // SAVE

    /**
     * Create new project ment
     * @return
     */
    public static String getNewDescription() {
        return DISCRIPTION_1;
    }

    /**
     * Add ment
     * @param y
     * @param m
     * @param d
     * @param imageLen
     * @return
     */
    public static String getAddDescription(String y, String m, String d, int imageLen) {
        return String.format(DISCRIPTION_2, y, m, d);
    }

    /**
     * project remove ment
     * @param y
     * @param m
     * @param d
     * @param imageLen
     * @return
     */
    public static String getRemoveDescription(String y, String m, String d, int imageLen){
        return String.format(DISCRIPTION_3, y, m, d);
    }

    /**
     * alarm description ment
     * @return
     */
    public static String getAlarmDescription() {
        return DISCRIPTION_4;
    }

    /**
     * complete description ment
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getCompleteDescription(long startTime, long endTime) {
        String[] startTimeYear = TimeClass.timeNumericToString(startTime);
        String[] endTimeYear = TimeClass.timeNumericToString(endTime);
        return String.format(DISCRIPTION_5, startTimeYear[0], startTimeYear[1], startTimeYear[2], endTimeYear[0], endTimeYear[1], endTimeYear[2]); }

    /**
     * save description ment
     * @return
     */
    public static String getSaveDescription() {
        return DISCRIPTION_6;
    }

    /**
     * project modify ment
     * @return
     */
    public static String getModifyDescription() {
        return DISCRIPTION_7;
    }
}
