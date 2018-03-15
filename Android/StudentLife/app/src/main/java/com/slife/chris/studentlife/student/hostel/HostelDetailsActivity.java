package com.slife.chris.studentlife.student.hostel;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.Constants;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HostelDetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener  {

    private static final String TAG_ROOM_PRICE = "roomPrice";
    private static final String TAG_ROOM_TYPE = "roomType";

    private RecyclerView roomDetailsRv;
    private SliderLayout mDemoSlider;
    private HostelDetailsAdapter viewHostelAdapter;
    private ArrayList<HostelDetailsStructure> hostelArrayList = new ArrayList<>();
    private Socket mSocket;
    private PreferencesClass myPreferencesClass;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Greens Hostels");
       toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setBackgroundResource(R.drawable.about_img);
        setSupportActionBar(toolbar);

        roomDetailsRv = (RecyclerView) findViewById(R.id.ll_room_details_rv);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        handleSocket();


    }
    public void handleSlider(JSONArray jsonArray){

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        HashMap<String,String> file_maps = new HashMap<String, String>();

        try {
            System.out.println(jsonArray);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);
                // System.out.println(object);
                if (object != null) {
                    // Storing each json item in variable
                    String imgPath = object.getString("img_path");
                    String description = object.getString("description");
                    file_maps.put(description, Constants.HOSTEL_URL+imgPath);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("q Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}


    private void handleSocket() {
        mSocket = SocketInstance.getSocket();
        myPreferencesClass = new PreferencesClass(getApplicationContext());

        mSocket.connect();
        //remove this login herre
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("user_id", myPreferencesClass.getUserId());
            credentials.put("hostel_id",1);
            mSocket.emit("getHostelRooms", credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("getHostelRooms", ongetHostelRooms);
        progressDialog = new ProgressDialog(HostelDetailsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving Rooms ...");
        progressDialog.show();

    }

    private Emitter.Listener ongetHostelRooms = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    Object json = null;
                    try {
                        json = new JSONTokener(args[0].toString()).nextValue();

                        if (json instanceof JSONObject) {

                            JSONObject obj = (JSONObject) args[0];
                            JSONArray data = obj.getJSONArray("roomsArray");
                            final JSONArray data2 = obj.getJSONArray("detailsArray");

                           // System.out.println("Hostels " + data);


                            if (data == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "No Room data has been uploaded on Student Life  !!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {

                                new LoadAllHostelRooms().execute(data);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressDialog.dismiss();
                                    }
                                });
                            }
                            if (data2 == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "No Uploaded Images !!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleSlider(data2);
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

    class LoadAllHostelRooms extends AsyncTask<JSONArray, Void, Void> {


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
                        String roomType = object.getString(TAG_ROOM_TYPE);
                        String roomPrice = object.getString(TAG_ROOM_PRICE);

                        HostelDetailsStructure hostelDetailsStructure = new HostelDetailsStructure();
                        hostelDetailsStructure.setRoomType(roomType);
                        hostelDetailsStructure.setRoomPrice(roomPrice);
                        hostelArrayList.add(hostelDetailsStructure);
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
        roomDetailsRv.setLayoutManager(new LinearLayoutManager(roomDetailsRv.getContext()));
        viewHostelAdapter = new HostelDetailsAdapter(getApplicationContext(), hostelArrayList);
        roomDetailsRv.setAdapter(viewHostelAdapter);
    }

}
