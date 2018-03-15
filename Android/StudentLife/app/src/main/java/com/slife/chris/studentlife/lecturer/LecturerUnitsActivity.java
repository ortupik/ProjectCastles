package com.slife.chris.studentlife.lecturer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LecturerUnitsActivity extends AppCompatActivity {

    private Socket mSocket;
    private ListView unitsListView;
    private static final String TAG_NAME = "name";
    private static final String TAG_COURSE = "dept";
    private static final String TAG_GROUP = "class_group";
    private static final String TAG_UNIT_CODE = "unit_code";
    private static final String TAG_HOURS = "hours";
    private static final String TAG_STATUS = "status";

    JSONArray units = null;
    private PreferencesClass myPrefences;

    ArrayList<HashMap<String, String>> unitsList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String  status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_units);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        unitsListView = (ListView) findViewById(R.id.units_list_view);
        myPrefences = new PreferencesClass(this);

        Bundle b = getIntent().getExtras();
        status = b.getString("status");
       // status



        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        mSocket.on("getLecUnits", onGetUnits);

        progressDialog = new ProgressDialog(LecturerUnitsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving units ...");
        progressDialog.show();

        try {

            JSONObject credentials = new JSONObject();
            credentials.put("type",status);
            credentials.put("user_id",3);
            mSocket.emit("getLecUnits",credentials);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("getLecUnits", onGetUnits);

    }
    private Emitter.Listener onGetUnits = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                  //  Toast.makeText(getApplicationContext(),data.toString(),Toast.LENGTH_SHORT).show();
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
                    String unitCode = c.getString(TAG_UNIT_CODE);
                    String course = c.getString(TAG_COURSE);
                    String classGroup = c.getString(TAG_GROUP);
                    String hours = c.getString(TAG_HOURS);


                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_NAME, name);
                    map.put(TAG_UNIT_CODE, unitCode);
                    map.put(TAG_COURSE, course);
                    map.put(TAG_GROUP, classGroup);
                    map.put(TAG_HOURS, hours);
                    if(status.equals("set") ){
                        map.put(TAG_STATUS,c.getString(TAG_STATUS) );
                    }else{
                        map.put(TAG_STATUS, status);
                    }

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




           // System.out.println(unitsList);
            if(unitsList != null){
                ArrayList<LecturerUnitsStructure> unitsStructures = GetUnits(unitsList);
                unitsListView.setAdapter(new LecturerUnitsAdapter(getApplicationContext(), unitsStructures));
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Units have not been added",Toast.LENGTH_LONG).show();
                    }
                });
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });

        }

    }

    private ArrayList<LecturerUnitsStructure> GetUnits(ArrayList<HashMap<String, String>> unitsList) {
        ArrayList<LecturerUnitsStructure> unitsDetails = new ArrayList<>();

        for(int i = 0; i < unitsList.size(); i++){

            HashMap<String, String> map = unitsList.get(i);

            LecturerUnitsStructure c1 = new LecturerUnitsStructure();
            c1.setName(map.get(TAG_NAME));
            c1.setUnitCode(map.get(TAG_UNIT_CODE));
            c1.setCourse(map.get(TAG_COURSE));
            c1.setGroup(map.get(TAG_GROUP));
            c1.setHours(map.get(TAG_HOURS));
            c1.setStatus(map.get(TAG_STATUS));
            unitsDetails.add(c1);

        }


       // System.out.println(unitsDetails);
        return unitsDetails;
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
