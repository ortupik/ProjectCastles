package com.slife.chris.studentlife.student;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.SwipeToRefreshListener;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomsEventsActivity extends AppCompatActivity {

    private Socket mSocket;
    private static final String TAG_DAY = "day";
    private static final String TAG_TIME = "s_time";
    private static final String TAG_F_TIME = "f_time";
    private static final String TAG_UNIT_CODE = "unit_code";
    private static final String TAG_CLASS_GROUP = "class_group";
    public  static final String TAG_DEPT = "dept";

    private  ArrayList<HashMap<String,String>> roomEList = new ArrayList<>();
    private  SortableRoomEventTableView roomTableView;
    private ProgressDialog progressDialog2;


    JSONArray roomsA = null;

    private String room,dept,classGroup;
    private  TableView<String[]> tableView;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_events);


        Bundle b = getIntent().getExtras();
        room = b.getString("room");
        dept = b.getString("dept");
        classGroup = b.getString("group");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(room+" Schedule");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(room+" Schedule");

          roomTableView = (SortableRoomEventTableView) findViewById(R.id.tableView);
        if (roomTableView != null) {

            roomTableView.addDataClickListener(new RoomEventClickListener());
            roomTableView.addDataLongClickListener(new REClickListener());
            roomTableView.setSwipeToRefreshEnabled(true);
            roomTableView.setSwipeToRefreshListener(new SwipeToRefreshListener() {
                @Override
                public void onRefresh(final RefreshIndicator refreshIndicator) {
                  //getRoomTimetable();
                }
            });
        }

        handleSocket();
    }
    private void handleSocket() {
        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        mSocket.on("roomTimetable", onGetRoomEvents);
        getRoomTimetable();
    }

    private void getRoomTimetable() {
        JSONObject details = new JSONObject();
        try {
            details.put("room",room);
            progressDialog2 = new ProgressDialog(RoomsEventsActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog2.setIndeterminate(true);
            progressDialog2.setMessage("Initializing...");
            progressDialog2.show();
            mSocket.emit("roomTimetable",details);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("roomTimetable", onGetRoomEvents);
        //    mSocket.disconnect();
    }
    private Emitter.Listener onGetRoomEvents = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                        new LoadRoomEvents().execute(data);
                }
            });
        }
    };

    class LoadRoomEvents extends AsyncTask<JSONArray, Void, Void> {

        @Override
        protected Void doInBackground(JSONArray... params) {

                roomsA = params[0];
               // System.out.println(roomsA);
            try {
                JSONArray monArr = roomsA.getJSONArray(0);
                JSONArray tueArr = roomsA.getJSONArray(1);
                JSONArray wedArr = roomsA.getJSONArray(2);
                JSONArray thurArr = roomsA.getJSONArray(3);
                JSONArray friArr = roomsA.getJSONArray(4);
                if(monArr!=null)setRoomList(monArr,1);
                if(tueArr!=null) setRoomList(tueArr,2);
                if(wedArr!=null) setRoomList(wedArr,3);
                if(thurArr!=null) setRoomList(thurArr,4);
                if(friArr!=null) setRoomList(friArr,5);
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
           pDialog = new ProgressDialog(RoomsEventsActivity.this);
            pDialog.setMessage("Loading Events Please wait...");
            pDialog.setIndeterminate(false);
           pDialog.show();

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(roomEList != null){
                final RoomEventsDataAdapter roomEventsDataAdapter = new RoomEventsDataAdapter(RoomsEventsActivity.this, roomEventList(roomEList));
                roomTableView.setDataAdapter(roomEventsDataAdapter);
                pDialog.dismiss();
        }
            progressDialog2.dismiss();

    }
    public void setRoomList(JSONArray roomsA,int index) throws JSONException {

        for (int i = 0; i < roomsA.length(); i++) {

            JSONObject object = roomsA.getJSONObject(i);
            // System.out.println(object);

            // Storing each json item in variable
            String day = object.getString(TAG_DAY);
            String s_time = object.getString(TAG_TIME);
            String f_time = object.getString(TAG_F_TIME);
            String unitCode = object.getString(TAG_UNIT_CODE);
            String dept = object.getString(TAG_DEPT);
            String group = object.getString(TAG_CLASS_GROUP);

            HashMap<String, String> map = new HashMap<>();
            map.put(TAG_DAY, day);
            map.put(TAG_TIME, s_time+" - "+f_time);
            map.put(TAG_CLASS_GROUP, dept+" "+group);
            map.put(TAG_UNIT_CODE, unitCode);
            roomEList.add(map);
        }
    }
    }
    public static List<RoomEventModel> roomEventList(ArrayList<HashMap<String, String>> roomList) {

        final List<RoomEventModel> roomEventModels = new ArrayList<>();

        if(roomList != null){

            int roomListSize =  roomList.size();
            for(int i = 0; i < roomListSize; i++){

                HashMap<String, String> map = roomList.get(i);

                String day = map.get(TAG_DAY);
                String group = map.get(TAG_CLASS_GROUP);
                String unit = map.get(TAG_UNIT_CODE);
                String time = map.get(TAG_TIME);

                RoomEventModel roomEventModel = new RoomEventModel(day, time, group,unit);
                roomEventModels.add(roomEventModel);
            }
            return roomEventModels;
        }else{
            return null;
        }

    }

    private class RoomEventClickListener implements TableDataClickListener<RoomEventModel> {

        @Override
        public void onDataClicked(final int rowIndex, final RoomEventModel clickedData) {
            final String carString = "Click: " + clickedData.getDay() + " " + clickedData.getS_time();
            Toast.makeText(RoomsEventsActivity.this, carString, Toast.LENGTH_SHORT).show();
        }
    }

    private class REClickListener implements TableDataLongClickListener<RoomEventModel> {

        @Override
        public boolean onDataLongClicked(final int rowIndex, final RoomEventModel clickedData) {
            final String carString = "Long Click: " + clickedData.getDay()+ " " + clickedData.getClass_group();
            Toast.makeText(RoomsEventsActivity.this, carString, Toast.LENGTH_SHORT).show();
            return true;
        }
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
