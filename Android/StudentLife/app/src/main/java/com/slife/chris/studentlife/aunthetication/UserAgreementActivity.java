package com.slife.chris.studentlife.aunthetication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.slife.chris.studentlife.MainActivity;
import com.slife.chris.studentlife.R;

public class UserAgreementActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView agreementTv = (TextView) findViewById(R.id.user_agreement_textview);

        TextView readTv = (TextView) findViewById(R.id.read_terms);
        readTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreementTv.setVisibility(View.VISIBLE);
            }
        });
        AppCompatButton _proceedBtn = (AppCompatButton) findViewById(R.id.btn_proceed) ;
        _proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
