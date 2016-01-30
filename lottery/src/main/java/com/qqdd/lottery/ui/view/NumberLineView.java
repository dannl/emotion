package com.qqdd.lottery.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qqdd.lottery.data.*;
import com.qqdd.lottery.data.Number;

/**
 * Created by danliu on 2016/1/28.
 */
public class NumberLineView extends LinearLayout {
    public NumberLineView(Context context) {
        super(context);
        init();
    }

    public NumberLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    private LotteryConfiguration mConfiguration;

    public void setLottery(final ILottery lottery) {
        if (lottery == null || !lottery.isValid()) {
            removeAllViews();
            return;
        }
        final LotteryConfiguration configuration = lottery.getConfiguration();
        if (mConfiguration != configuration) {
            removeAllViews();
            mConfiguration = configuration;
            final int normalSize = configuration.getNormalSize();
            final int specialSize = configuration.getSpecialSize();
            final Context context = getContext();
            for (int i = 0; i < normalSize; i++) {
                final NumberView numberView = new NumberView(context, NumberView.Display.NORMAL);
                addView(numberView, newLayoutParam());
            }
            for (int i = 0; i < specialSize; i++) {
                final NumberView numberView = new NumberView(context, NumberView.Display.SPECIAL);
                addView(numberView, newLayoutParam());
            }
        }
        final NumberList normals = lottery.getNormals();
        final NumberList specials = lottery.getSpecials();


        final int normalSize = normals.size();
        for (int i = 0; i < normalSize; i++) {
            ((NumberView)getChildAt(i)).setNumber(normals.get(i));
        }
        for (int i = 0; i < specials.size(); i++) {
            ((NumberView) getChildAt(i + normalSize)).setNumber(specials.get(i));
        }
    }

    @NonNull
    private static LayoutParams newLayoutParam() {
        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        return params;
    }
}
