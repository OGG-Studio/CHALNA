package com.example.philip.chalna;

public class AlarmData {
    int id;
    int project_id;
    String alarm_time;
    int alarm_cycle;
    String alram_next_time;

    public AlarmData(int _id, int _project_id, String _alarm_time, int _alarm_cycle, String _alram_next_time){
        id = _id;
        project_id = _project_id;
        alarm_time = _alarm_time;
        alarm_cycle = _alarm_cycle;
        alram_next_time = _alram_next_time;
    }
}
