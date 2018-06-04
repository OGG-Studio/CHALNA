package com.example.philip.chalna;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
