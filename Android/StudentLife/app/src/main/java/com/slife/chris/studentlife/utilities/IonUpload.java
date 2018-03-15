package com.slife.chris.studentlife.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;
import com.slife.chris.studentlife.MainActivity;

import java.io.File;

/**
 * Created by Chris on 11/29/2016.
 */

public class IonUpload {

    Future<Response<String>> uploading;
    private ProgressDialog progressDialog;
    private String destination;


    public void upload(final Activity context, String path, final String type) {

        if(type.equals("profile")){
            destination = "android_upload_profile/";
        } if(type.equals("images")){
            destination = "android_upload_images/";
        }

       /* progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading Profile...");
        progressDialog.show();*/

        File f = new File(path);

        uploading = Ion.with(context)
                .load(Constants.CHAT_SERVER_URL+"/"+destination)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {

                    }
                }).setMultipartParameter("folder","profile_images")
                .setMultipartFile("image", f)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            Toast.makeText(context ,"Error uploading image", Toast.LENGTH_LONG).show();
                         //   if(progressDialog!=null)progressDialog.dismiss();
                            return;
                        }
                      //  if(progressDialog!=null)progressDialog.dismiss();
                        Toast.makeText(context, "File upload complete", Toast.LENGTH_LONG).show();
                        if(type.equals("profile")) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            context.finish();


                        }
                    }
                });

    }
}
