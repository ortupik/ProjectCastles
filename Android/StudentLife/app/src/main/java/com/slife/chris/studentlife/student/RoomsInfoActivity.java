package com.slife.chris.studentlife.student;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.SocketInstance;

import io.socket.client.Socket;

public class RoomsInfoActivity extends AppCompatActivity {

    private Socket mSocket;
    private String room,dept,classGroup;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

       /* Bundle b = getIntent().getExtras();
        room = b.getString("room");
        dept = b.getString("dept");
        classGroup = b.getString("group");*/

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
           toolbar.setTitle("");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");

        handleSocket();
    }
    private void handleSocket() {
        mSocket = SocketInstance.getSocket();
        mSocket.connect();
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
