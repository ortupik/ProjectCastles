package com.slife.chris.studentlife.units;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by jorge on 7/08/14.
 */
public abstract class AbstractPagedHeadIndicator extends FrameLayout implements ViewPager.OnPageChangeListener {

    protected int pageCount = 0;

    public AbstractPagedHeadIndicator(Context context) {
        super(context);
    }

    public AbstractPagedHeadIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AbstractPagedHeadIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public abstract void init();
    public abstract void addPage();
    public abstract void setBgColor(int bgColor);
    public abstract void setColor(int indicatorColor);
}
