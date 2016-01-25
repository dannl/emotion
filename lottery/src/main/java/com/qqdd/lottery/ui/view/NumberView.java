package com.qqdd.lottery.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qqdd.lottery.R;
import com.qqdd.lottery.data.NumberTable;

import java.text.DecimalFormat;

/**
 * simple view to display a number.
 * Created by danliu on 1/20/16.
 */
public class NumberView extends LinearLayout {

    public enum Display {
        NORMAL, SPECIAL
    }

    private static final DecimalFormat PROBABILITY_FORMAT = new DecimalFormat("0.00");

    private TextView mNumber;
    private TextView mProbability;

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
        mProbability = (TextView) findViewById(R.id.rate);
        setupDisplay(display);
    }

    private void setupDisplay(Display display) {
        final Resources resources = getResources();
        if (display == Display.NORMAL) {
            mNumber.setTextColor(resources.getColor(R.color.colorPrimary));
            mNumber.setBackgroundResource(R.drawable.red_circle_bg);
            mProbability.setTextColor(resources.getColor(R.color.colorPrimary));
        } else {
            mNumber.setBackgroundResource(R.drawable.blue_circle_bg);
            mNumber.setTextColor(resources.getColor(R.color.colorAccent));
            mProbability.setTextColor(resources.getColor(R.color.colorAccent));
        }
    }

    public void setNumber(final com.qqdd.lottery.data.Number number, final NumberTable table) {
        if (number == null) {
            mNumber.setText("");
            mProbability.setText("");
        } else {
            mNumber.setText(String.valueOf(number.getValue()));
            final float totalWeight = table.getTotalWeight();
            if (totalWeight == 0f) {
                mProbability.setText("0.0%");
            } else {
                mProbability.setText(String.format("%s%%", PROBABILITY_FORMAT.format(number.getWeight() / table.getTotalWeight() * 100)));
            }
        }
    }
}