package com.slife.chris.studentlife.lecturer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.slife.chris.studentlife.R;

public class LecturerMainActivity extends AppCompatActivity {

    private CardView pendingCardView;
    private CardView setCardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pendingCardView = (CardView) findViewById(R.id.btn_pending_units);
        pendingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LecturerMainActivity.this,LecturerUnitsActivity.class);
                i.putExtra("status","pending");
                startActivity(i);
            }
        });

        setCardView = (CardView) findViewById(R.id.btn_set_units);
        setCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LecturerMainActivity.this,LecturerUnitsActivity.class);
                i.putExtra("status","set");
                startActivity(i);
            }
        });

    }

}
