package com.slife.chris.studentlife.lecturer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.slife.chris.studentlife.R;

/**
 * Created by prrane on 12/22/16.
 */

public class TargetActivity extends AppCompatActivity {

        private TextView textView ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_target);
            textView = (TextView)findViewById(R.id.textView17);
            textView.setText("The Target Message Can Be Shown Here ");
        }
    }

