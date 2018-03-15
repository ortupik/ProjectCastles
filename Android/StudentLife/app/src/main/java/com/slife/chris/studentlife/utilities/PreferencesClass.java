package com.slife.chris.studentlife.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Chris on 9/27/2016.
 */

public class PreferencesClass {

    private static final String TAG = "PreferencesClass";
    private Context context;

    public PreferencesClass(Context context){
        this.context = context;
    }

    public String getUserId() {

        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        String user_id = prefs.getString("user_id", "");

        if (user_id.isEmpty()) {
            Log.i(TAG, "user_id not found.");
            return "";
        }
        return user_id;
    }
    public String getUserRole() {

        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        String user_role = prefs.getString("user_role", "");

        if (user_role.isEmpty()) {
            Log.i(TAG, "user_role not found.");
            return "";
        }
        return user_role;
    }
    public String getLecturerId() {

        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        String lecturer_id = prefs.getString("lecturer_id", "");

        if (lecturer_id.isEmpty()) {
            Log.i(TAG, "lecturer_id not found.");
            return "";
        }
        return lecturer_id;
    }
    public String getStudentRegStatus() {

        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        String isReg = prefs.getString("studentRegStatus", "");

        if (isReg.isEmpty()) {
            Log.i(TAG, "student not registered.");
            return "";
        }
        return isReg;
    }
    public void storeStudentRegStatus(String studentRegStatus) {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving studentRegStatus" + studentRegStatus);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("studentRegStatus", studentRegStatus);
        editor.commit();
    }

    public void storeUserId(String user_id) {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving user_id" + user_id);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_id", user_id);
        editor.commit();
    }
    public void storeUserRole(String user_role) {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving user_role" + user_role);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_role", user_role);
        editor.commit();
    }
    public void storeLecturerId(String lecturer_id) {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving lecturer_id" + lecturer_id);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lecturer_id", lecturer_id);
        editor.commit();
    }

    public void storePhoneNo(String phoneNo) {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving phoneNo" + phoneNo);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneNo", phoneNo);
        editor.commit();
    }

    public String getPhoneNo() {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        String phoneNo = prefs.getString("phoneNo", "");

        if (phoneNo.isEmpty()) {
            Log.i(TAG, "phoneNo not found.");
            return "";
        }
        return phoneNo;
    }
    public  void deletePreferences(Context context){
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
    }

    public void storeRegistrationStatus(Context applicationContext, String regStatus) {
        final SharedPreferences prefs = applicationContext.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving regStatus" + regStatus);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("regStatus", regStatus);
        editor.commit();
    }
    public String getRegistrationStatus() {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        String regStatus = prefs.getString("regStatus", "");

        if (regStatus.isEmpty()) {
            Log.i(TAG, "regStatus not found.");
            return "";
        }
        return regStatus;
    }
    public void storeLoginStatus(Context applicationContext, String loginStatus) {
        final SharedPreferences prefs = applicationContext.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving loginStatus" + loginStatus);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("loginStatus", loginStatus);
        editor.commit();
    }
    public String getLoginStatus() {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        String regStatus = prefs.getString("loginStatus", "");

        if (regStatus.isEmpty()) {
            Log.i(TAG, "loginStatus not found.");
            return "";
        }
        return regStatus;
    }
    public void storeCastlePop(Context applicationContext, int castlePop) {
        final SharedPreferences prefs = applicationContext.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving castlePop" + castlePop);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("castlePop", castlePop);
        editor.commit();
    }
    public void storeGroupPop(Context applicationContext, int groupPop) {
        final SharedPreferences prefs = applicationContext.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        Log.i(TAG, "Saving groupPop" + groupPop);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("groupPop", groupPop);
        editor.commit();
    }
    public int getGroupPop() {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        int groupPop = prefs.getInt("groupPop", 0);

        if (groupPop == 0) {
            Log.i(TAG, "groupPop not found.");
            return 0;
        }
        return groupPop;
    }
    public int getCastlePop() {
        final SharedPreferences prefs = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
        int castlePop = prefs.getInt("castlePop", 0);

        if (castlePop == 0) {
            Log.i(TAG, "castlePop not found.");
            return 0;
        }
        return castlePop;
    }
}
