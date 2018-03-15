package com.slife.chris.studentlife.units;

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

public class MyUnitsActivity extends AppCompatActivity {

    private Socket mSocket;
    private ListView unitsListView;
    private static final String TAG_NAME = "name";
    private static final String TAG_COURSE = "dept";
    private static final String TAG_GROUP = "class_group";
    private static final String TAG_UNIT_CODE = "unit_code";
    private static final String TAG_LECTURER_FNAME = "fname";
    private static final String TAG_LECTURER_LNAME = "lname";

    JSONArray units = null;
    private PreferencesClass myPrefences;

    ArrayList<HashMap<String, String>> unitsList = new ArrayList<>();
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_units);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        unitsListView = (ListView) findViewById(R.id.units_list_view);
        myPrefences = new PreferencesClass(this);


        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        mSocket.on("getUnits", onGetUnits);

        progressDialog = new ProgressDialog(MyUnitsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving units ...");
        progressDialog.show();

        try {

            JSONObject credentials = new JSONObject();
            credentials.put("user_id",myPrefences.getUserId());
            mSocket.emit("getUnits",credentials);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("getUnits", onGetUnits);

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
                    String unitCode = c.getString(TAG_UNIT_CODE);
                    String lecturer_Fname = c.getString(TAG_LECTURER_FNAME);
                    String lecturer_Lname = c.getString(TAG_LECTURER_LNAME);
                    String course = c.getString(TAG_COURSE);
                    String classGroup = c.getString(TAG_GROUP);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_NAME, name);
                    map.put(TAG_UNIT_CODE, unitCode);
                    map.put(TAG_LECTURER_FNAME, lecturer_Fname+" "+lecturer_Lname);
                    map.put(TAG_COURSE, course);
                    map.put(TAG_GROUP, classGroup);

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




           // System.out.println(unitsList);
            if(unitsList != null){
                ArrayList<UnitsStructure> unitsStructures = GetUnits(unitsList);
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

    private ArrayList<UnitsStructure> GetUnits(ArrayList<HashMap<String, String>> unitsList) {
        ArrayList<UnitsStructure> unitsDetails = new ArrayList<>();

        for(int i = 0; i < unitsList.size(); i++){

            HashMap<String, String> map = unitsList.get(i);

            UnitsStructure c1 = new UnitsStructure();
            c1.setName(map.get(TAG_NAME));
            c1.setUnitCode(map.get(TAG_UNIT_CODE));
            c1.setLecturerName(map.get(TAG_LECTURER_FNAME));
            c1.setCourse(map.get(TAG_COURSE));
            c1.setGroup(map.get(TAG_GROUP));
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
