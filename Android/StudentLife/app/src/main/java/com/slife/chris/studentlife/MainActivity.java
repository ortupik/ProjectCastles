package com.slife.chris.studentlife;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.slife.chris.studentlife.aunthetication.Login;
import com.slife.chris.studentlife.aunthetication.LoginActivity;
import com.slife.chris.studentlife.aunthetication.SignupProfileActivity;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.database.UserDb;
import com.slife.chris.studentlife.student.CastleUtilities;
import com.slife.chris.studentlife.student.StudentRegistration;
import com.slife.chris.studentlife.student.hostel.ViewHostelsActivity;
import com.slife.chris.studentlife.utilities.About;
import com.slife.chris.studentlife.utilities.Constants;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.QRActivity;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private PreferencesClass myPrefences;
    private String dept,classGroup,regNo,phoneNo,imgPath,username,status;
    private ProgressDialog progressDialog;
    private Socket mSocket;
    private TextView usernameTv,phoneTv,groupPopTv,castlePopTv;
    private CircleImageView profileImage;
    //USER DETAILS
    private  static final String KEY_USERNAME= "username";
    private  static final String KEY_PHONE ="phoneNo";
    private  static final String KEY_PROFILE_PHOTO = "profile_photo";
    private  static final String KEY_STATUS = "status";

    private static ArrayList<HashMap<String,String>> userData;
    private TextDrawable.IBuilder mDrawableBuilder1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        myPrefences = new PreferencesClass(MainActivity.this);
         if (TextUtils.isEmpty(myPrefences.getUserId()) &&
                TextUtils.isEmpty(myPrefences.getPhoneNo())&&
                TextUtils.isEmpty(myPrefences.getRegistrationStatus())){

            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish();

        }else if(!TextUtils.isEmpty(myPrefences.getUserId()) &&
                !TextUtils.isEmpty(myPrefences.getPhoneNo())&&
                TextUtils.isEmpty(myPrefences.getRegistrationStatus())){

            Toast.makeText(getApplicationContext(), "Please Complete Profile !!  ", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(),SignupProfileActivity.class);
            startActivity(i);
            finish();
        }else {

            mDrawableBuilder1 = TextDrawable.builder().round();

            mSocket = SocketInstance.getSocket();
            mSocket.connect();
            mSocket.on("getDekutCastleDetails", onGetDekutDetails);
            mSocket.on("autoLogin", onAutoLogIn);

            myPrefences.storeLoginStatus(getApplicationContext(), "logged in");// remove it
            new Login(getApplicationContext());

            checkConnection();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View hView = navigationView.getHeaderView(0);
            usernameTv = (TextView) hView.findViewById(R.id.username);
            phoneTv = (TextView) hView.findViewById(R.id.phoneNo);
            profileImage = (CircleImageView) hView.findViewById(R.id.imgAvatar);

            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                MenuItem item = navigationView.getMenu().getItem(i);

                SpannableString spannableString = new SpannableString(item.getTitle());
                spannableString.setSpan(Constants.getRobotoMedium(getApplicationContext()), 0, spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                item.setTitle(spannableString);
            }
            MenuItem socialItem = navigationView.getMenu().getItem(0);
            MenuItem studentItem = navigationView.getMenu().getItem(1);
            MenuItem webItem = navigationView.getMenu().getItem(2);


            UserDb userDbDb = new UserDb(getApplicationContext());
            try {
                userDbDb.open();
                userData = userDbDb.getUserData();
                phoneNo = userData.get(0).get(KEY_PHONE);
                username = userData.get(0).get(KEY_USERNAME);
                imgPath = userData.get(0).get(KEY_PROFILE_PHOTO);
                status = userData.get(0).get(KEY_STATUS);

                usernameTv.setText(username);
                usernameTv.setTypeface(Constants.getRobotoBold(getApplicationContext()));
                String newPhone = "+254" + phoneNo.substring(1, phoneNo.length());
                phoneNo = newPhone;
                phoneTv.setText(phoneNo);
                phoneTv.setTypeface(Constants.getRobotoLight(getApplicationContext()));

                Glide.with(profileImage.getContext())
                        .load(Constants.IMG_URL + imgPath)
                        .fitCenter()
                        .into(profileImage);

                userDbDb.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.content_main);
            LinearLayout joinCastleView = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.join_castle, null);
            LinearLayout groupView = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.group_view, null);
            LinearLayout castleView = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.castle_card_view, null);
            LinearLayout myView = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.me_view, null);

            if (TextUtils.isEmpty(myPrefences.getStudentRegStatus())) {
                socialItem.setVisible(false);
                studentItem.setVisible(false);
                webItem.setVisible(false);
                mainLayout.addView(joinCastleView);
                AppCompatButton joinBtn = (AppCompatButton) findViewById(R.id.join_castle);
                joinBtn.setOnClickListener(this);
            } else {
                if (!TextUtils.isEmpty(myPrefences.getStudentRegStatus())) {
                    socialItem.setVisible(true);
                    studentItem.setVisible(true);
                    webItem.setVisible(true);
                    setupCastle(mainLayout, groupView, castleView);
                    setMyView(mainLayout, myView);
                } else {
                    //redudant really
                    socialItem.setVisible(false);
                    studentItem.setVisible(false);
                    webItem.setVisible(false);
                    Intent intent = new Intent(MainActivity.this, StudentRegistration.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            }


        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!TextUtils.isEmpty(myPrefences.getStudentRegStatus())) {
                getDetails();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(myPrefences.getStudentRegStatus())) {
                getDetails();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void checkConnection() {

        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(getApplicationContext(),"Disconnected to Server! ",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mSocket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Reconnected to Server! ",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setMyView(LinearLayout mainLayout, LinearLayout myView) {
        mainLayout.addView(myView);
        TextView phoneTv = (TextView) findViewById(R.id.phoneNo_tv);
        TextView statusTv = (TextView) findViewById(R.id.status_tv);
        TextView usernameTv = (TextView) findViewById(R.id.username_tv);
       CircleImageView myImage = (CircleImageView) findViewById(R.id.my_image);


        phoneTv.setTypeface(Constants.getRobotoLight(getApplicationContext()));
        statusTv.setTypeface(Constants.getRobotoLight(getApplicationContext()));
        usernameTv.setTypeface(Constants.getRobotoBold(getApplicationContext()));

        phoneTv.setText(phoneNo);
        statusTv.setText("\""+status+"\"");
        usernameTv.setText(username);

        Glide.with(myImage.getContext())
                .load(Constants.IMG_URL+imgPath)
                .fitCenter()
                .into(myImage);

    }


    private void setupCastle(LinearLayout mainLayout, LinearLayout groupView, LinearLayout castleView) {
        mainLayout.addView(castleView);
        mainLayout.addView(groupView);
        showLoading();

        AppCompatButton socialBtn = (AppCompatButton) findViewById(R.id.social_btn);
        socialBtn.setOnClickListener(this);
        AppCompatButton utilitiesBtn = (AppCompatButton) findViewById(R.id.utilities_btn);
        utilitiesBtn.setOnClickListener(this);

        TextView classGroupTv = (TextView) findViewById(R.id.class_group);
        ImageView groupImageView = (ImageView) findViewById(R.id.group_image);

        groupPopTv = (TextView) findViewById(R.id.group_pop);
        castlePopTv = (TextView) findViewById(R.id.castle_pop);


        StudentDb studentDb = new StudentDb(getApplicationContext());
        try {
            studentDb.open();
            ArrayList<HashMap<String,String>> studentData = studentDb.getStudentData();
                dept = studentData.get(0).get("dept");
                classGroup = studentData.get(0).get("classGroup");
                regNo =  studentData.get(0).get("regNo");

           // Toast.makeText(getApplicationContext(),"dept "+dept,Toast.LENGTH_LONG).show();
                classGroupTv.setText(classGroup);

                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                int color = generator.getRandomColor();
                     String initials = dept;
                    if(dept.length()> 2){
                         initials = dept.substring(0,3).toUpperCase();
                    }

                TextDrawable drawable = mDrawableBuilder1.build(initials,color);
                groupImageView.setImageDrawable(drawable);

                if(myPrefences.getGroupPop() != 0 && myPrefences.getCastlePop() !=0){
                     int groupPop = myPrefences.getGroupPop();
                    int castlePop = myPrefences.getCastlePop();

                    String groupInfo,castleInfo;

                    if(groupPop > 1)
                        groupInfo = groupPop +" members";
                    else
                        groupInfo = groupPop +" member";

                    if(castlePop > 1)
                        castleInfo = castlePop +" members";
                    else
                        castleInfo = castlePop +" member";

                    groupPopTv.setText(groupInfo);
                    castlePopTv.setText(castleInfo);

                }else {

                    getDetails();
                }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        studentDb.close();

    }

    private void getDetails() {
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("user_id", myPrefences.getUserId());
            credentials.put("castle_id", 3);
            credentials.put("dept", dept);
            credentials.put("class_group", classGroup);

            mSocket.emit("getDekutCastleDetails", credentials);
            showLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private  Emitter.Listener onAutoLogIn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject data = (JSONObject) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Auto login ",Toast.LENGTH_SHORT).show();
                    new Login(getApplicationContext());
                }
            });

        }
    };
    private Emitter.Listener onGetDekutDetails = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        int groupPop = data.getInt("group_pop");
                        int castlePop = data.getInt("castle_pop");

                       // if(myPrefences.getCastlePop()== 0)
                            myPrefences.storeCastlePop(getApplicationContext(),castlePop);
                       // if(myPrefences.getGroupPop()== 0)
                            myPrefences.storeGroupPop(getApplicationContext(),groupPop);

                        String groupInfo,castleInfo;

                        if(groupPop > 1)
                             groupInfo = groupPop +" members";
                        else
                            groupInfo = groupPop +" member";

                        if(castlePop > 1)
                            castleInfo = castlePop +" members";
                        else
                            castleInfo = castlePop +" member";

                        mSocket.emit("people",data,new Emitter.Listener(){
                            @Override
                            public void call(Object... args) {

                            }
                        });



                       if(groupPopTv!=null) groupPopTv.setText(groupInfo);
                       if(castlePopTv!=null) castlePopTv.setText(castleInfo);
                        dismissLoading();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Retrieving details ..");
        }
        // progressDialog.setCancelable(false);
       // progressDialog.show();
    }

    public void dismissLoading() {
       if(progressDialog!=null) progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //myPrefences.storeLoginStatus(getApplicationContext(),"logged out");
      //  Toast.makeText(getApplicationContext(),"back presses",Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();
        } else if (id == R.id.qr_item) {
            Intent i = new Intent(getApplicationContext(), QRActivity.class);
            startActivity(i);

        } else if (id == R.id.social) {
            Intent i = new Intent(getApplicationContext(), ChatTabActivity.class);
            startActivity(i);
        } else if (id == R.id.student) {
            Intent i = new Intent(getApplicationContext(), CastleUtilities.class);
            startActivity(i);
        }
        else if (id == R.id.about) {
            Intent i = new Intent(getApplicationContext(), About.class);
            startActivity(i);
        }
        else if (id == R.id.hostel) {
            Intent i = new Intent(getApplicationContext(), ViewHostelsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {

        myPrefences.storeLoginStatus(getApplicationContext(),"logged out");
      // myPrefences.deletePreferences(getApplicationContext());
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.join_castle:
                 intent = new Intent(MainActivity.this, StudentRegistration.class);
                startActivity(intent);
                finish();
                break;
            case R.id.utilities_btn:
                intent = new Intent(MainActivity.this, CastleUtilities.class);
                startActivity(intent);
                break;
            case R.id.social_btn:
                 intent = new Intent(MainActivity.this, ChatTabActivity.class);
                startActivity(intent);
                break;
        }
    }
}
