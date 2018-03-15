package com.slife.chris.studentlife.aunthetication;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.database.UserDb;
import com.slife.chris.studentlife.utilities.ImageCompressionAsyncTask;
import com.slife.chris.studentlife.utilities.ImageManipulate;
import com.slife.chris.studentlife.utilities.IonUpload;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SignupProfileActivity extends AppCompatActivity {
    public static final int CAMERA_IMAGE = 200;
    public static final int GALLERY_IMAGE = 100;
    public static final int EXIT = 120;
    private String filepath;
    private String encodedImg = "";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_status) EditText _statusText;
    //@Bind(R.id.btnSelect) Button _selectButton;
    @Bind(R.id.btn_signup) Button _signupButton;
    //@Bind(R.id.btnCamera) Button _cameraButton;
    @Bind(R.id.imgAvatar) CircleImageView img;
    @Bind(R.id.linearLayoutPick)LinearLayout linearLayoutPictureFrame;


    private Socket mSocket;
    private PreferencesClass myPrefences;
    private ProgressDialog progressDialog,progressDialog2;
    private String phoneNo;
    private String dept,classGroup,regNo;
    private String isStudentRegistered;
    private Bitmap bitmap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_profile);
        ButterKnife.bind(this);

        myPrefences = new PreferencesClass(SignupProfileActivity.this);
        mSocket = SocketInstance.getSocket();
        mSocket.connect();

        mSocket.on("login", onloggedIn);
        mSocket.on("registerUserProfile", onRegisterUserProfile);
        mSocket.on("checkStudentRegistration", onGetStudentRegistration);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/*");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
        linearLayoutPictureFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/*");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
            }
        });

        /*_cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_IMAGE);
            }
        });*/


    }


    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

       // _signupButton.setEnabled(false);

         progressDialog = new ProgressDialog(SignupProfileActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Profile...");
       progressDialog.show();

        String name = _nameText.getText().toString();
        String status = _statusText.getText().toString();


       String filename = new File(filepath).getName();
        String fileExt = filename.substring(filename.length()-3,filename.length());
        // actual login code
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("username", name);
            credentials.put("phoneNo", myPrefences.getPhoneNo());
            credentials.put("user_id", myPrefences.getUserId());
            credentials.put("status", status);
            credentials.put("profile_photo",filename);
            mSocket.emit("registerUserProfile", credentials);
            new IonUpload().upload(SignupProfileActivity.this, filepath,"profile");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private Emitter.Listener onGetStudentRegistration = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject data = (JSONObject) args[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if(data.getInt("success") == 1){
                            if (TextUtils.isEmpty(myPrefences.getStudentRegStatus())) {
                                myPrefences.storeStudentRegStatus("success");
                            }
                            StudentDb studentDb = new StudentDb(getApplicationContext());
                            studentDb.open();
                            studentDb.createEntry(dept,classGroup,regNo);
                            studentDb.close();
                            isStudentRegistered = "student_yes";

                        }else if(data.getInt("success") == 0){
                            isStudentRegistered = "student_no";
                        }


                        onSignupSuccess();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onloggedIn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject data = (JSONObject) args[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //  Toast.makeText(SignupProfileActivity.this, "" + data.toString(), Toast.LENGTH_LONG).show();
                    new Login().execute(data);
                }
            });
        }
    };

    private Emitter.Listener onRegisterUserProfile = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

                        int result = data.getInt("success");

                         if (result == 0) {
                            Toast.makeText(SignupProfileActivity.this, "Could not Register you at this time", Toast.LENGTH_SHORT).show();
                        } else if (result == 1) {
                            String user_id = data.getString("user_id");
                            if (TextUtils.isEmpty(myPrefences.getUserId())) {
                                myPrefences.storeUserId(user_id);
                            }
                            Toast.makeText(SignupProfileActivity.this, "Completed Registration Successfully  ! ", Toast.LENGTH_SHORT).show();
                            JSONObject credentials = new JSONObject();
                            try {
                                credentials.put("user_id", user_id);
                                credentials.put("phoneNo", myPrefences.getPhoneNo());
                                mSocket.emit("login", credentials);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        myPrefences.storeRegistrationStatus(getApplicationContext(),"yes");
        myPrefences.storeLoginStatus(getApplicationContext(),"yes");
        //finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String status = _statusText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (status.isEmpty()) {
            _statusText.setError("Enter Valid Status");
            valid = false;
        } else {
            _statusText.setError(null);
        }
        if(filepath == null || filepath.equals("")){
              Toast.makeText(getApplicationContext(),"Select/Capture Image",Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;



        switch (requestCode) {
            case GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    filepath = getPathFromURI(uri);
                    Log.d("DEBUG", "Choose: " + filepath);

                    //compresses the image
                    ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                        @Override
                        protected void onPostExecute(byte[] imageBytes) {
                            // image here is compressed & ready to be sent to the server
                        }
                    };
                    imageCompression.execute(filepath);// imagePath as a string

                    //   Toast.makeText(getApplicationContext(), filepath, Toast.LENGTH_SHORT).show();
                    //img.setImageURI(data.getData());
                    Glide.with(this).load(uri).into(img);
                  /*  try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
                break;
            case CAMERA_IMAGE:
                if (resultCode == RESULT_OK) {
                    bitmap = (Bitmap)data.getExtras().get("data");
                   // img.setImageBitmap(bitmap);
                    Uri uri  = new ImageManipulate().getImageUri(getApplicationContext(),bitmap);
                    Glide.with(this).load(uri).into(img);
                    filepath = getPathFromURI(uri);
                    ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                        @Override
                        protected void onPostExecute(byte[] imageBytes) {
                            // image here is compressed & ready to be sent to the server
                        }
                    };
                    imageCompression.execute(filepath);// imagePath as a string

                }
                break;

           }
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    class Login extends AsyncTask<JSONObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog2 = new ProgressDialog(SignupProfileActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog2.setIndeterminate(true);
            progressDialog2.setMessage("Sign in ...");
            progressDialog2.show();
           // _signupButton.setEnabled(false);
        }

        protected String doInBackground(JSONObject... args) {
            JSONObject loginData = args[0];

            UserDb userDbDb = new UserDb(SignupProfileActivity.this);
            try {

                String username = loginData.getString("username");
                String status = loginData.getString("status");
                String phoneNo = loginData.getString("phoneNo");
                String profile_photo = loginData.getString("profile_photo");
                String user_id = myPrefences.getUserId();

                userDbDb.open();
                userDbDb.createEntry(user_id, username, phoneNo, status, profile_photo);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            userDbDb.close();

            return null;
        }


        protected void onPostExecute(String data) {

          //if(progressDialog2!=null)  progressDialog2.dismiss();
            onSignupSuccess();
            mSocket.emit("checkStudentRegistration",myPrefences.getUserId());
        }


    }

}