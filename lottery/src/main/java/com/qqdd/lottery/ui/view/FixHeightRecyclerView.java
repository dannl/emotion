package com.qqdd.lottery.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * fix height list view or grid view.
 * Created by danliu on 2016/1/20.
 */
public class FixHeightRecyclerView extends RecyclerView {

    public FixHeightRecyclerView(Context context) {
        super(context);
    }

    public FixHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixHeightRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(0xffff, MeasureSpec.AT_MOST));
    }
}
