package com.slife.chris.studentlife.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.units.MyUnitsActivity;
import com.slife.chris.studentlife.utilities.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CastleUtilities extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.btn_class_tt)CardView btnClassTT;
    @Bind(R.id.btn_display_units) CardView btnDisplayUnits;
    @Bind(R.id.btn_rooms) CardView btnRooms;
    @Bind(R.id.class_group) TextView classGroup;

    private String dept,group,regNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_castle_utilities);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnClassTT.setOnClickListener(this);
        btnDisplayUnits.setOnClickListener(this);
        btnRooms.setOnClickListener(this);

        StudentDb studentDb = new StudentDb(getApplicationContext());
        try {
            studentDb.open();
            ArrayList<HashMap<String,String>> studentData = studentDb.getStudentData();
            dept = studentData.get(0).get("dept");
            group = studentData.get(0).get("classGroup");
            regNo =  studentData.get(0).get("regNo");
            classGroup.setText(dept+" "+group);
            classGroup.setTypeface(Constants.getRobotoMedium(getApplicationContext()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        studentDb.close();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_class_tt:
                intent = new Intent(CastleUtilities.this, IntroTimeTable.class);
                startActivity(intent);
                break;
            case R.id.btn_display_units:
                intent = new Intent(CastleUtilities.this, MyUnitsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_rooms:
                intent = new Intent(CastleUtilities.this, RoomsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
