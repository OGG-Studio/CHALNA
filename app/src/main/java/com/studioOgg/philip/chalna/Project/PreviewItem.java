package com.studioOgg.philip.chalna.Project;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studioOgg.philip.chalna.R;

public class PreviewItem extends LinearLayout {
    public ImageView imageView;
    public TextView name;
    public TextView description;
    public TextView startTime; // 알람시작시간....
    public TextView alarmCycle;

    public PreviewItem(Context context)
    {
        super(context);
        initView();
    }
    public PreviewItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView();
    }
    private void initView()
    {
        inflate(getContext(), R.layout.project_selection_view, this);
        name = findViewById(R.id.project_item_name);
        description = findViewById(R.id.project_item_description);
        startTime = findViewById(R.id.project_item_startTime);
        alarmCycle = findViewById(R.id.project_item_alarmCycle);
        imageView = findViewById(R.id.project_item_preview);
    }
}
