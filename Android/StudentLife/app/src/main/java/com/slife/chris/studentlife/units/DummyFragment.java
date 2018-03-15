package com.slife.chris.studentlife.units;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slife.chris.studentlife.R;


/**
 * Created by jorge on 2/08/14.
 */
public class DummyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dummy, container, false);
        return rootView;
    }
}
