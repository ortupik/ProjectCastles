package com.slife.chris.studentlife.student.hostel;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.Toast;

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

public class ViewHostelsActivity extends AppCompatActivity {

    private static final String TAG_HOSTEL_NAME = "hostelName";
    private static final String TAG_HOSTEL_DISTANCE = "hostelDistance";
    private static final String TAG_RATINGS = "ratings";
    private RecyclerView roomDetailsRV;
    private CardView hostelRoomListLL;
    private ViewHostelAdapter viewHostelAdapter;
    private ArrayList<HostelStructure> hostelArrayList = new ArrayList<>();
    private Socket mSocket;
    private PreferencesClass myPreferencesClass;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hostels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All Hostels");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setBackgroundResource(R.drawable.about_img);
        setSupportActionBar(toolbar);

        roomDetailsRV = (RecyclerView) findViewById(R.id.content_Rv);
        hostelRoomListLL = (CardView) LayoutInflater.from(ViewHostelsActivity.this).inflate(R.layout.hostel_list_item, null);

        handleSocket();

    }

    private void handleSocket() {
        mSocket = SocketInstance.getSocket();
        myPreferencesClass = new PreferencesClass(getApplicationContext());

        mSocket.connect();
        //remove this login herre
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("user_id", myPreferencesClass.getUserId());
            mSocket.emit("getHostels", credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("getHostels", ongetHostels);
        progressDialog = new ProgressDialog(ViewHostelsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving Hostels ...");
        progressDialog.show();

    }

    private Emitter.Listener ongetHostels = new Emitter.Listener() {
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
                            System.out.println("Hostels " + data);

                            if (data == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "No hostel data has been uploaded on Student Life  !!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {

                                new LoadAllHostels().execute(data);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressDialog.dismiss();
                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };

    class LoadAllHostels extends AsyncTask<JSONArray, Void, Void> {

        @Override
        protected Void doInBackground(JSONArray... params) {

            try {

                JSONArray jsonArray = params[0];
                System.out.println(jsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    // System.out.println(object);
                    if (object != null) {
                        // Storing each json item in variable
                        String hostelName = object.getString(TAG_HOSTEL_NAME);
                        String hostelDistance = object.getString(TAG_HOSTEL_DISTANCE);
                        String ratings = object.getString(TAG_RATINGS);

                        HostelStructure hostelStructure = new HostelStructure();
                        hostelStructure.setHostelName(hostelName);
                        hostelStructure.setHostelDistance(hostelDistance + " From Resource Center");
                        hostelStructure.setRatings(String.valueOf(ratings));

                        hostelArrayList.add(hostelStructure);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Contacts Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
       //     pDialog.show();*/
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            setHostelData();
            // pDialog.dismiss();
        }

    }

    private void setHostelData() {
        roomDetailsRV.setLayoutManager(new LinearLayoutManager(roomDetailsRV.getContext()));
        viewHostelAdapter = new ViewHostelAdapter(getApplicationContext(), hostelArrayList);
        roomDetailsRV.setAdapter(viewHostelAdapter);
    }
}

