package com.slife.chris.studentlife.student;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TimetableActivity extends AppCompatActivity {

    private static final String TAG_DAY = "day";
    private static final String TAG_S_TIME = "s_time";
    private static final String TAG_F_TIME = "f_time";
    private static final String TAG_UNIT_CODE = "unit_code";
    private  static final String TAG_INTAKE = "intake";
    private  static final String TAG_ROOM = "room";
    private static final String TAG_CLASS_GROUP = "class_group";
    public  static final String TAG_DEPT = "dept";
    public  static final String TAG_HOURS= "hours";
    public  static final String TAG_USERNAME = "username";
    private static ArrayList<Integer> colorsList = new ArrayList<>();

    private Socket mSocket;
    private PreferencesClass myPreferencesClass;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);



        myPreferencesClass = new PreferencesClass(getApplicationContext());
        handleSocket();


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public TableRow createTable(String day, JSONArray dayArray) throws JSONException {

        TableRow row = (TableRow) LayoutInflater.from(TimetableActivity.this).inflate(R.layout.table_row, null);

        //set Day
        TextView dayTv = (TextView) LayoutInflater.from(TimetableActivity.this).inflate(R.layout.table_def_filled, null);
        dayTv.setText("\n"+day+"\n");
        row.addView(dayTv);


        int initTime = 7;
        int size = dayArray.length();
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getRandomColor();

        for( int i = 0; i < 12; i++ ){

            TextView filledTv = (TextView) LayoutInflater.from(TimetableActivity.this).inflate(R.layout.table_def_filled, null);
            row.addView(filledTv);
            for(int j = 0; j < size; j++) {
                JSONObject dayObj = dayArray.getJSONObject(j);
                if(dayObj.getString(TAG_S_TIME).equals(String.valueOf(initTime))){
                    filledTv.setBackground(getResources().getDrawable(R.drawable.table_def_shape));
                    /*color = generator.getColor(android.R.color.holo_blue_dark);
                    filledTv.setBackgroundColor(color);
                    filledTv.setTextColor(getResources().getColor(R.color.white));*/
                    int hours = Integer.parseInt(dayObj.getString(TAG_HOURS));
                    i+= hours-1;
                    initTime+= hours-1;

                    String details = dayObj.get(TAG_UNIT_CODE)+"\n"+dayObj.get(TAG_USERNAME)+"\n"+dayObj.get(TAG_ROOM);
                    filledTv.setText(details);
                    TableRow.LayoutParams params = (TableRow.LayoutParams) filledTv.getLayoutParams();
                    params.span = hours;
                    filledTv.setLayoutParams(params);
                    break;
                }else{
                    String details = " \n \n";
                    filledTv.setText(details);
                    filledTv.setBackground(getResources().getDrawable(R.drawable.table_empty_def));
                }

            }
            initTime++;
        }


        return row;
    }
    private void handleSocket() {
        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        //remove this login herre
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("user_id",myPreferencesClass.getUserId());
            mSocket.emit("viewTimetable",credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("viewTimetable", onGetTimeTable);
        progressDialog = new ProgressDialog(TimetableActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving details ...");
        progressDialog.show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("viewTimetable", onGetTimeTable);
        //    mSocket.disconnect();
    }

    private Emitter.Listener onGetTimeTable = new Emitter.Listener() {
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
                                        Toast.makeText(getApplicationContext(), "Your class timetable data has not been uploaded Student Life  !!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {

                                TableLayout table = (TableLayout) TimetableActivity.this.findViewById(R.id.timetable);
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
                                        progressDialog.dismiss();
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
