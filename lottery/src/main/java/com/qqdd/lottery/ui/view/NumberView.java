package com.qqdd.lottery.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qqdd.lottery.R;
import com.qqdd.lottery.data.NumberTable;

import java.text.DecimalFormat;

/**
 * simple view to display a number.
 * Created by danliu on 1/20/16.
 */
public class NumberView extends LinearLayout implements Checkable {


    private boolean mIsChecked = false;

    @Override
    public void setChecked(boolean checked) {
        mIsChecked = checked;
        if (checked) {
            mNumber.setTextColor(getResources().getColor(android.R.color.white));
            if (mDisplay == Display.NORMAL) {
                mNumber.setBackgroundResource(R.drawable.red_circle_bg_pressed);
            } else {
                mNumber.setBackgroundResource(R.drawable.blue_circle_bg_pressed);
            }
        } else {
            if (mDisplay == Display.NORMAL) {
                mNumber.setTextColor(getResources().getColorStateList(R.color.color_primary_selector));
                mNumber.setBackgroundResource(R.drawable.red_circle_bg);
            } else {
                mNumber.setBackgroundResource(R.drawable.blue_circle_bg);
                mNumber.setTextColor(getResources().getColorStateList(R.color.color_accent_selector));
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mIsChecked);
    }

    public enum Display {
        NORMAL, SPECIAL
    }

    private TextView mNumber;
    private Display mDisplay;

    public NumberView(Context context) {
        this(context, Display.NORMAL);
    }

    public NumberView(Context context, Display display) {
        super(context);
        init(context, display);
    }

    private void init(final Context context, Display display) {
        inflate(context, R.layout.number_view, this);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        mNumber = (TextView) findViewById(R.id.number);
        mDisplay = display;
        setupDisplay(display);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    private void setupDisplay(Display display) {
        final Resources resources = getResources();
        if (display == Display.NORMAL) {
            mNumber.setTextColor(resources.getColorStateList(R.color.color_primary_selector));
            mNumber.setBackgroundResource(R.drawable.red_circle_bg);
        } else {
            mNumber.setBackgroundResource(R.drawable.blue_circle_bg);
            mNumber.setTextColor(resources.getColorStateList(R.color.color_accent_selector));
        }
    }

    public void setNumber(final int number) {
        mNumber.setText(String.valueOf(number));
    }
}
