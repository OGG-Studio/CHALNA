package com.example.philip.chalna.Database;

public class ProjectData {
    public int id;
    public String name;
    public int mode;
    public int wide;
    public String dir;
    public int project_level; // 0 is playing, 1 is complete
    public int zoom_factor;

    // ADDING
    public String description ="";
    public long modificationDate;
    public long startDate;

    public ProjectData(int _id, String _name, int _mode, int _wide, String _dir, int _project_level, int _zoom
                        ,String _description, long _modificationDate, long _startDate){
        id = _id;
        name = _name;
        mode = _mode;
        wide = _wide;
        dir = _dir;
        project_level = _project_level;
        zoom_factor = _zoom;

        description = _description;
        modificationDate = _modificationDate;
        startDate = _startDate;
    }
}
