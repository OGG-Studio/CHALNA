package com.example.philip.chalna.Database;

public class AlarmData {
    public int id;
    public int project_id;
    public long alarm_time;
    public int alarm_cycle;
    public long alram_next_time;

    public AlarmData(int _id, int _project_id, long _alarm_time, int _alarm_cycle, long _alram_next_time){
        id = _id;
        project_id = _project_id;
        alarm_time = _alarm_time;
        alarm_cycle = _alarm_cycle;
        alram_next_time = _alram_next_time;
    }
}
