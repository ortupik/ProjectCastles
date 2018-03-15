package com.slife.chris.studentlife.student;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ExamsActivity extends AppCompatActivity {

    private static final String TAG_DAY = "day";
    private static final String TAG_S_TIME = "s_time";
    private static final String TAG_F_TIME = "f_time";
    private static final String TAG_UNIT_CODE = "unit_code";
    private  static final String TAG_DATE = "date";
    private  static final String TAG_ROOM = "room";
    private static final String TAG_CLASS_GROUP = "class_group";
    public  static final String TAG_DEPT = "dept";
    public  static final String TAG_HOURS= "hours";
    public  static final String TAG_USERNAME = "username";

    private Socket mSocket;
    private PreferencesClass myPreferencesClass;
    private Spinner spinner;
    private ProgressDialog progressDialog,progressDialog2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_timetable);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Exam Timetable");
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle("Exam Timetable");

        myPreferencesClass = new PreferencesClass(getApplicationContext());


        spinner = (Spinner) findViewById(R.id.spinner);

        final String[] accessor = {"Week 0","Week 1","Week 2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, accessor);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getExamTimetable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        handleSocket();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public TableRow createTable(String day, JSONArray dayArray) throws JSONException {

        TableRow row = (TableRow) LayoutInflater.from(ExamsActivity.this).inflate(R.layout.table_row, null);
        row.removeAllViews();

        //set Day
        TextView dayTv = (TextView) LayoutInflater.from(ExamsActivity.this).inflate(R.layout.table_day_exam, null);

        row.addView(dayTv);


        int initTime = 10;
        int size = dayArray.length();

        for( int i = 0; i < 3; i++ ){

            TextView filledTv = (TextView) LayoutInflater.from(ExamsActivity.this).inflate(R.layout.table_def_filled_exam, null);
            row.addView(filledTv);
            dayTv.setText(day+"\n"+"\n");
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT


            for(int j = 0; j < size; j++) {

                JSONObject dayObj = dayArray.getJSONObject(j);
                dayTv.setText(day+"\n"+dayObj.getString(TAG_DATE)+"\n");

                if(dayObj.getString(TAG_F_TIME).equals(String.valueOf(initTime))){
                    filledTv.setBackground(getResources().getDrawable(R.drawable.table_def_shape));
                    int color = generator.getRandomColor();
                    filledTv.setBackgroundColor(color);
                    filledTv.setTextColor(getResources().getColor(R.color.white));
                    int hours = Integer.parseInt(dayObj.getString(TAG_HOURS));
                    //i+= hours-1;
                   // initTime+= hours-1;
                    System.out.println("init time "+initTime);
                    String username = dayObj.get(TAG_USERNAME).toString();
                    if(username.length() > 9){
                        username = username.substring(0,9);
                    }
                    String unitName = dayObj.getString(TAG_UNIT_CODE);
                    if(unitName.length() > 9){
                        unitName = unitName.substring(0,9);
                    }
                    String details = unitName+"\n"+username+"\n"+dayObj.get(TAG_ROOM);
                    filledTv.setText(details);
                }else{
                    String details = "\n\n";
                    filledTv.setText(details);
                    filledTv.setBackground(getResources().getDrawable(R.drawable.table_empty_def));

                }

            }
            initTime+=3;
        }


        return row;
    }
    private void handleSocket() {
        mSocket = SocketInstance.getSocket();
        mSocket.connect();

       /* progressDialog = new ProgressDialog(ExamsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving details ...");
        progressDialog.show();*/

        mSocket.on("viewExamTimetable", onGetExamTimeTable);

    }

    private void getExamTimetable() {
        int pos = spinner.getSelectedItemPosition();

        JSONObject credentials = new JSONObject();
        try {
            credentials.put("user_id",myPreferencesClass.getUserId());
            credentials.put("week",pos);
            mSocket.emit("viewExamTimetable",credentials);

            progressDialog2 = new ProgressDialog(ExamsActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog2.setIndeterminate(true);
            progressDialog2.setMessage("Details for week "+pos);
            progressDialog2.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("viewExamTimetable", onGetExamTimeTable);
        //    mSocket.disconnect();
    }

    private Emitter.Listener onGetExamTimeTable = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    Object json = null;
                    try {
                        json = new JSONTokener(args[0].toString()).nextValue();

                        if (json instanceof JSONArray) {
                            JSONArray data = (JSONArray) args[0];
                            System.out.println("Timetable " + data);

                            if (data == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Your Exam timetable data has not been uploaded Student Life  !!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {

                                TableLayout table = (TableLayout) ExamsActivity.this.findViewById(R.id.timetable);
                                table.removeAllViewsInLayout();
                                JSONArray timeTableArray = data;
                                try {
                                    table.addView(createTable("MON", timeTableArray.getJSONArray(0)));
                                    table.addView(createTable("TUE", timeTableArray.getJSONArray(1)));
                                    table.addView(createTable("WED", timeTableArray.getJSONArray(2)));
                                    table.addView(createTable("THUR", timeTableArray.getJSONArray(3)));
                                    table.addView(createTable("FRI", timeTableArray.getJSONArray(4)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                table.requestLayout();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog2.dismiss();
                                    }
                                });
                            }

                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }


                }
            });
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
