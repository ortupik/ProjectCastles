package com.slife.chris.studentlife.lecturer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.Response;
import com.slife.chris.studentlife.R;

import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class LecPreferenceActivity extends AppCompatActivity {

    private TextView txtunitName;
    private TextView txtunitCode;
    private TextView txtHours;
    private TextView txtCourseName;
    private TextView txtTimeTv;
    private Spinner daySpinner;
    private Button timeButton;
    private AppCompatButton setPrefBtn;

    private int lecturer_id,s_time,f_time,hours;
    private String day,unit_code,intake,lesson,class_group,dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lec_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        txtunitName = (TextView) findViewById(R.id.unit_name);
        txtCourseName = (TextView) findViewById(R.id.course_name);
        txtunitCode = (TextView) findViewById(R.id.unit_code);
        txtHours = (TextView) findViewById(R.id.hours);
        txtTimeTv = (TextView) findViewById(R.id.timeTV);
        daySpinner = (Spinner) findViewById(R.id.day_spinner);
        timeButton = (Button) findViewById(R.id.time_button);
        setPrefBtn = (AppCompatButton) findViewById(R.id.btn_set_pref);


        Bundle b = getIntent().getExtras();
        class_group = b.getString("class_group");
        dept = b.getString("course");
        unit_code = b.getString("unit_code");
        hours = Integer.parseInt(b.getString("hours"));

        txtCourseName.setText(dept+" "+class_group);
        txtunitName.setText(b.getString("name"));
        txtunitCode.setText(unit_code);
        txtHours.setText(hours +" Hours");

        final String[] accessor = {"MON","TUE","WED","THUR","FRI"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, accessor);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        daySpinner.setSelection(0);


        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lecturer_id = 1;
                day = daySpinner.getSelectedItem().toString();

                LinearTimePickerDialog dialog = LinearTimePickerDialog.Builder.with(LecPreferenceActivity.this)
                        .setDialogBackgroundColor(R.color.white)
                        .setPickerBackgroundColor(R.color.colorPrimary)
                        .setLineColor(Color.argb(64, 255, 255, 255))
                        .setTextColor(Color.WHITE)
                        .setShowTutorial(false)
                        .setTextBackgroundColor(Color.argb(16, 255, 255, 255))
                        .setButtonCallback(new LinearTimePickerDialog.ButtonCallback() {
                            @Override
                            public void onPositive(DialogInterface dialog, int hour, int minutes) {
                             //   Toast.makeText(getApplicationContext(), "" + hour + ":" + minutes, Toast.LENGTH_SHORT).show();
                                s_time = hour;
                                f_time = hours + s_time;

                                txtTimeTv.setText(hour+" O'Clock");
                            }

                            @Override
                            public void onNegative(DialogInterface dialog) {

                            }
                        })
                        .build();
                   dialog.show();
            }
        });

        setPrefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData();
            }
        });


    }


    private void postData()  {

        final ProgressDialog progressDialog = new ProgressDialog(LecPreferenceActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting preference... ");
        progressDialog.show();


        try {

            JSONObject postContent = new JSONObject();
            postContent.put("lecturer_id", lecturer_id);
            postContent.put("hours", hours);
            postContent.put("dept", dept);
            postContent.put("class_group", class_group);
            postContent.put("lesson", "lesson");
            postContent.put("intake", "sep_dec");
            postContent.put("unit_code", unit_code);
            postContent.put("day", day);
            postContent.put("s_time", s_time);
            postContent.put("f_time",f_time);

            String jsonString = postContent.toString();
            System.out.println(jsonString);
            Request request = Bridge
                    .post("http://192.168.43.233:8000/timetable/insertLecPreference")
                    .body(postContent)
                    .request();
            Response response = request.response();
            JSONObject responseJsonObject = response.asJsonObject();
            if (response.isSuccess()) {

                int success = 0;
                        try {
                            success = responseJsonObject.getInt("status");
                           String message = responseJsonObject.getString("message");
                            if(success == 1){
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Successfully inserted Preference ", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(),LecturerMainActivity.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                Toast.makeText(getApplicationContext(), "Successfully inserted Preference ", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Could not post preference(Error) ", Toast.LENGTH_SHORT).show();
            }
        } catch (BridgeException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
