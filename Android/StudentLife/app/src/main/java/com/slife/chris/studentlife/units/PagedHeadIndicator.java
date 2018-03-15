package com.slife.chris.studentlife.units;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.slife.chris.studentlife.R;


/**
 * Created by jorge on 5/08/14.
 */
public class PagedHeadIndicator extends AbstractPagedHeadIndicator {

    private View indicatorView;
    private Context context;
    private int lastPageOffset = 0;
    private int screenWidthPixels;

    public PagedHeadIndicator(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PagedHeadIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PagedHeadIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    @Override
    public void init() {

        screenWidthPixels = DisplayUtils.getScreenWidthPixels(context);

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.indicator_height));
        setLayoutParams(layoutParams);

        setBackgroundColor(getResources().getColor(R.color.material_blue)); //default indicator bg color

        addIndicatorView();
    }

    private void addIndicatorView() {
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT);

        indicatorView = new View(context);
        indicatorView.setBackgroundColor(getResources().getColor(R.color.material_light_blue)); //default indicator color

        addView(indicatorView, indicatorParams);
    }

    @Override
    public void addPage() {
        pageCount++;
        recalculateIndicatorWidth();
    }

    /**
     * Recalculates indicator width each time a new page is added to the pager
     */
    private void recalculateIndicatorWidth() {
        int newWidth = screenWidthPixels / pageCount;
        FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) indicatorView.getLayoutParams();
        indicatorParams.width = newWidth;
        indicatorView.setLayoutParams(indicatorParams);
    }

    @Override
    public void setBgColor(int bgColor) {
        setBackgroundColor(bgColor);
    }

    @Override
    public void setColor(int indicatorColor) {
        indicatorView.setBackgroundColor(indicatorColor);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        int relativePositionOffset = 0;
        if (pageCount != 0) {
            relativePositionOffset = indicatorView.getWidth() * position + positionOffsetPixels / pageCount;
        }

        if (positionOffset != 0) {
            FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) indicatorView.getLayoutParams();
            indicatorParams.leftMargin = relativePositionOffset;
            indicatorView.setLayoutParams(indicatorParams);
        }

        System.out.println("page scroll -- postion of page "+position );


    }

    @Override
    public void onPageSelected(int position) {

        System.out.println("page scroll -- postion of page " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
