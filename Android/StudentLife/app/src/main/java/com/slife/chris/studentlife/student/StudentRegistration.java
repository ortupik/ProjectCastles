package com.slife.chris.studentlife.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.slife.chris.studentlife.MainActivity;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.SQLException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class StudentRegistration extends AppCompatActivity {

    @Bind(R.id.dept_spinner)Spinner deptSpinner;
    @Bind(R.id.group_spinner) Spinner groupSpinner;
    @Bind(R.id.input_reg_no) EditText regNoEt;
    @Bind(R.id.btn_signup_student) AppCompatButton signUpButton;
    private static ProgressDialog progressDialog,progressDialog2;

    private Socket mSocket;
    private PreferencesClass myPrefences;
    private String dept,classGroup,regNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        myPrefences = new PreferencesClass(StudentRegistration.this);
        mSocket = SocketInstance.getSocket();
        mSocket.connect();

        mSocket.emit("getDepts");
        mSocket.on("getDepts",onGetDepts);

        progressDialog2 = new ProgressDialog(StudentRegistration.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Initializing...");
        progressDialog2.show();



        mSocket.on("studentRegistration", onStudentRegistration);

         signUpButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 registerStudent();
             }
         });


    }
    private void registerStudent(){

         dept = deptSpinner.getSelectedItem().toString();
         classGroup = groupSpinner.getSelectedItem().toString();
         regNo = regNoEt.getText().toString();
       if(validate()){
           JSONObject credentials = new JSONObject();
           try {
               credentials.put("phoneNo", myPrefences.getPhoneNo());
               credentials.put("regNo", regNoEt.getText().toString());
               credentials.put("user_id", myPrefences.getUserId());
               credentials.put("castle_id", 3);
               credentials.put("dept",dept);
               credentials.put("class_group",classGroup);

               mSocket.emit("studentRegistration", credentials);
               showLoading();
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }

    }


    private Boolean validate() {
        String regNo = regNoEt.getText().toString();
        if (regNo.isEmpty() || regNo.length() < 4 || regNo.length() > 17) {
            regNoEt.setError("Invalid Registration number");
            return false;
        } else {
            regNoEt.setError(null);
            return true;
        }
    }
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Linking StudentDb...");
        }
       // progressDialog.setCancelable(false);
         progressDialog.show();
    }

    public void dismissLoading() {
        progressDialog.dismiss();
    }

    private Emitter.Listener onGetDepts = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Object json = null;
                    try {
                        json = new JSONTokener(args[0].toString()).nextValue();

                        if (json instanceof JSONArray) {
                            JSONArray data = (JSONArray) args[0];

                            System.out.println("From on depts " + data);


                            ArrayList<String> groups = new ArrayList<>();
                            for(int i = 0; i < data.length();i++){
                                String group = data.getString(i);
                                groups.add(group);
                            }

                            ArrayList<String> departments = new ArrayList<>();
                            departments.add("CS");
                            departments.add("IT");
                            departments.add("BIT");
                            ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(StudentRegistration.this,
                                    android.R.layout.simple_spinner_item, departments);
                            deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            deptSpinner.setAdapter(deptAdapter);

                            if(departments.size() > 0)
                              deptSpinner.setSelection(0);

                            ArrayAdapter<String> groupsAdapter = new ArrayAdapter<String>(StudentRegistration.this,
                                    android.R.layout.simple_spinner_item, groups);
                            groupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            groupSpinner.setAdapter(groupsAdapter);
                            groupSpinner.setSelection(0);



                        } else {
                           Toast.makeText(getApplicationContext(),"No Courses",Toast.LENGTH_LONG).show();
                        }
                        if(progressDialog2!=null) progressDialog2.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });

        }

        ;
    };

    private Emitter.Listener onStudentRegistration = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject data = (JSONObject) args[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissLoading();

                    try {
                        if(data.getInt("success") == 1){
                            if (TextUtils.isEmpty(myPrefences.getStudentRegStatus())) {
                                myPrefences.storeStudentRegStatus("success");
                            }

                            StudentDb studentDb = new StudentDb(getApplicationContext());
                            studentDb.open();
                            studentDb.createEntry(dept,classGroup,regNo);
                            studentDb.close();

                            Toast.makeText(StudentRegistration.this,  "Succesfully Registered as Student", Toast.LENGTH_LONG).show();

                            Intent i = new Intent(StudentRegistration.this,MainActivity.class);
                            i.putExtra("fromRegistration","student_yes");
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(StudentRegistration.this,  "Ensure  your correct registration number !!", Toast.LENGTH_LONG).show();
                        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
