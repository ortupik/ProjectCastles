package com.slife.chris.studentlife.units;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.slife.chris.studentlife.R;

/**
 * Created by jorge on 31/07/14.
 */
public class FirstHeaderFragment extends Fragment {

    private View rootView;
    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder1;
    private ImageView classImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_header_first, container, false);


        classImageView = (ImageView)rootView.findViewById(R.id.classImageView);
        mDrawableBuilder1 = TextDrawable.builder()
                .round();

        TextDrawable drawable = mDrawableBuilder1.build("CS 3.1",0xfff06292);
        classImageView.setImageDrawable(drawable);


        return rootView;
    }
}
