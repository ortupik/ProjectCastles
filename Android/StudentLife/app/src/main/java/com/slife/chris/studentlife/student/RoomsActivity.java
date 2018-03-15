package com.slife.chris.studentlife.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Socket mSocket;

    private static final String TAG_ROOM = "room";
    private static final String TAG_CLASS_NOW = "classNow";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_DEPT = "dept";
    private static final String TAG_GROUP = "group";
    private static final String TAG_UNIT_CODE = "unit_code";
    private TextView filledTv;
    private Calendar cal = new GregorianCalendar();
    private  int isFirstTime = 0;

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> futureTask;
    private Runnable myTask;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Class Rooms");
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Class Rooms");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Snackbar.make(mViewPager, "Click a room to get more info", 3000)
                .setAction("OK", null).show();

        final int currentMin = cal.get(Calendar.MINUTE);
        final int currSeconds = cal.get(Calendar.SECOND);
        int remainMin = 60 - currentMin;
        final int remainSec =  ((remainMin*60) - currSeconds);

        scheduledExecutorService = Executors.newScheduledThreadPool(0);
        myTask = new Runnable(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                      //  Toast.makeText(RoomsActivity.this, "current sec "+currSeconds+" remainig "+remainSec, Toast.LENGTH_SHORT).show();
                      //  getRoomInfo();
                    }
                });
            }
        };


        changeReadInterval(remainSec);
        handleSocket();
        getRoomInfo();

    }
    public void changeReadInterval(long time)
    {
        if(time > 0){
            if (futureTask != null){
                futureTask.cancel(true);
            }
            futureTask = scheduledExecutorService.scheduleAtFixedRate(myTask, 0, time, TimeUnit.SECONDS);
        }
    }



    private void handleSocket() {

        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        //remove this login herre

        mSocket.on("getClassRooms", onGetClassRooms);

    }

    private void getRoomInfo() {

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(isFirstTime == 0){
            isFirstTime++;
        }else{
            hour++;
        }
        //Toast.makeText(RoomsActivity.this, "current hour "+hour, Toast.LENGTH_SHORT).show();
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("currentTime",hour);//
            credentials.put("day",getDay(cal.get(Calendar.DAY_OF_WEEK)));//
            mSocket.emit("getClassRooms",credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(RoomsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving details...");
        progressDialog.show();
    }

    public String getDay(int day_of_weeek) {
        switch (day_of_weeek) {
            case 1:
                return "SUN";
            case 2:
                return "MON";
            case 3:
                return "TUE";
            case 4:
                return "WED";
            case 5:
                return "THUR";
            case 6:
                return "FRI";
            case 7:
                return "SAT";
            default:
                return "";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("getClassRooms", onGetClassRooms);
        //    mSocket.disconnect();
    }

    public void  createClassTable( JSONArray classArray) throws JSONException {

        TableLayout table = (TableLayout)RoomsActivity.this.findViewById(R.id.classTable);
        table.removeAllViews();

        TableRow row = null;
        String room = "";
        String dept = "";
        String group = "";

        int size = classArray.length();
        ColorGenerator generator = ColorGenerator.DEFAULT; // or use DEFAULT


            for(int j = 0; j < size; j++) {
                if(j%4 == 0){
                    row = (TableRow) LayoutInflater.from(RoomsActivity.this).inflate(R.layout.table_row, null);
                    table.addView(row);
                }
                 filledTv = (TextView) LayoutInflater.from(RoomsActivity.this).inflate(R.layout.room_table, null);
                row.addView(filledTv);

                final JSONObject classObj = classArray.getJSONObject(j);

                room = classObj.getString(TAG_ROOM);

                if(classObj.getString(TAG_CLASS_NOW).equals("1")){

                    dept = classObj.getString(TAG_DEPT);
                    group = classObj.getString(TAG_GROUP);

                    StringBuilder sb = new StringBuilder();
                     sb.append(group.charAt(1));
                    sb.append(".");
                    sb.append(group.charAt(3));

                    filledTv.setTextColor(Color.WHITE);
                    filledTv.setBackground(getResources().getDrawable(R.drawable.room_filled));
                    int color = generator.getRandomColor();
                    filledTv.setBackgroundColor(color);
                    String details = room+"\n"+dept+" "+sb.toString();
                    filledTv.setText(details);
                }else{
                    String details = room+"\nFREE";
                    filledTv.setText(details);
                    filledTv.setBackground(getResources().getDrawable(R.drawable.room_empty_def));
                    filledTv.setTextColor(Color.BLACK);
                }


                final String finalRoom = room;
                final String finalGroup = group;
                final String finalDept = dept;

                filledTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),RoomsEventsActivity.class);
                        i.putExtra("room", finalRoom);
                        i.putExtra("dept", finalDept);
                        i.putExtra("group", finalGroup);
                        startActivity(i);
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
    private Emitter.Listener onGetClassRooms = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Object json = null;
                    try {
                        json = new JSONTokener(args[0].toString()).nextValue();

                        if(json instanceof JSONArray){
                            JSONArray data = (JSONArray) args[0];
                            System.out.println("Class rooms "+data);
                            JSONArray classTimeTableArray = data;
                            createClassTable(classTimeTableArray);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rooms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View view = inflater.inflate(R.layout.fragment_rooms, container, false);
            final Calendar cal = new GregorianCalendar();
            final TextView nowDate = (TextView) view.findViewById(R.id.now_date);
            final int hour = cal.get(Calendar.HOUR_OF_DAY);
            final int nextHour = hour;

            int currentMin = cal.get(Calendar.MINUTE);
            int currSeconds = cal.get(Calendar.SECOND);
            int remainMin = 60 - currentMin;
            int remainSec =  (remainMin*60) - currSeconds;

            ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // Toast.makeText(getActivity(), "updated !", Toast.LENGTH_SHORT).show();
                            final Calendar cal = new GregorianCalendar();
                            final TextView nowDate = (TextView) view.findViewById(R.id.now_date);
                            final int hour = cal.get(Calendar.HOUR_OF_DAY);
                            final int nextHour = hour+1;
                            nowDate.setText(new RoomsActivity().getDay(cal.get(Calendar.DAY_OF_WEEK)) +"  "+ hour+ " - "+nextHour);
                        }
                    });

                }
            }, 0, remainSec, TimeUnit.SECONDS);
            //nowDate.setText(new RoomsActivity().getDay(cal.get(Calendar.DAY_OF_WEEK)) +"  "+ hour+ " - "+nextHour);
            return view;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";

            }
            return null;
        }
    }

}
