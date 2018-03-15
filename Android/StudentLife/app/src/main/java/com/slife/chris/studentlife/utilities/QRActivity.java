package com.slife.chris.studentlife.utilities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.database.UserDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class QRActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, OnQRCodeReadListener {

  private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

  private ViewGroup mainLayout;

  private TextView resultTextView;
  private QRCodeReaderView qrCodeReaderView;
  private CheckBox flashlightCheckBox;
  private PointsOverlayView pointsOverlayView;
  private Socket mSocket;

  //USER DETAILS
  private  static final String KEY_USER_ID= "user_id";
  private  static final String KEY_PHONE ="phoneNo";
  private  static final String KEY_USERNAME ="username";
    private String phoneNo,userId,username,dept,classGroup;





  private static ArrayList<HashMap<String,String>> userData;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_qr);

    mainLayout = (ViewGroup) findViewById(R.id.main_layout);
    mSocket = SocketInstance.getSocket();
    mSocket.on("qrLogin",onQrLogin);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      initQRCodeReaderView();
    } else {
      requestCameraPermission();
    }
  }

  @Override protected void onResume() {
    super.onResume();

    if (qrCodeReaderView != null) {
      qrCodeReaderView.startCamera();
    }
  }

  @Override protected void onPause() {
    super.onPause();

    if (qrCodeReaderView != null) {
      qrCodeReaderView.stopCamera();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
      return;
    }

    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
      initQRCodeReaderView();
    } else {
      Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
          .show();
    }
  }

  // Called when a QR is decoded
  // "text" : the text encoded in QR
  // "points" : points where QR control points are placed
  @Override public void onQRCodeRead(String text, PointF[] points) {
    resultTextView.setText(R.string.qr_title);
    JSONObject credentials = new JSONObject();

    UserDb userDbDb = new UserDb(QRActivity.this);
    try {
      userDbDb.open();
      userData = userDbDb.getUserData();
      phoneNo =  userData.get(0).get(KEY_PHONE);
      userId =  userData.get(0).get(KEY_USER_ID);
      username =  userData.get(0).get(KEY_USERNAME);

      StudentDb studentDb = new StudentDb(getApplicationContext());
        studentDb.open();
        ArrayList<HashMap<String,String>> studentData = studentDb.getStudentData();
        dept = studentData.get(0).get("dept");
        classGroup = studentData.get(0).get("classGroup");

      credentials.put("socket_id",text);
      credentials.put("user_id",userId);
      credentials.put("username",username);
      credentials.put("phoneNo",phoneNo);
      credentials.put("dept",dept);
      credentials.put("class_group",classGroup);

      studentDb.close();

      mSocket.emit("qrLogin",credentials);

      userDbDb.close();

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    pointsOverlayView.setPoints(points);
  }
  private Emitter.Listener onQrLogin = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          JSONObject data = (JSONObject) args[0];
            Toast.makeText(QRActivity.this, "Logged you in Castle Web ! ", Toast.LENGTH_LONG).show();
            finish();
        }
      });
    }
  };

  private void requestCameraPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
      Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
          Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
        @Override public void onClick(View view) {
          ActivityCompat.requestPermissions(QRActivity.this,
              new String[] { Manifest.permission.CAMERA }, MY_PERMISSION_REQUEST_CAMERA);
        }
      }).show();
    } else {
      Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
          Snackbar.LENGTH_SHORT).show();
      ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
          MY_PERMISSION_REQUEST_CAMERA);
    }
  }

  private void initQRCodeReaderView() {
    View content = getLayoutInflater().inflate(R.layout.content_qr, mainLayout, true);

    qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
    resultTextView = (TextView) content.findViewById(R.id.result_text_view);
    flashlightCheckBox = (CheckBox) content.findViewById(R.id.flashlight_checkbox);
    pointsOverlayView = (PointsOverlayView) content.findViewById(R.id.points_overlay_view);

    qrCodeReaderView.setAutofocusInterval(2000L);
    qrCodeReaderView.setOnQRCodeReadListener(this);
    flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        qrCodeReaderView.setTorchEnabled(isChecked);
      }
    });

    qrCodeReaderView.setBackCamera();
    qrCodeReaderView.startCamera();
  }

}