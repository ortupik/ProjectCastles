package com.slife.chris.studentlife.aunthetication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.database.UserDb;
import com.slife.chris.studentlife.lecturer.LecturerMainActivity;
import com.slife.chris.studentlife.student.CastleUtilities;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {
    public static final int EXIT = 120;
    private static final String TAG = "PreferencesClass";
    private static final int REQUEST_SIGNUP = 0;
    @Bind(R.id.input_phone)
    EditText _phoneNoText;
    @Bind(R.id.input_password) EditText _passwordText;

    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    @Bind(R.id.shimmer_tv)
    ShimmerTextView tv;
    private Shimmer shimmer;

    private Socket mSocket;
    private PreferencesClass myPrefences;
    private ProgressDialog progressDialog;
    private String dept,classGroup,regNo;
    private String isStudentRegistered;

    private Emitter.Listener onloggedIn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject data = (JSONObject) args[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Toast.makeText(FCMLoginActivity.this, "" + data.toString(), Toast.LENGTH_LONG).show();
                    new Login().execute(data);
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        myPrefences = new PreferencesClass(LoginActivity.this);
        mSocket = SocketInstance.getSocket();
        mSocket.connect();

        mSocket.on("signInUser", onSignInUser);
        mSocket.on("login", onloggedIn);
        mSocket.on("checkStudentRegistration", onGetStudentRegistration);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
       // tv.setTypeface(Constants.getRobotoBold(getApplicationContext()));
        toggleAnimation(tv);

    }
    public void toggleAnimation(View target) {
        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.start(tv);
        }
    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        String phone = _phoneNoText.getText().toString();
        String password = _passwordText.getText().toString();
        // actual login code
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("phoneNo", phone);
            credentials.put("password", password);
            mSocket.emit("signInUser", credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private Emitter.Listener onSignInUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

                        String action = data.getString("action");
                        int result = data.getInt("result");
                        String phoneNo = _phoneNoText.getText().toString();
                        String userRole =  data.getString("user_role");


                        switch (action) {
                            case "signInUser":
                             if (result == 0) {
                                    Toast.makeText(LoginActivity.this, "Invalid Credentials !", Toast.LENGTH_SHORT).show();
                                } else if (result == 1) {
                                    String user_id = data.getString("user_id");
                                    if (TextUtils.isEmpty(myPrefences.getUserId())) {
                                        myPrefences.storeUserId(user_id);
                                        myPrefences.storePhoneNo(phoneNo);
                                        myPrefences.storeUserRole(userRole);
                                    }
                                    Toast.makeText(LoginActivity.this, "Welcome Back ! ", Toast.LENGTH_LONG).show();
                                    JSONObject credentials = new JSONObject();
                                    try {
                                        credentials.put("user_id", user_id);
                                        credentials.put("phoneNo",phoneNo);
                                        mSocket.emit("login", credentials);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else if(result == 2){
                                 Toast.makeText(LoginActivity.this, "Please Complete Profile ! ", Toast.LENGTH_SHORT).show();
                                 String user_id = data.getString("user_id");
                                 myPrefences.storeUserId(user_id);
                                 myPrefences.storePhoneNo(phoneNo);
                                 Intent intent = new Intent(getApplicationContext(),SignupProfileActivity.class);
                                 startActivity(intent);
                                 finish();
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };
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
                            dept = data.getString("dept");
                            classGroup = data.getString("class_group");
                            regNo = data.getString("regNo");

                            StudentDb studentDb = new StudentDb(getApplicationContext());
                            studentDb.open();
                            studentDb.createEntry(dept,classGroup,regNo);
                            studentDb.close();
                            isStudentRegistered = "student_yes";

                        }else if(data.getInt("success") == 0){
                            isStudentRegistered = "student_no";
                        }
                        Intent intent = new Intent(LoginActivity.this, CastleUtilities.class);
                        startActivity(intent);

                        onLoginSuccess();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {

                this.finish();
            }

    }

    @Override
    public void onBackPressed() {
        // Disable going back to the TableViewActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        myPrefences.storeLoginStatus(getApplicationContext(),"yes");
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String phone = _phoneNoText.getText().toString();

        String password = _passwordText.getText().toString();

        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches()) {
            _phoneNoText.setError("enter a valid phone number");
            valid = false;
        } else {
            _phoneNoText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    class Login extends AsyncTask<JSONObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
          //  _loginButton.setEnabled(false);
        }

        protected String doInBackground(JSONObject... args) {
            JSONObject loginData = args[0];

            UserDb userDbDb = new UserDb(LoginActivity.this);
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

            if(myPrefences.getUserRole().equals("Student")){
                myPrefences.storeRegistrationStatus(getApplicationContext(),"yes");
                JSONObject object = new JSONObject();
                try {
                    object.put("user_id",myPrefences.getUserId());
                    mSocket.emit("checkStudentRegistration",object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(myPrefences.getUserRole().equals("Lecturer")){
                Intent intent = new Intent(LoginActivity.this, LecturerMainActivity.class);
                startActivity(intent);
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"Unsupported User !!",Toast.LENGTH_LONG).show();
                    }
                });
            }


            return null;
        }


        protected void onPostExecute(String data) {


        }


    }

}
