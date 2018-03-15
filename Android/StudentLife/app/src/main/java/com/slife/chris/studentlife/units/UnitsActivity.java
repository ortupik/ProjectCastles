package com.slife.chris.studentlife.units;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 */
public class UnitsActivity extends AppCompatActivity {


    private static final String TAG_NAME = "name";
    private static final String TAG_COURSE = "course";
    private static final String TAG_UNIT_ID = "unitId";
    private static final String TAG_LECTURER = "lecturerId";
    private static final String TAG_YEAR = "year";
    private String dept,classGroup;


    JSONArray units = null;
    ArrayList<HashMap<String, String>> unitsList = new ArrayList<>();
    private ProgressDialog progressDialog2;

    private int currentPage = 0;
    private View rootView;
    private ListView listView;
    private int indicatorBgColor;
    private int indicatorColor;
    private PageTransformerTypes pageTransformerType;

    private Socket mSocket;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_units);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Greens Hostels");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setBackgroundResource(R.drawable.about_img);

        indicatorColor = 0xfff06292;
        pageTransformerType = PageTransformerTypes.SCALE;


        mSocket = SocketInstance.getSocket();
        mSocket.connect();



        progressDialog2 = new ProgressDialog(UnitsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Initializing...");
        progressDialog2.show();

        mSocket.on("getUnits", onGetUnits);

        initPagedHeadList();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //mSocket.off("getUnits", onGetUnits);

    }

    private void initPagedHeadList() {



        StudentDb studentDb = new StudentDb(getApplicationContext());
        try {
            studentDb.open();
            ArrayList<HashMap<String,String>> studentData = studentDb.getStudentData();
            dept = studentData.get(0).get("dept");
            classGroup = studentData.get(0).get("classGroup");

            JSONObject credentials = new JSONObject();
            credentials.put("dept",dept);//
            credentials.put("class_group",classGroup);//
            mSocket.emit("getUnits",credentials);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        studentDb.close();

    }


    private Emitter.Listener onGetUnits = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    new LoadAllUnits().execute(data);


                }
            });
        }
    };

    class LoadAllUnits extends AsyncTask<JSONArray, Void, Void> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Void doInBackground(JSONArray... params) {

            try {

                      units = params[0];

                    // looping through All units
                    for (int i = 0; i < units.length(); i++) {
                        JSONObject c = units.getJSONObject(i);

                        // Storing each json item in variable
                        String name = c.getString(TAG_NAME);
                        String unitId = c.getString(TAG_UNIT_ID);
                        String lecturer = c.getString(TAG_LECTURER);
                        String course = c.getString(TAG_COURSE);
                        String year = c.getString(TAG_YEAR);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_NAME, name);
                        map.put(TAG_UNIT_ID, unitId);
                        map.put(TAG_LECTURER, lecturer);
                        map.put(TAG_COURSE, course);
                        map.put(TAG_YEAR, year);

                        // adding HashList to ArrayList
                        unitsList.add(map);
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ArrayList<UnitsStructure> unitsStructures = GetUnits(unitsList);
            listView.setAdapter(new LecturerUnitsAdapter(getApplicationContext(), unitsStructures));

            progressDialog2.dismiss();

        }

    }

    private ArrayList<UnitsStructure> GetUnits(ArrayList<HashMap<String, String>> unitsList) {
        ArrayList<UnitsStructure> unitsDetails = new ArrayList<>();

        for(int i = 0; i < unitsList.size(); i++){

            HashMap<String, String> map = unitsList.get(i);

            UnitsStructure c1 = new UnitsStructure();
            c1.setName(map.get(TAG_NAME));
            c1.setUnitCode(map.get(TAG_UNIT_ID));
            c1.setLecturerName(map.get(TAG_LECTURER));
            c1.setCourse(map.get(TAG_COURSE));
            c1.setGroup(map.get(TAG_YEAR));
            unitsDetails.add(c1);
        }



        return unitsDetails;
    }
}
