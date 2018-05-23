package com.example.philip.chalna;

public class ProjectData {
    int id;
    String name;
    int mode;
    int wide;
    String dir;
    int project_level; // 0 is playing, 1 is complete

    public ProjectData(int _id, String _name, int _mode, int _wide, String _dir, int _project_level){
        id = _id;
        name = _name;
        mode = _mode;
        wide = _wide;
        dir = _dir;
        project_level = _project_level;
    }
}
