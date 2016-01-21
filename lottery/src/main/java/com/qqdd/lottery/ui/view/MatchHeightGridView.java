package com.qqdd.lottery.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by DanLiu
 * Nov 19 2015
 */
public class MatchHeightGridView extends GridView {
    public MatchHeightGridView(Context context) {
        super(context);
    }

    public MatchHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchHeightGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0xffff, MeasureSpec.AT_MOST));
    }
}
