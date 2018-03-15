package com.slife.chris.studentlife.units;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.slife.chris.studentlife.R;


/**
 * Created by jorge on 2/08/14.
 */
public class PagedHeadListView extends ListView {

    private View headerView;
    private ViewPager mPager;
    private ViewPagerAdapter headerViewPagerAdapter;

    //Custom attrs
    private float headerHeight;
    private boolean disableVerticalTouchOnHeader;
    private int indicatorBgColor;
    private int indicatorColor;
    private int indicatorType;
    private int pageTransformer;

    private PagedHeadIndicator indicator;

    /**
     * Inner listener defined to be used if disableVerticalTouchOnHeader attr is set to true
     */
    private OnTouchListener touchListenerForHeaderIntercept = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

    public PagedHeadListView(Context context) {
        super(context);
        init(null);
    }

    public PagedHeadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PagedHeadListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PagedHeadListView);

            headerHeight = a.getDimensionPixelSize(R.styleable.PagedHeadListView_headerHeight, 300);

            disableVerticalTouchOnHeader = a.getBoolean(R.styleable.PagedHeadListView_disableVerticalTouchOnHeader, false);
            indicatorBgColor = a.getColor(R.styleable.PagedHeadListView_indicatorBgColor, getResources().getColor(R.color.material_blue));
            indicatorColor = a.getColor(R.styleable.PagedHeadListView_indicatorColor, getResources().getColor(R.color.material_light_blue));
            indicatorType = a.getInt(R.styleable.PagedHeadListView_indicatorType, IndicatorTypes.BOTTOMALIGNED.ordinal());
            pageTransformer = a.getInt(R.styleable.PagedHeadListView_pageTransformer, PageTransformerTypes.DEPTH.ordinal());

            a.recycle();
        }

        initializePagedHeader();
    }

    private void initializePagedHeader() {

        headerView = View.inflate(getContext(), R.layout.paged_header, null);

        LayoutParams headerViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) headerHeight);
        headerView.setLayoutParams(headerViewParams);

        mPager = (ViewPager) headerView.findViewById(R.id.headerViewPager);
        FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        headerViewPagerAdapter = new ViewPagerAdapter(fragmentManager);

        indicator = new PagedHeadIndicator(getContext());
        indicator.setBgColor(indicatorBgColor);
        indicator.setColor(indicatorColor);

        switch (indicatorType) {
            case 0:
                addHeaderView(headerView);
                break;
            case 1:
                addHeaderView(indicator);
                addHeaderView(headerView);
                break;
            case 2:
                addHeaderView(headerView);
                addHeaderView(indicator);
                break;
        }

        mPager.setAdapter(headerViewPagerAdapter);
       mPager.setOnPageChangeListener(indicator);

        if (disableVerticalTouchOnHeader)
            mPager.setOnTouchListener(touchListenerForHeaderIntercept);

        setHeaderPageTransformer(PageTransformerTypes.values()[pageTransformer]);
    }

    public void setHeaderPageTransformer(PageTransformerTypes pageTransformerType) {
        if (pageTransformerType.equals(PageTransformerTypes.ZOOMOUT)) {
            mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        }

    }

    /**
     * Created to allow custom page transformers supplied by the users
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     *                            to be drawn from last to first instead of first to last.
     * @param customPageTransformer PageTransformer that will modify each page's animation properties
     */
    public void setHeaderPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer customPageTransformer) {
        mPager.setPageTransformer(reverseDrawingOrder, customPageTransformer);
    }

    /**
     * Mapped to allow users to listen to page change events
     * @param onPageChangeListener
     */
    public void setOnHeaderPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {


        mPager.setOnPageChangeListener(onPageChangeListener);


    }

    public void addFragmentToHeader(Fragment fragmentToAdd) {
        indicator.addPage();
        headerViewPagerAdapter.addFragment(fragmentToAdd);
    }

    public void setHeaderOffScreenPageLimit(int offScreenPageLimit){
        mPager.setOffscreenPageLimit(offScreenPageLimit);
    }

    public void setIndicatorBgColor(int bgColor) {
        indicator.setBgColor(bgColor);
    }

    public void setIndicatorColor(int color) {
        indicator.setColor(color);
    }

    public void disableVerticalTouchOnHeader() {
        mPager.setOnTouchListener(null);
        mPager.setOnTouchListener(touchListenerForHeaderIntercept);
    }

    /**
     * Height in pixels for the header
     * @param newHeaderHeight
     */
    public void setHeaderHeight(int newHeaderHeight) {
        LayoutParams headerViewParams = (LayoutParams) headerView.getLayoutParams();
        headerViewParams.height = newHeaderHeight;
        headerView.setLayoutParams(headerViewParams);
    }
}
