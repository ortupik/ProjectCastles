/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slife.chris.studentlife.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.ContactsDb;
import com.slife.chris.studentlife.dochat.ChatDialogActivity;
import com.slife.chris.studentlife.utilities.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserDetailActivity extends AppCompatActivity {

    //user details
    public  static final String KEY_USER_ID = "user_id";
    public  static final String KEY_USERNAME = "username";
    public  static final String KEY_STATUS = "status";
    public  static final String KEY_PHONE_NO = "phoneNo";
    public  static final String KEY_PROFILE_PHOTO = "profile_photo";
    private static ArrayList<HashMap<String,String>> userData;

    private String username,status,phoneNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Intent intent = getIntent();
        final String user_id = intent.getStringExtra(KEY_USER_ID);


        ContactsDb contactsDb = new ContactsDb(UserDetailActivity.this);
        try {
            contactsDb.open();
            userData = contactsDb.getUserData(user_id);
             username = userData.get(0).get(KEY_USERNAME);
             phoneNo =  userData.get(0).get(KEY_PHONE_NO);
             status =  userData.get(0).get(KEY_STATUS);
            contactsDb.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(username);

        TextView phoneTv = (TextView) findViewById(R.id.profile_phone);
        phoneTv.setText(phoneNo);
        TextView statusTv = (TextView) findViewById(R.id.profile_status);
        statusTv.setText(status);

        FloatingActionButton goToChats = (FloatingActionButton) findViewById(R.id.goToChat);
        goToChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("user_id",user_id);

                Intent intent = new Intent(UserDetailActivity.this, ChatDialogActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        loadBackdrop();
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setImageURI(Uri.parse(Constants.IMG_URL+userData.get(0).get(KEY_PROFILE_PHOTO)));
       Glide.with(this).load(Constants.IMG_URL+userData.get(0).get(KEY_PROFILE_PHOTO))
               .into(imageView);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
