package com.slife.chris.studentlife.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.utilities.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class IntroTimeTable extends AppCompatActivity {

    private Button viewTimeTableBtn,introTimeTableBtn;
    private TextView groupTv;

    private String dept,classGroup,regNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_time_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       StudentDb studentDb = new StudentDb(getApplicationContext());
        try {
            studentDb.open();
             ArrayList<HashMap<String,String>> studentData = studentDb.getStudentData();
             dept = studentData.get(0).get("dept");
             classGroup = studentData.get(0).get("classGroup");
             regNo =  studentData.get(0).get("regNo");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        studentDb.close();

        groupTv = (TextView) findViewById(R.id.class_group);
        groupTv.setTypeface(Constants.getRobotoMedium(getApplicationContext()));
        if(dept !=null && classGroup != null) groupTv.setText(dept+" "+classGroup);
        viewTimeTableBtn = (Button)findViewById(R.id.btn_viewTT);
        viewTimeTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroTimeTable.this, TimetableActivity.class);
                startActivity(intent);
            }
        });
        introTimeTableBtn = (Button)findViewById(R.id.btn_viewET);
        introTimeTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroTimeTable.this, ExamsActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
