package com.slife.chris.studentlife;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.slife.chris.studentlife.aunthetication.Login;
import com.slife.chris.studentlife.aunthetication.LoginActivity;
import com.slife.chris.studentlife.chats.ChatsFragment;
import com.slife.chris.studentlife.contacts.ContactsFragment;
import com.slife.chris.studentlife.utilities.PreferencesClass;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.emitter.Emitter;


public class ChatTabActivity extends AppCompatActivity {

    private PreferencesClass myPrefences;
    private TextView usernameTv,phoneTv;
    private CircleImageView profileImage;
    //USER DETAILS
    private  static final String KEY_USERNAME= "username";
    private  static final String KEY_PHONE ="phoneNo";
    private  static final String KEY_PROFILE_PHOTO = "profile_photo";

    private String phoneNo,imgPath,username;
    private static ArrayList<HashMap<String,String>> userData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_tab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myPrefences = new PreferencesClass(ChatTabActivity.this);

        if (TextUtils.isEmpty(myPrefences.getUserId())) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }




        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        UserDb userDbDb = new UserDb(getApplicationContext());
        try {
            userDbDb.open();
            userData = userDbDb.getUserData();
            phoneNo =  userData.get(0).get(KEY_PHONE);
            username =  userData.get(0).get(KEY_USERNAME);
            imgPath =  userData.get(0).get(KEY_PROFILE_PHOTO);

            usernameTv.setText(username);
            phoneTv.setText(phoneNo);
            Glide.with(profileImage.getContext())
                    .load(Constants.IMG_URL+imgPath)
                    .fitCenter()
                    .into(profileImage);

            userDbDb.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
*/

        handleBottomBar();
    }

    private void handleBottomBar() {

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Fragment fragment = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (tabId == R.id.tab_chats) {
                    fragment = new ChatsFragment();
                }else if(tabId == R.id.tab_contacts){
                    fragment = new ContactsFragment();
                }
                if(fragment !=null){
                    fragmentManager.beginTransaction()
                            .replace(R.id.contentContainer, fragment)
                            .commit();
                }
            }
        });
        Snackbar.make(bottomBar,"Caution,this section is highly experimental !", 5000)
                .setAction("OK", null).show();

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
            }
        });
    }
    private void logout() {
        myPrefences.deletePreferences(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
