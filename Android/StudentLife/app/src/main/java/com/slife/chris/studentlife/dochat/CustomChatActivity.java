/*
 * Copyright (c) 2016 Qiscus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slife.chris.studentlife.dochat;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.slife.chris.studentlife.R;



public class CustomChatActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private ImageView attachButton;
    private LinearLayout addPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.qiscus_primary_light));
        }

        setContentView(R.layout.activity_chat_dialog);

       // swipeRefreshLayout.setProgressViewOffset(false, 0, 128);

        final Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.qiscus_simple_grow);
        attachButton = (ImageView) findViewById(R.id.button_attach);
        addPanel = (LinearLayout) findViewById(R.id.add_panel);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addPanel.getVisibility() == View.GONE) {
                     addPanel.startAnimation(logoMoveAnimation);
                    addPanel.setVisibility(View.VISIBLE);
                } else {
                    addPanel.setVisibility(View.GONE);
                }
            }
        });

        showLoading();

    }


    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
        }
        progressDialog.setCancelable(false);
       // progressDialog.show();
    }

    public void dismissLoading() {
        progressDialog.dismiss();
    }
}
