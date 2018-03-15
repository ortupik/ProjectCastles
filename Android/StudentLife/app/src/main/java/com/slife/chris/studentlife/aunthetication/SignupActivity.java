package com.slife.chris.studentlife.aunthetication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SignupActivity extends AppCompatActivity {


    @Bind(R.id.input_phone)
    EditText _phoneNoText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.confirm_password)
    EditText _confirmPassword;
    @Bind(R.id.shimmer_tv) ShimmerTextView tv;

    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login)
    TextView linkLogin;
    @Bind(R.id.scrollView)
    ScrollView _scrollView;

    private Socket mSocket;
    private PreferencesClass myPrefences;
    private ProgressDialog progressDialog;
    private Shimmer shimmer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

       // img.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/tame.jpg"));
        //img.setImageURI(Uri.parse("/storage/emulated/0/imgd.jpg"));

        myPrefences = new PreferencesClass(SignupActivity.this);
        mSocket = SocketInstance.getSocket();
        mSocket.connect();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


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

    private Emitter.Listener onRegisterUser = new Emitter.Listener() {
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

                        switch (action) {
                            case "registerUser":
                                if (result == 1) {
                                    String user_id = data.getString("user_id");
                                    if (TextUtils.isEmpty(myPrefences.getUserId())) {
                                        myPrefences.storeUserId(user_id);
                                        myPrefences.storePhoneNo(phoneNo);
                                    }
                                    Toast.makeText(SignupActivity.this, "Registered successfully .. go to part 2 !", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignupActivity.this, SignupProfileActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else if (result == 0) {
                                    Snackbar.make(_scrollView, "Could not Register you at this time", Snackbar.LENGTH_LONG)
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_orange_dark)).setAction("OK", null).show();
                                   // Toast.makeText(FCMSignupActivity.this, "Could not Register you at this time", Toast.LENGTH_LONG).show();
                                    if(progressDialog!=null) progressDialog.dismiss();
                                    _signupButton.setEnabled(true);
                                } else if (result == 2) {
                                    String user_id = data.getString("user_id");
                                   // Toast.makeText(FCMSignupActivity.this, , Toast.LENGTH_LONG).show();
                                    Snackbar.make(_scrollView, "Please Complete Profile !!  ", Snackbar.LENGTH_LONG)
                                            .setAction("OK", null).show();
                                    if (TextUtils.isEmpty(myPrefences.getUserId())) {
                                        myPrefences.storeUserId(user_id);
                                        myPrefences.storePhoneNo(phoneNo);
                                    }
                                    Intent intent = new Intent(SignupActivity.this, SignupProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if (result == 3) {
                                    String message = data.getString("message");
                                    if(message != null){
                                        Snackbar.make(_scrollView, message, 7000)
                                                .setAction("OK", null).setActionTextColor(getResources().getColor(android.R.color.holo_orange_dark)).show();
                                    }
                                    if(progressDialog!=null) progressDialog.dismiss();
                                    _signupButton.setEnabled(true);

                                }
                                _signupButton.setEnabled(true);
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
    public void signup() {

        String password = _passwordText.getText().toString();
        String confPassword = _confirmPassword.getText().toString();

        if (!validate()) {
            onSignupFailed();
            return;
        }
        if(!password.equals(confPassword)){
           // Toast.makeText(getApplicationContext(),"Passwords dont match !!",Toast.LENGTH_LONG).show();
            Snackbar.make(_scrollView, "Passwords dont match !!", Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(android.R.color.holo_orange_dark))
                    .setAction("OK", null).show();
            onSignupFailed();
            return;
        }



         progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String phone = _phoneNoText.getText().toString();

        _signupButton.setEnabled(false);
        mSocket.on("registerUser", onRegisterUser);


        // actual login code
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("phoneNo", phone);
            credentials.put("password", password);
            mSocket.emit("registerUser", credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent i = new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
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

}